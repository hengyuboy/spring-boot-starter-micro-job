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

import com.gitee.hengboy.micro.job.common.model.JobNode;
import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeInfoRecord;
import com.gitee.hengboy.micro.job.data.service.JobNodeInfoService;
import com.gitee.hengboy.micro.job.data.service.JobNodeTriggerService;
import com.gitee.hengboy.micro.job.server.event.HeartSyncEvent;
import com.gitee.hengboy.micro.job.server.quartz.execute.JobExecuteService;
import com.gitee.hengboy.micro.job.server.service.NodeLoadBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

import java.util.concurrent.ConcurrentMap;

/**
 * 上报节点触发器后处理的监听任务
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-15
 * Time：13:26
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class HeartSyncSaveNodeTriggerListener implements SmartApplicationListener {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(HeartSyncSaveNodeTriggerListener.class);

    /**
     * 任务执行
     */
    @Autowired
    private JobExecuteService jobExecuteService;
    /**
     * 任务触发器
     */
    @Autowired
    private JobNodeTriggerService jobNodeTriggerService;
    /**
     * 任务节点信息
     */
    @Autowired
    private JobNodeInfoService jobNodeInfoService;


    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
        return aClass == HeartSyncEvent.class;
    }

    /**
     * 执行顺序：1
     * 在更新节点心跳信息后执行
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 1;
    }

    /**
     * 根据节点上报的信息进行后续处理，如：
     * - 任务自动启动
     *
     * @param applicationEvent
     */
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        // 转换事件
        HeartSyncEvent heartSyncEvent = (HeartSyncEvent) applicationEvent;
        // 上报的节点实例
        JobNode jobNode = heartSyncEvent.getJobNode();

        // 执行保存节点的触发器信息
        if (jobNode.getTriggers() != null && jobNode.getTriggers().size() > 0) {

            // 任务基本信息
            JobNodeInfoRecord jobNodeInfoRecord = jobNodeInfoService.selectOrInsert(jobNode.getNodeAddress());

            jobNode.getTriggers()
                    .stream()
                    .forEach(trigger -> {
                        // 从缓存内查询该触发器key所绑定的执行服务节点
                        ConcurrentMap bingNodeMap = NodeLoadBalanceService.getLoadBalanceNode(trigger.getTriggerKey());
                        // 如果存在缓存信息不执行再次绑定操作
                        if (bingNodeMap == null || !bingNodeMap.containsKey(jobNodeInfoRecord.getJniAddress())) {
                            try {

                                // 保存触发器 & 返回触发器主键自增的值
                                jobNodeTriggerService.saveOrUpdate(trigger);

                                // 缓存负载均衡节点信息
                                NodeLoadBalanceService.cacheLoadBalanceNode(jobNodeInfoRecord.getJniId(), jobNodeInfoRecord.getJniAddress(), trigger);

                                // 判断是否需要自启动
                                if (trigger.isAutoStart() && !jobExecuteService.exist(trigger.getTriggerKey())) {
                                    logger.debug("Task node [{}:{}], trigger: {}, execute self-startup.", jobNode.getIpAddress(), jobNode.getPort(), trigger.getTriggerKey());
                                    jobExecuteService.startJob(trigger.getTriggerKey(), null);
                                }
                                // 判断任务已经存在时
                                else if (jobExecuteService.exist(trigger.getTriggerKey())) {
                                    // 如果本次上报的autoStart为false，执行删除任务
                                    if (!trigger.isAutoStart()) {
                                        jobExecuteService.removeJob(trigger.getTriggerKey());
                                    }
                                }
                            }
                            // 遇到异常时清空缓存信息
                            catch (Exception e) {
                                NodeLoadBalanceService.removeLoadBalanceNode(jobNodeInfoRecord.getJniAddress());
                            }
                        }
                    });
            logger.debug("Reception node [{}:{}] report job trigger", jobNode.getIpAddress(), jobNode.getPort());
        }
    }
}
