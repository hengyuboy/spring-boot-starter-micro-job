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
package com.gitee.hengboy.micro.job.data.service;

import com.gitee.hengboy.micro.job.common.model.JobNodeTrigger;
import com.gitee.hengboy.micro.job.data.model.tables.JobNodeTriggerInfo;
import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeTriggerInfoRecord;
import com.gitee.hengboy.micro.job.data.service.base.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static com.gitee.hengboy.micro.job.data.model.Tables.JOB_NODE_TRIGGER_INFO;

/**
 * 任务节点触发器
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-12
 * Time：16:36
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Transactional(rollbackFor = Exception.class)
public class JobNodeTriggerService extends BaseService {
    /**
     * 保存节点触发器
     * 如果不存在该触发器，执行添加
     * 如果已经存在该出发，执行更新
     *
     * @param reportTrigger 节点上报的触发器实例
     */
    public void saveOrUpdate(JobNodeTrigger reportTrigger) {
        // 根据key 查询
        JobNodeTriggerInfoRecord jobNodeTriggerInfoRecord = selectByKey(reportTrigger.getTriggerKey());
        // 执行添加
        if (jobNodeTriggerInfoRecord == null) {
            dslContext.insertInto(JOB_NODE_TRIGGER_INFO)
                    .set(JobNodeTriggerInfo.NTI_KEY, reportTrigger.getTriggerKey())
                    .set(JobNodeTriggerInfo.NTI_LB_STRATEGY, reportTrigger.getStrategy().toString())
                    .set(JobNodeTriggerInfo.NTI_NAME, reportTrigger.getName())
                    .set(JobNodeTriggerInfo.NTI_CRON, reportTrigger.getCron())
                    .set(JobNodeTriggerInfo.NTI_CREATE_TIME, new Timestamp(System.currentTimeMillis()))
                    .execute();
        }
        // 执行更新
        else {
            dslContext.update(JOB_NODE_TRIGGER_INFO)
                    .set(JobNodeTriggerInfo.NTI_LB_STRATEGY, reportTrigger.getStrategy().toString())
                    .set(JobNodeTriggerInfo.NTI_NAME, reportTrigger.getName())
                    .set(JobNodeTriggerInfo.NTI_CRON, reportTrigger.getCron())
                    .where(JobNodeTriggerInfo.NTI_KEY.eq(reportTrigger.getTriggerKey()))
                    .execute();
        }
    }

    /**
     * 根据指定的key查询触发器信息
     *
     * @param triggerKey 触发器key
     * @return
     */
    public JobNodeTriggerInfoRecord selectByKey(String triggerKey) {
        return dslContext.selectFrom(JOB_NODE_TRIGGER_INFO)
                .where(JobNodeTriggerInfo.NTI_KEY.eq(triggerKey))
                .fetchOne();
    }

    /**
     * 更新cron表达式
     *
     * @param triggerKey 任务key
     * @param cron       表达式
     */
    public void updateCron(String triggerKey, String cron) {
        dslContext.update(JOB_NODE_TRIGGER_INFO)
                .set(JobNodeTriggerInfo.NTI_CRON, cron)
                .where(JobNodeTriggerInfo.NTI_KEY.eq(triggerKey))
                .execute();
    }

    /**
     * 根据触发器主键查询
     *
     * @param triggerId 触发器主键
     * @return
     */
    public JobNodeTriggerInfoRecord selectById(Integer triggerId) {
        return dslContext.selectFrom(JOB_NODE_TRIGGER_INFO)
                .where(JobNodeTriggerInfo.NTI_ID.eq(triggerId))
                .fetchOne();
    }

    /**
     * 查询全部的节点触发器
     *
     * @return
     */
    public List<JobNodeTriggerInfoRecord> selectAll() {
        return dslContext.selectFrom(JOB_NODE_TRIGGER_INFO)
                .fetch().into(JobNodeTriggerInfoRecord.class);
    }
}
