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
package com.gitee.hengboy.micro.job.server.strategy.model;

import lombok.Data;

/**
 * 负载均衡节点
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-16
 * Time：14:45
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Data
public class Node {
    /**
     * 初始权重 （保持不变）
     */
    private final int weight;
    /**
     * 节点
     */
    private final int nodeId;
    /**
     * 节点地址
     */
    private final String nodeAddress;
    /**
     * 当前权重
     */
    private int currentWeight;

    /**
     * 构造函数初始化负载节点信息
     *
     * @param nodeId 节点编号
     * @param weight 节点权重，值越大权重越大
     */
    public Node(int nodeId, String nodeAddress, int weight) {
        this.weight = weight;
        this.nodeId = nodeId;
        this.nodeAddress = nodeAddress;
        this.currentWeight = weight;
    }
}
