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
package com.gitee.hengboy.micro.job.server.strategy.support;

import com.gitee.hengboy.micro.job.common.exception.JobException;
import com.gitee.hengboy.micro.job.server.service.NodeLoadBalanceService;
import com.gitee.hengboy.micro.job.server.strategy.LbStrategy;
import com.gitee.hengboy.micro.job.server.strategy.model.Node;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ip hash负载均衡策略
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-16
 * Time：15:10
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class IpHashStrategy implements LbStrategy {
    /**
     * 任务执行节点集合
     */
    private SortedMap<Integer, Node> nodes = new TreeMap();

    /**
     * 查询命中的任务执行节点
     *
     * @param jobTriggerKey 任务触发器key
     * @return
     * @throws JobException
     */
    @Override
    public Node select(String jobTriggerKey) throws JobException {

        // 如果该触发器不存在执行的节点，则直接返回null
        ConcurrentMap<String, Node> nodeMap = NodeLoadBalanceService.getLoadBalanceNode(jobTriggerKey);
        if (nodeMap == null || nodeMap.size() == 0) {
            return null;
        }

        // 初始化每个任务执行节点的hash值
        nodeMap.values().stream().forEach(node -> this.nodes.put(hash(node.getNodeAddress()), node));

        // 获取当前任务触发器key的hash值
        int hash = hash(jobTriggerKey);
        // 如果任务触发器key的hash值大于任务执行节点的最大hash值，返回所有任务节点
        // 相反，返回大于任务触发器key的hash值的任务节点
        SortedMap<Integer, Node> nodeSortedMap = hash >= nodes.lastKey() ? nodes.tailMap(0) : nodes.tailMap(hash);
        // 如果不存在任务执行节点
        if (nodeSortedMap.isEmpty()) {
            return null;
        }
        // 返回第一个任务执行节点
        return nodeSortedMap.get(nodeSortedMap.firstKey());
    }

    /**
     * 计算字符串的hash值
     *
     * @param str
     * @return
     */
    private static int hash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

}
