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

import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeExecuteDetailRecord;
import com.gitee.hengboy.micro.job.server.event.JobClusterExecuteEvent;
import com.gitee.hengboy.micro.job.server.quartz.constants.JobConstants;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * 加载执行的任务
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-15
 * Time：15:24
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteJobRunnable implements Runnable {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(ExecuteJobRunnable.class);
    /**
     * spring 上下文
     */
    private ApplicationContext applicationContext;

    /**
     * 每次执行读取10条
     */
    @Override
    public void run() {
        while (true) {
            try {
                logger.debug("Current queue job count：{}", JobConstants.JOB_EXECUTE_QUEUE.size());
                // 从队列内取出一个任务
                JobNodeExecuteDetailRecord executeDetailRecord = JobConstants.JOB_EXECUTE_QUEUE.take();
                if (executeDetailRecord != null) {
                    // 发布远程执行任务事件
                    applicationContext.publishEvent(new JobClusterExecuteEvent(this, executeDetailRecord));
                }
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    logger.error("task from job queue error");
                }
                e.printStackTrace();
            }
        }
    }
}
