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
package com.gitee.hengboy.micro.job.server.service;

import com.gitee.hengboy.micro.job.common.model.JobNodeTrigger;
import com.gitee.hengboy.micro.job.server.quartz.constants.JobConstants;
import com.gitee.hengboy.micro.job.server.strategy.model.Node;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 节点负载均衡
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-17
 * Time：10:38
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class NodeLoadBalanceService {


    /**
     * 查询负载均衡节点
     *
     * @param jobTriggerKey 任务触发器key
     * @return
     */
    public static ConcurrentMap<String, Node> getLoadBalanceNode(String jobTriggerKey) {
        return JobConstants.TRIGGER_NODE_BIND.get(jobTriggerKey);
    }

    /**
     * 缓存任务触发器与节点的绑定信息
     *
     * @param nodeId  节点编号
     * @param trigger 任务触发器
     */
    public static void cacheLoadBalanceNode(Integer nodeId, String nodeAddress, JobNodeTrigger trigger) {
        // 查询是否已经缓存了该触发器的节点信息
        ConcurrentMap<String, Node> nodes = JobConstants.TRIGGER_NODE_BIND.get(trigger.getTriggerKey());
        if (nodes == null) {
            nodes = new ConcurrentHashMap(1);
        }
        // 初始化负载节点信息
        nodes.put(nodeAddress, new Node(nodeId, nodeAddress, trigger.getWeight()));
        // 写入内存集合
        JobConstants.TRIGGER_NODE_BIND.put(trigger.getTriggerKey(), nodes);
    }

    /**
     * 缓存任务触发器与节点的绑定信息
     *
     * @param triggerKey 触发器key
     * @param nodes      节点列表
     */
    public static void cacheLoadBalanceNode(String triggerKey, ConcurrentMap<String, Node> nodes) {
        JobConstants.TRIGGER_NODE_BIND.put(triggerKey, nodes);
    }

    /**
     * 根据节点编号剔除负载均衡缓存绑定节点
     *
     * @param nodeAddress 任务节点地址
     */
    public static void removeLoadBalanceNode(String nodeAddress) {
        JobConstants.TRIGGER_NODE_BIND.values().stream().forEach(bind -> bind.remove(nodeAddress));
    }
}
