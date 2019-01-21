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
 * 随机权重负载策略
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-16
 * Time：15:09
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class RandomWeightedStrategy implements LbStrategy {
    /**
     * 参与负载的节点列表
     */
    private TreeMap<Double, Node> nodes = new TreeMap();

    /**
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
        nodeMap.values().stream().forEach(
                node -> {
                    double lastWeight = this.nodes.size() == 0 ? 0 : this.nodes.lastKey().doubleValue();
                    this.nodes.put(node.getWeight() + lastWeight, node);
                }
        );
        Double randomWeight = this.nodes.lastKey() * Math.random();
        SortedMap<Double, Node> tailMap = this.nodes.tailMap(randomWeight, false);
        return this.nodes.get(tailMap.firstKey());
    }
}
