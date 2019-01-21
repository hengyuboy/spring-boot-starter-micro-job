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
package com.gitee.hengboy.micro.job.server.listener;

import com.gitee.hengboy.micro.job.common.model.JobNode;
import com.gitee.hengboy.micro.job.common.tools.InetAddressTools;
import com.gitee.hengboy.micro.job.data.service.JobNodeInfoService;
import com.gitee.hengboy.micro.job.server.event.HeartSyncEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

/**
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-12
 * Time：10:31
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class HeartSyncListener implements SmartApplicationListener {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(HeartSyncListener.class);
    /**
     * 节点基本信息
     */
    @Autowired
    private JobNodeInfoService jobNodeInfoService;

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
        return aClass == HeartSyncEvent.class;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        try {
            // 转换事件
            HeartSyncEvent heartSyncEvent = (HeartSyncEvent) applicationEvent;
            // 节点信息
            JobNode jobNode = heartSyncEvent.getJobNode();
            // 格式化节点地址
            String nodeAddress = InetAddressTools.formatterAddress(jobNode.getIpAddress(), jobNode.getPort());
            // 根据节点地址查询节点信息是否存在
            jobNodeInfoService.selectOrInsert(nodeAddress);
            // 执行更新最后心跳时间
            jobNodeInfoService.updateLastHeartTime(nodeAddress);
            logger.debug("job node [{}]，Last Heart Synchronization Time Update Completed.", nodeAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
