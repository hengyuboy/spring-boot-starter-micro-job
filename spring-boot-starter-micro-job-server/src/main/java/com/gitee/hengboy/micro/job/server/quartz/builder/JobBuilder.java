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
package com.gitee.hengboy.micro.job.server.quartz.builder;

import com.gitee.hengboy.micro.job.common.constants.QuartzJobConstants;
import lombok.Builder;
import lombok.Data;
import org.quartz.*;
import org.springframework.util.StringUtils;

/**
 * quartz job 生成工具类
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-14
 * Time：13:30
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class JobBuilder {

    /**
     * 创建新的任务
     *
     * @param scheduler quartz 调度中心
     * @param jobBean   新任务的信息
     */
    public static void newJob(Scheduler scheduler, JobBean jobBean, String jsonParamData) throws SchedulerException {
        // 任务key
        JobKey jobKey = JobKey.jobKey(jobBean.getName(), jobBean.getGroup());
        // 任务处理
        JobDetail jobDetail = org.quartz.JobBuilder.newJob(jobBean.getJobExecuteClass()).withIdentity(jobKey).build();
        // 参数处理
        if (!StringUtils.isEmpty(jsonParamData)) {
            jobDetail.getJobDataMap().put(QuartzJobConstants.NODE_JOB_PARAM, jsonParamData);
        }
        // 触发器key
        TriggerKey triggerKey = TriggerKey.triggerKey(jobBean.getName(), jobBean.getGroup());
        // 触发器信息
        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(CronScheduleBuilder.cronSchedule(jobBean.getCron()));

        scheduler.scheduleJob(jobDetail, triggerBuilder.build());
    }

    /**
     * 任务实体信息
     * 用于配置job的基本信息
     */
    @Data
    @Builder
    public static class JobBean {
        private String cron;
        private Class jobExecuteClass;
        private String name;
        private String group;

        public String getGroup() {
            // 分组为空时，使用执行类的类名
            if (StringUtils.isEmpty(group)) {
                return jobExecuteClass.getName();
            }
            return group;
        }
    }
}
