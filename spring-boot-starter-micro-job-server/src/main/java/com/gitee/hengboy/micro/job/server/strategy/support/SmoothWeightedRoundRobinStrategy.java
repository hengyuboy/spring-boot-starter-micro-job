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

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 平滑轮询权重策略
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-16
 * Time：14:50
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class SmoothWeightedRoundRobinStrategy implements LbStrategy {
    /**
     * 参与负载的节点列表
     */
    private ConcurrentMap<String, Node> nodes;
    /**
     * 重入锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 获取本次负载均衡处理后的任务执行节点
     *
     * @param jobTriggerKey 任务触发器key
     * @return
     * @throws JobException
     */
    @Override
    public Node select(String jobTriggerKey) throws JobException {
        // 如果该触发器不存在执行的节点，则直接返回null
        this.nodes = NodeLoadBalanceService.getLoadBalanceNode(jobTriggerKey);
        if (nodes == null || nodes.size() == 0) {
            return null;
        }
        try {
            lock.lock();
            return this.getCurrentNode();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取当前负载均衡后的节点
     *
     * @return
     */
    private Node getCurrentNode() {
        int totalWeight = 0;
        Node maxNode = null;
        int maxWeight = 0;
        Iterator<String> iterator = nodes.keySet().iterator();
        while (iterator.hasNext()) {
            Node n = nodes.get(iterator.next());
            totalWeight += n.getWeight();

            // 每个节点的当前权重要加上原始的权重
            n.setCurrentWeight(n.getCurrentWeight() + n.getWeight());

            // 保存当前权重最大的节点
            if (maxNode == null || maxWeight < n.getCurrentWeight()) {
                maxNode = n;
                maxWeight = n.getCurrentWeight();
            }
        }
        // 被选中的节点权重减掉总权重
        maxNode.setCurrentWeight(maxNode.getCurrentWeight() - totalWeight);
        return maxNode;
    }
}
