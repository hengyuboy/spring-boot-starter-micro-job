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
package com.gitee.hengboy.micro.job.server.runnable;

import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeInfoRecord;
import com.gitee.hengboy.micro.job.data.service.JobNodeInfoService;
import com.gitee.hengboy.micro.job.server.configuration.properties.JobServerProperties;
import com.gitee.hengboy.micro.job.server.quartz.constants.JobConstants;
import com.gitee.hengboy.micro.job.server.service.NodeLoadBalanceService;
import com.weibo.api.motan.config.RefererConfig;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 心跳同步检查线程
 * 心跳超时剔除
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-10
 * Time：15:43
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@NoArgsConstructor
@AllArgsConstructor
public class HeartSyncCheckRunnable implements Runnable {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(HeartSyncCheckRunnable.class);
    /**
     * 节点信息
     */
    private JobNodeInfoService jobNodeInfoService;
    /**
     * 服务配置
     */
    private JobServerProperties jobServerProperties;

    /**
     * 客户端心跳检查超过60秒未更新进行剔除操作
     */
    @Override
    public void run() {
        try {
            // 查询心跳超时的节点列表
            List<Integer> nodeIds = jobNodeInfoService.selectTimeOutNodes(jobServerProperties.getHeartCheckOverTime());
            if (nodeIds != null && nodeIds.size() > 0) {
                nodeIds.stream()
                        .forEach(
                                nodeId -> {
                                    // 销毁客户端连接
                                    RefererConfig refererConfig = JobConstants.JOB_CLUSTER_EXECUTE_CACHE.get(nodeId);
                                    if (refererConfig != null) {
                                        // 断开有节点的心跳链接，销毁motan netty链接
                                        refererConfig.destroy();
                                        // 剔除内存保存的远程执行缓存信息
                                        JobConstants.JOB_CLUSTER_EXECUTE_CACHE.remove(nodeId);
                                    }
                                    // 任务节点
                                    JobNodeInfoRecord node = jobNodeInfoService.selectById(nodeId);
                                    if (node != null) {
                                        // 剔除负载均衡节点
                                        NodeLoadBalanceService.removeLoadBalanceNode(node.getJniAddress());
                                    }
                                    // 删除节点信息
                                    jobNodeInfoService.deleteById(nodeId);
                                }
                        );
            }
            logger.debug("Expired Job Node Cleaning Completed.");
        } catch (Exception e) {
            logger.error("Expired Job Node Cleaning Error", e);
        }

    }
}
