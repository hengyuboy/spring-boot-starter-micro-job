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
package com.gitee.hengboy.micro.job.server.quartz.bean;

import com.gitee.hengboy.micro.job.common.constants.QuartzJobConstants;
import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeExecuteDetailRecord;
import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeTriggerInfoRecord;
import com.gitee.hengboy.micro.job.data.service.JobNodeExecuteDetailService;
import com.gitee.hengboy.micro.job.data.service.JobNodeTriggerService;
import com.gitee.hengboy.micro.job.server.quartz.constants.JobConstants;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 任务分发执行quartz实例
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-14
 * Time：11:10
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class JobExecuteBean extends QuartzJobBean {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(JobExecuteBean.class);

    /**
     * 任务执行详情
     */
    @Autowired
    private JobNodeExecuteDetailService jobNodeExecuteDetailService;
    /**
     * 任务触发器
     */
    @Autowired
    private JobNodeTriggerService jobNodeTriggerService;

    /**
     * 执行任务分发
     * - 将任务写入数据库与内存分别一份
     * <p>
     * 为什么写入内存？
     * 项目内执行进行操作内存，内存执行完成后，同步更新数据库的状态，为了提高执行效率，执行准确性
     *
     * @param jobExecutionContext 任务执行上下文
     * @throws JobExecutionException
     */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 任务key
        JobKey jobKey = jobExecutionContext.getJobDetail().getKey();

        // 任务触发基本信息
        JobNodeTriggerInfoRecord jobNodeTriggerInfoRecord = jobNodeTriggerService.selectByKey(jobKey.getName());

        // 保存任务执行信息
        if (jobNodeTriggerInfoRecord != null) {
            // 执行参数
            Object param = jobExecutionContext.getJobDetail().getJobDataMap().get(QuartzJobConstants.NODE_JOB_PARAM);
            // 保存任务执行信息并返回任务对象
            JobNodeExecuteDetailRecord record = jobNodeExecuteDetailService.save(
                    param == null ? null : String.valueOf(param),
                    jobNodeTriggerInfoRecord.getNtiLbStrategy(),
                    jobKey.getName()
            );
            try {
                // 写入任务执行队列内
                JobConstants.JOB_EXECUTE_QUEUE.put(record);
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    logger.error("Job：[{}] Put to job queue error", jobKey.getName());
                }
                e.printStackTrace();
            }
        }
    }
}
