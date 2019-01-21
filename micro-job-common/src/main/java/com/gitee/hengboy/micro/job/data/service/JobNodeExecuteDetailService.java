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

import com.gitee.hengboy.micro.job.data.model.tables.JobNodeExecuteDetail;
import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeExecuteDetailRecord;
import com.gitee.hengboy.micro.job.data.service.base.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static com.gitee.hengboy.micro.job.data.model.Tables.JOB_NODE_EXECUTE_DETAIL;

/**
 * 任务执行详情
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-15
 * Time：14:55
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Transactional(rollbackFor = Exception.class)
public class JobNodeExecuteDetailService extends BaseService {
    /**
     * 查询任务执行记录
     *
     * @param executeStatus 任务状态 详见JobExecuteStatusEnum
     * @param limit         查询条件
     * @return
     */
    public List<JobNodeExecuteDetailRecord> select(String executeStatus, int limit) {
        return dslContext.selectFrom(JOB_NODE_EXECUTE_DETAIL)
                .where(JobNodeExecuteDetail.NED_STATUS.eq(executeStatus))
                .orderBy(JobNodeExecuteDetail.NED_ID.asc())
                .limit(limit)
                .fetch().into(JobNodeExecuteDetailRecord.class);
    }

    /**
     * 根据指定状态查询全部的任务
     *
     * @param executeStatus 执行状态
     * @return
     */
    public List<JobNodeExecuteDetailRecord> selectAll(String executeStatus) {
        return dslContext.selectFrom(JOB_NODE_EXECUTE_DETAIL)
                .where(JobNodeExecuteDetail.NED_STATUS.eq(executeStatus))
                .orderBy(JobNodeExecuteDetail.NED_ID.asc())
                .fetch().into(JobNodeExecuteDetailRecord.class);
    }

    /**
     * 任务执行保存
     *
     * @param param 执行参数
     */
    public JobNodeExecuteDetailRecord save(String param, String lbStrategy, String triggerKey) {
        return dslContext.insertInto(JOB_NODE_EXECUTE_DETAIL)
                .set(JobNodeExecuteDetail.NED_PARAM, param)
                .set(JobNodeExecuteDetail.NED_LD_STRATEGY, lbStrategy)
                .set(JobNodeExecuteDetail.NED_TRIGGER_KEY, triggerKey)
                .returning()
                .fetchOne();
    }

    /**
     * 执行更新状态
     *
     * @param executeId     执行详情编号
     * @param executeStatus 执行详情状态
     */
    public void updateStatus(Integer executeId, String executeStatus) {
        dslContext.update(JOB_NODE_EXECUTE_DETAIL)
                .set(JobNodeExecuteDetail.NED_STATUS, executeStatus)
                .where(JobNodeExecuteDetail.NED_ID.eq(executeId))
                .execute();
    }

    /**
     * 更新执行成功时间
     *
     * @param executeId 执行详情编号
     */
    public void updateSuccessTime(Integer executeId) {
        dslContext.update(JOB_NODE_EXECUTE_DETAIL)
                .set(JobNodeExecuteDetail.NED_SUCCESS_TIME, new Timestamp(System.currentTimeMillis()))
                .where(JobNodeExecuteDetail.NED_ID.eq(executeId))
                .execute();
    }

    /**
     * 更新执行节点的编号
     *
     * @param executeId      执行详情编号
     * @param jobNodeAddress 执行任务节点的地址
     */
    public void updateExecuteNode(Integer executeId, String jobNodeAddress) {
        dslContext.update(JOB_NODE_EXECUTE_DETAIL)
                .set(JobNodeExecuteDetail.NED_NODE_ADDRESS, jobNodeAddress)
                .where(JobNodeExecuteDetail.NED_ID.eq(executeId))
                .execute();
    }

    /**
     * 更新重试次数
     *
     * @param executeId         执行详情编号
     * @param currentRetryCount 当前重试次数
     */
    public void updateRetryCount(Integer executeId, Integer currentRetryCount) {
        dslContext.update(JOB_NODE_EXECUTE_DETAIL)
                .set(JobNodeExecuteDetail.NED_RETRY_COUNT, currentRetryCount)
                .where(JobNodeExecuteDetail.NED_ID.eq(executeId))
                .execute();
    }
}
