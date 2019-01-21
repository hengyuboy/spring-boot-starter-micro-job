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
package com.gitee.hengboy.micro.job.node.runnable;

import com.gitee.hengboy.micro.job.common.annotation.Job;
import com.gitee.hengboy.micro.job.common.exception.JobException;
import com.gitee.hengboy.micro.job.common.model.JobNode;
import com.gitee.hengboy.micro.job.common.model.JobNodeTrigger;
import com.gitee.hengboy.micro.job.common.service.HeartSyncService;
import com.gitee.hengboy.micro.job.common.tools.ClassTools;
import com.gitee.hengboy.micro.job.common.tools.InetAddressTools;
import com.gitee.hengboy.micro.job.common.tools.StringTools;
import com.gitee.hengboy.micro.job.common.trigger.JobTrigger;
import com.gitee.hengboy.micro.job.node.configuration.properties.JobNodeProperties;
import com.gitee.hengboy.micro.job.node.constants.NodeConstants;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * 心跳检查runnable
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-08
 * Time：16:24
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
public class HeartSyncRunnable implements Runnable {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(HeartSyncRunnable.class);

    /**
     * 心跳检查
     */
    private HeartSyncService heartSyncService;
    /**
     * job node config properties
     */
    private JobNodeProperties jobNodeProperties;
    /**
     * 扫描job实现类的根package
     */
    private String scanJobBasePackage;

    /**
     * 心跳检查执行逻辑
     * - 上报客户端信息到服务端
     * - 上报时携带客户端触发器列表
     */
    @Override
    public void run() {
        try {
            // 配置节点信息
            JobNode node = new JobNode();

            // 节点IP地址
            node.setIpAddress(InetAddressTools.getLocalIp());
            // 节点端口号
            node.setPort(jobNodeProperties.getLocalPort());
            // 格式化后的地址，ip:port
            node.setNodeAddress(InetAddressTools.formatterAddress(node.getIpAddress(), node.getPort()));
            // 心跳检查时上报的触发器列表
            node.setTriggers(getJobTrigger());

            logger.info("Job node：[{}] execute heart sync", node.getNodeAddress());

            // 执行发送心跳检查到服务端
            heartSyncService.sync(node);
        } catch (Exception e) {
            logger.error("Abnormal heartbeat detection", e);
        }
    }

    /**
     * 检查相关参数
     *
     * @param job 任务对象
     */
    private void checkParam(Job job, String jobKey) {
        if (job.autoStart() && StringUtils.isEmpty(job.cron())) {
            throw new JobException("Job：" + jobKey + " , Automatic startup requires configuration of cron parameters.");
        }
    }

    /**
     * 缓存任务执行触发器
     * key:任务触发器key
     * value:任务执行时的类
     *
     * @param jobKey   触发器key
     * @param jobClass 任务触发器类型
     */
    private void cacheExecuteJob(String jobKey, Class<? extends JobTrigger> jobClass) {
        // 写入缓存，用于执行本地任务
        NodeConstants.CLUSTER_EXECUTE_SERVICE.put(jobKey, jobClass);
    }

    /**
     * 获取任务节点的所有触发器
     *
     * @return
     */
    private List<JobNodeTrigger> getJobTrigger() {
        // 从缓存中读取绑定的触发器列表
        if (NodeConstants.NODE_TRIGGERS.size() > 0) {
            return NodeConstants.NODE_TRIGGERS;
        }

        // 获取节点内所有实现JobTrigger接口的实现类
        Set<Class<? extends JobTrigger>> jobs = ClassTools.getJobs(scanJobBasePackage);
        jobs.stream().forEach(
                jobClass -> {
                    // 配置的注解信息
                    Job job = jobClass.getAnnotation(Job.class);
                    if (job == null) {
                        throw new JobException("Trigger [" + jobClass.getName() + "] is not configured with annotations");
                    }

                    // 任务key
                    String jobKey = job.jobKey();
                    if (StringUtils.isEmpty(jobKey)) {
                        // 首字母小写的类名作为默认的jobKey
                        jobKey = StringTools.toLowerCaseFirstOne(jobClass.getSimpleName());
                    }
                    // 检查相关参数
                    checkParam(job, jobKey);

                    // 上报触发器信息
                    JobNodeTrigger reportTrigger = new JobNodeTrigger();
                    reportTrigger.setAutoStart(job.autoStart());
                    reportTrigger.setCron(job.cron());
                    reportTrigger.setStrategy(job.strategy());
                    reportTrigger.setWeight(job.weight());
                    reportTrigger.setTriggerKey(jobKey);

                    // 写入到缓存
                    NodeConstants.NODE_TRIGGERS.add(reportTrigger);

                    // 缓存执行信息
                    cacheExecuteJob(jobKey, jobClass);
                }
        );
        return NodeConstants.NODE_TRIGGERS;
    }
}
