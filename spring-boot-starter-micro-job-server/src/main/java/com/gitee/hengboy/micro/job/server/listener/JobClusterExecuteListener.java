/*
 * Copyright 2019 恒宇少年
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.gitee.hengboy.micro.job.server.listener;

import com.gitee.hengboy.micro.job.common.enums.JobExecuteStatusEnum;
import com.gitee.hengboy.micro.job.common.enums.LoadBalanceStrategy;
import com.gitee.hengboy.micro.job.common.model.JobExecuteParam;
import com.gitee.hengboy.micro.job.common.model.JobExecuteResult;
import com.gitee.hengboy.micro.job.common.motan.dynamic.client.DynamicClient;
import com.gitee.hengboy.micro.job.common.service.JobClusterExecuteService;
import com.gitee.hengboy.micro.job.common.tools.InetAddressTools;
import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeExecuteDetailRecord;
import com.gitee.hengboy.micro.job.data.service.JobNodeExecuteDetailService;
import com.gitee.hengboy.micro.job.server.configuration.properties.JobServerProperties;
import com.gitee.hengboy.micro.job.server.event.JobClusterExecuteEvent;
import com.gitee.hengboy.micro.job.server.quartz.constants.JobConstants;
import com.gitee.hengboy.micro.job.server.quartz.execute.JobExecuteService;
import com.gitee.hengboy.micro.job.server.strategy.LbStrategy;
import com.gitee.hengboy.micro.job.server.strategy.StrategyFactory;
import com.gitee.hengboy.micro.job.server.strategy.model.Node;
import com.weibo.api.motan.config.RefererConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

/**
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-15
 * Time：15:56
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class JobClusterExecuteListener implements SmartApplicationListener {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(JobClusterExecuteListener.class);

    /**
     * 任务执行详情
     */
    @Autowired
    private JobNodeExecuteDetailService jobNodeExecuteDetailService;
    /**
     * job server config properties
     */
    @Autowired
    private JobServerProperties jobServerProperties;
    /**
     * 任务执行操作
     */
    @Autowired
    private JobExecuteService jobExecuteService;

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
        return JobClusterExecuteEvent.class == aClass;
    }

    /**
     * 执行任务远程处理
     *
     * @param applicationEvent
     */
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        // 转换事件
        JobClusterExecuteEvent jobClusterExecuteEvent = (JobClusterExecuteEvent) applicationEvent;
        // 任务执行详情
        JobNodeExecuteDetailRecord executeDetailRecord = jobClusterExecuteEvent.getExecuteDetail();

        try {
            // 获取负载方式的实现
            LbStrategy lbStrategy = StrategyFactory.getStrategy(LoadBalanceStrategy.valueOf(executeDetailRecord.getNedLdStrategy()));
            // 不同负载方式获取到的负载任务节点不同
            Node loadBalanceNode = lbStrategy.select(executeDetailRecord.getNedTriggerKey());
            if (loadBalanceNode != null) {
                logger.info("Use Node：[{}] execute job [{}]", loadBalanceNode.getNodeAddress(), executeDetailRecord.getNedTriggerKey());
                // 远程执行实例
                JobClusterExecuteService jobClusterExecuteService = getClusterExecuteService(loadBalanceNode);
                // rpc远程执行任务
                JobExecuteResult jobExecuteResult = jobClusterExecuteService.execute(new JobExecuteParam(executeDetailRecord.getNedTriggerKey(), executeDetailRecord.getNedParam()));
                // 任务执行完后逻辑
                // 1. 回收
                // 2. 成功
                // 3. 更新执行详情
                jobExecuteAfter(executeDetailRecord, jobExecuteResult, loadBalanceNode);
            }
            // 更新状态为重试
            else {
                jobNodeExecuteDetailService.updateStatus(executeDetailRecord.getNedId(), JobExecuteStatusEnum.RETRY.toString());
                // 任务回收，放入重试队列
                jobRecovery(executeDetailRecord);
            }
        } catch (Exception e) {
            logger.error("Execute job ：" + executeDetailRecord.getNedTriggerKey() + "error", e);
            jobRecovery(executeDetailRecord);
        }
    }

    /**
     * 任务回收
     *
     * @param executeDetailRecord
     */
    private void jobRecovery(JobNodeExecuteDetailRecord executeDetailRecord) {
        try {
            JobConstants.JOB_RECOVERY_RETRY_QUEUE.put(executeDetailRecord);
            if (executeDetailRecord.getNedRetryCount() < jobServerProperties.getRetryTimes()) {
                logger.warn("Job：[{}] recovery completion", executeDetailRecord.getNedId());
            } else {
                logger.error("Job：[{}] upper limit of retries , recovery ignore", executeDetailRecord.getNedId());
            }
        } catch (InterruptedException ex) {
            logger.info("Job：[{}] recovery error", executeDetailRecord.getNedTriggerKey());
            ex.printStackTrace();
        }
    }

    /**
     * 获取客户端远程执行任务RPC实例
     *
     * @param loadBalanceNode 负载任务节点信息
     * @return
     */
    private JobClusterExecuteService getClusterExecuteService(Node loadBalanceNode) {
        // 从缓存内读取
        RefererConfig refererConfig = JobConstants.JOB_CLUSTER_EXECUTE_CACHE.get(loadBalanceNode.getNodeId());
        if (refererConfig == null && loadBalanceNode != null) {
            // 分割负载节点地址
            // index zero = ip address
            // index one = ports
            String[] address = InetAddressTools.splitNodeAddress(loadBalanceNode.getNodeAddress());

            // 执行动态创建motan客户端
            refererConfig = DynamicClient.getInstance(
                    DynamicClient.DynamicClientConfig.getDefaultConfig()
                            .setAddress(address[0])
                            .setPort(Integer.valueOf(address[1])));
            // 写入缓存
            JobConstants.JOB_CLUSTER_EXECUTE_CACHE.put(loadBalanceNode.getNodeId(), refererConfig);
        }
        return (JobClusterExecuteService) refererConfig.getRef();
    }

    /**
     * 远程任务执行完成后
     * 1. 回收
     * 2. 成功
     * 3. 更新执行详情
     *
     * @param jobExecuteResult    远程执行结果
     * @param executeDetailRecord 任务执行详情
     */
    private void jobExecuteAfter(JobNodeExecuteDetailRecord executeDetailRecord, JobExecuteResult jobExecuteResult, Node loadBalanceNode) {
        // 任务执行状态，默认成功
        JobExecuteStatusEnum jobExecuteStatusEnum = JobExecuteStatusEnum.SUCCESS;
        switch (jobExecuteResult.getCode()) {
            case SUCCESS:
                // 更新成功时间
                jobNodeExecuteDetailService.updateSuccessTime(executeDetailRecord.getNedId());
                break;
            case ERROR:
            case RETRY:
                // 更新状态为重试
                jobExecuteStatusEnum = JobExecuteStatusEnum.RETRY;
                break;
            // 删除执行的任务
            case REMOVE:
                jobExecuteService.removeJob(executeDetailRecord.getNedTriggerKey());
                break;
            default:
                // 更新状态为重试
                jobExecuteStatusEnum = JobExecuteStatusEnum.RETRY;
                break;
        }
        // 放入重试队列
        if (JobExecuteStatusEnum.RETRY.equals(jobExecuteStatusEnum)) {
            // 任务回收
            jobRecovery(executeDetailRecord);
        }
        // 更新状态
        jobNodeExecuteDetailService.updateStatus(executeDetailRecord.getNedId(), jobExecuteStatusEnum.toString());

        jobNodeExecuteDetailService.updateExecuteNode(executeDetailRecord.getNedId(), loadBalanceNode.getNodeAddress());
    }
}
