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
import com.gitee.hengboy.micro.job.server.quartz.constants.JobConstants;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载等待执行或者重试的任务从数据库内
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-18
 * Time：11:40
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
public class LoadWaitExecuteJobRunnable implements Runnable {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(LoadWaitExecuteJobRunnable.class);
    /**
     * 任务执行详情s
     */
    @Autowired
    private JobNodeExecuteDetailService jobNodeExecuteDetailService;

    /**
     * 从数据库内加载全部状态为WAIT|RETRY的任务
     * 写入重试队列内
     */
    @Override
    public void run() {
        List<JobNodeExecuteDetailRecord> jobs = new ArrayList();
        // 等待执行的状态
        jobs.addAll(jobNodeExecuteDetailService.selectAll(JobExecuteStatusEnum.WAIT.toString()));
        // 重试状态s
        jobs.addAll(jobNodeExecuteDetailService.selectAll(JobExecuteStatusEnum.RETRY.toString()));

        // 写入重试队列
        jobs.stream().forEach(job -> {
            try {
                JobConstants.JOB_RECOVERY_RETRY_QUEUE.put(job);
            } catch (InterruptedException ex) {
                logger.info("Job：[{}] recovery error", job.getNedTriggerKey());
                ex.printStackTrace();
            }
        });
    }
}
