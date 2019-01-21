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

import com.gitee.hengboy.micro.job.data.model.tables.JobNodeInfo;
import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeInfoRecord;
import com.gitee.hengboy.micro.job.data.service.base.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static com.gitee.hengboy.micro.job.data.model.Tables.JOB_NODE_INFO;

/**
 * 任务节点基本信息
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-11
 * Time：10:45
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Transactional(rollbackFor = Exception.class)
public class JobNodeInfoService extends BaseService {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(JobNodeInfoService.class);

    /**
     * 查询心跳检查过期的节点编号集合
     *
     * @return 过期节点的编号集合
     */
    public List<Integer> selectTimeOutNodes(int heartCheckOverTime) {
        return dslContext.selectFrom(JOB_NODE_INFO)
                .where(JobNodeInfo.JNI_LAST_HEART_TIME.lt(new Timestamp(System.currentTimeMillis() - (heartCheckOverTime * 1000))))
                .fetch(JobNodeInfo.JNI_ID);
    }

    /**
     * 根据节点路径查询或者添加
     * 如果存在节点时直接返回
     * 如果不存在节点时执行保存
     *
     * @param nodeAddress 节点地址
     * @return
     */
    public JobNodeInfoRecord selectOrInsert(String nodeAddress) {
        JobNodeInfoRecord jobNodeInfoRecord = selectByAddress(nodeAddress);
        if (jobNodeInfoRecord == null) {
            jobNodeInfoRecord = insert(nodeAddress);
            logger.info("job node [{}]，bind success.", nodeAddress);
        }
        return jobNodeInfoRecord;
    }

    /**
     * 根据节点地址查询节点信息
     *
     * @param nodeAddress 节点地址
     */
    public JobNodeInfoRecord selectByAddress(String nodeAddress) {
        return dslContext
                .selectFrom(JOB_NODE_INFO)
                .where(JobNodeInfo.JNI_ADDRESS.eq(nodeAddress))
                .orderBy(JobNodeInfo.JNI_CREATE_TIME.desc())
                .limit(1)
                .fetchOne();
    }

    /**
     * 更新最后心跳时间
     *
     * @param nodeAddress 节点地址
     */
    public void updateLastHeartTime(String nodeAddress) {
        // 当前时间
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        dslContext.update(JOB_NODE_INFO)
                .set(JobNodeInfo.JNI_LAST_HEART_TIME, currentTime)
                .where(JobNodeInfo.JNI_ADDRESS.eq(nodeAddress))
                .execute();
    }

    /**
     * 任务节点保存
     *
     * @param nodeAddress 任务节点地址，如：127.0.0.1:8080格式
     */
    public JobNodeInfoRecord insert(String nodeAddress) {
        // 当前时间
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return dslContext
                .insertInto(JOB_NODE_INFO)
                .set(JobNodeInfo.JNI_ADDRESS, nodeAddress)
                .set(JobNodeInfo.JNI_CREATE_TIME, currentTime)
                .set(JobNodeInfo.JNI_LAST_HEART_TIME, currentTime)
                .returning()
                .fetchOne();
    }

    /**
     * 根据节点编号删除节点信息
     *
     * @param nodeId 节点编号
     */
    public void deleteById(Integer nodeId) {
        dslContext.deleteFrom(JOB_NODE_INFO)
                .where(JobNodeInfo.JNI_ID.eq(nodeId))
                .execute();
    }

    /**
     * 根据节点主键查询节点信息
     *
     * @param nodeId 节点编号
     * @return
     */
    public JobNodeInfoRecord selectById(Integer nodeId) {
        return dslContext
                .selectFrom(JOB_NODE_INFO)
                .where(JobNodeInfo.JNI_ID.eq(nodeId))
                .fetchOne();
    }
}
