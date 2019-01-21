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
package com.gitee.hengboy.micro.job.node.runnable;

import com.gitee.hengboy.micro.job.common.motan.dynamic.server.DynamicServer;
import com.gitee.hengboy.micro.job.node.configuration.properties.JobNodeProperties;
import com.gitee.hengboy.micro.job.node.service.JobClusterExecuteServiceImpl;

/**
 * 导出任务执行服务端线程
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-10
 * Time：14:17
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class ExportJobExecuteServerRunnable implements Runnable {
    /**
     * job node config properties
     */
    private JobNodeProperties jobNodeProperties;

    public ExportJobExecuteServerRunnable(JobNodeProperties jobNodeProperties) {
        this.jobNodeProperties = jobNodeProperties;
    }

    /**
     * 创建节点执行任务服务
     * - 根据application配置文件内的hengboy.job.node.local-port配置信息进行自定义端口
     */
    @Override
    public void run() {
        DynamicServer.export(
                DynamicServer.DynamicServerConfig.getDefaultConfig()
                        .setServiceRef(JobClusterExecuteServiceImpl.class)
                        .setPort(jobNodeProperties.getLocalPort())
        );
    }
}
