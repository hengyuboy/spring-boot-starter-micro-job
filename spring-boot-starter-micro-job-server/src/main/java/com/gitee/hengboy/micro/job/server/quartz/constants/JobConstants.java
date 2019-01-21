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
package com.gitee.hengboy.micro.job.server.quartz.constants;

import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeExecuteDetailRecord;
import com.gitee.hengboy.micro.job.server.strategy.model.Node;
import com.weibo.api.motan.config.RefererConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 任务常量
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-15
 * Time：15:37
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public interface JobConstants {
    /**
     * 任务执行并行队列
     */
    BlockingQueue<JobNodeExecuteDetailRecord> JOB_EXECUTE_QUEUE = new LinkedBlockingQueue();
    /**
     * 任务回收并行队列
     */
    BlockingQueue<JobNodeExecuteDetailRecord> JOB_RECOVERY_RETRY_QUEUE = new LinkedBlockingQueue();
    /**
     * 节点任务远程执行缓存
     * key：节点编号
     * value：远程执行RPC实例
     */
    Map<Integer, RefererConfig> JOB_CLUSTER_EXECUTE_CACHE = new HashMap();
    /**
     * 触发器与节点的绑定信息
     * key：任务触发器key
     * value
     * key: 节点地址，如：127.0.0.1:8080
     * value: 节点对象信息
     */
    ConcurrentMap<String, ConcurrentMap<String, Node>> TRIGGER_NODE_BIND = new ConcurrentHashMap();

}
