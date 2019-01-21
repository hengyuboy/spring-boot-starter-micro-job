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
package com.gitee.hengboy.micro.job.server.runnable;

import com.gitee.hengboy.micro.job.common.enums.JobExecuteStatusEnum;
import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeExecuteDetailRecord;
import com.gitee.hengboy.micro.job.data.service.JobNodeExecuteDetailService;
import com.gitee.hengboy.micro.job.server.event.JobClusterExecuteEvent;
import com.gitee.hengboy.micro.job.server.quartz.constants.JobConstants;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ConcurrentMap;

/**
 * 重试执行任务线程
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-16
 * Time：11:45
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
public class RetryExecuteJobRunnable implements Runnable {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(RetryExecuteJobRunnable.class);
    /**
     * application context
     */
    private ApplicationContext applicationContext;
    /**
     * 任务执行详情
     */
    private JobNodeExecuteDetailService jobNodeExecuteDetailService;
    /**
     * 配置的重试次数
     */
    private int configRetryMaxCount;

    /**
     * 任务重试
     */
    @Override
    public void run() {
        while (true) {
            try {
                logger.debug("Current retry queue job count：{}", JobConstants.JOB_RECOVERY_RETRY_QUEUE.size());
                // 从队列内取出一个任务
                JobNodeExecuteDetailRecord executeDetailRecord = JobConstants.JOB_RECOVERY_RETRY_QUEUE.take();
                if (executeDetailRecord != null) {
                    // 绑定的节点集合
                    ConcurrentMap bindNodes = JobConstants.TRIGGER_NODE_BIND.get(executeDetailRecord.getNedTriggerKey());
                    // 重试次数内
                    // 不存在执行节点，放入重试执行队列
                    if (executeDetailRecord.getNedRetryCount() < configRetryMaxCount || bindNodes == null || bindNodes.size() == 0) {
                        JobConstants.JOB_RECOVERY_RETRY_QUEUE.put(executeDetailRecord);
                        continue;
                    }
                    // 重试次数内执行 & 存在触发器执行节点
                    if (executeDetailRecord.getNedRetryCount() < configRetryMaxCount && bindNodes != null && bindNodes.size() > 0) {
                        // 设置重试次数
                        executeDetailRecord.setNedRetryCount(executeDetailRecord.getNedRetryCount() + 1);

                        logger.warn("Job：[{}]，{} retry", executeDetailRecord.getNedId(), executeDetailRecord.getNedRetryCount());
                        // 发布远程执行任务事件
                        applicationContext.publishEvent(new JobClusterExecuteEvent(this, executeDetailRecord));
                    }
                    // 超过次数 & 存在执行节点
                    // 更新执行信息为ERROR
                    else if (executeDetailRecord.getNedRetryCount() >= configRetryMaxCount && bindNodes != null && bindNodes.size() > 0) {
                        jobNodeExecuteDetailService.updateStatus(executeDetailRecord.getNedId(), JobExecuteStatusEnum.ERROR.toString());
                    }
                    // 更新重试次数
                    jobNodeExecuteDetailService.updateRetryCount(executeDetailRecord.getNedId(), executeDetailRecord.getNedRetryCount());
                }
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    logger.error("task from job retry queue error");
                }
                e.printStackTrace();
            }
        }
    }
}
