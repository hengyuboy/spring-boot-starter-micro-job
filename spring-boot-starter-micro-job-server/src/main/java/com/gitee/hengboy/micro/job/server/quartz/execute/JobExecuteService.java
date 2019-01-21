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
package com.gitee.hengboy.micro.job.server.quartz.execute;

import com.alibaba.fastjson.JSON;
import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeTriggerInfoRecord;
import com.gitee.hengboy.micro.job.data.service.JobNodeTriggerService;
import com.gitee.hengboy.micro.job.server.quartz.bean.JobExecuteBean;
import com.gitee.hengboy.micro.job.server.quartz.builder.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 任务操作
 * - 任务启动
 * - 任务删除
 * - 任务暂停
 * - 验证任务是否存在
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-14
 * Time：14:03
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class JobExecuteService {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(JobExecuteService.class);
    /**
     * 任务触发器
     */
    @Autowired
    private JobNodeTriggerService jobNodeTriggerService;
    /**
     * quartz调度中心
     */
    @Autowired
    private Scheduler scheduler;

    /**
     * 启动任务
     * 1. 根据任务的key查询任务信息
     * 2. 根据任务信息创建quartz执行任务实例
     *
     * @param triggerKey 触发器key
     */
    public void startJob(String triggerKey, Object param) {
        try {
            // 查询触发信息
            JobNodeTriggerInfoRecord jobNodeTriggerInfoRecord = jobNodeTriggerService.selectByKey(triggerKey);
            JobBuilder.newJob(scheduler,
                    JobBuilder.JobBean.builder()
                            .jobExecuteClass(JobExecuteBean.class)
                            .cron(jobNodeTriggerInfoRecord.getNtiCron())
                            .name(triggerKey)
                            .build(),
                    param != null ? JSON.toJSONString(param) : ""
            );
        } catch (Exception e) {
            logger.error("启动任务：{}，遇到异常信息如下.", triggerKey);
            e.printStackTrace();
        }
    }

    /**
     * 使用指定的分组删除任务
     *
     * @param triggerKey 任务key
     */
    public void removeJob(String triggerKey, String groupName) {
        try {
            JobKey jobKey = JobKey.jobKey(triggerKey, groupName);
            scheduler.deleteJob(jobKey);
        } catch (Exception e) {
            logger.error("删除任务：{}，遇到异常信息如下.", triggerKey);
            e.printStackTrace();
        }
    }

    /**
     * 使用默认的分组删除任务
     *
     * @param triggerKey 任务key
     */
    public void removeJob(String triggerKey) {
        removeJob(triggerKey, JobExecuteBean.class.getName());
    }

    /**
     * 使用指定分组暂停任务
     *
     * @param triggerKey 任务key
     * @param groupName  任务分组
     */
    public void pauseJob(String triggerKey, String groupName) {
        try {
            JobKey jobKey = JobKey.jobKey(triggerKey, groupName);
            scheduler.pauseJob(jobKey);
        } catch (Exception e) {
            logger.error("暂停任务：{}，遇到异常信息如下.", triggerKey);
            e.printStackTrace();
        }
    }

    /**
     * 使用默认的分组暂停任务
     *
     * @param triggerKey 任务key
     */
    public void pauseJob(String triggerKey) {
        pauseJob(triggerKey, JobExecuteBean.class.getName());
    }

    /**
     * 根据指定分组名称查询任务否存在
     *
     * @param triggerKey 任务key
     * @param groupName  所属分组
     * @return
     */
    public boolean exist(String triggerKey, String groupName) {
        boolean exist = false;
        try {
            JobKey jobKey = JobKey.jobKey(triggerKey, groupName);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail != null) {
                exist = true;
            }
        } catch (Exception e) {
            logger.error("暂停任务：{}，遇到异常信息如下.", triggerKey);
            e.printStackTrace();
        }
        return exist;
    }

    /**
     * 使用默认分组名称查询任务是否存在
     *
     * @param triggerKey 任务key
     * @return
     */
    public boolean exist(String triggerKey) {
        return exist(triggerKey, JobExecuteBean.class.getName());
    }
}
