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
package com.gitee.hengboy.micro.job.server.cmd;

import com.gitee.hengboy.micro.job.common.thread.JobThread;
import com.gitee.hengboy.micro.job.data.service.JobNodeExecuteDetailService;
import com.gitee.hengboy.micro.job.data.service.JobNodeInfoService;
import com.gitee.hengboy.micro.job.server.configuration.properties.JobServerProperties;
import com.gitee.hengboy.micro.job.server.runnable.ExecuteJobRunnable;
import com.gitee.hengboy.micro.job.server.runnable.HeartSyncCheckRunnable;
import com.gitee.hengboy.micro.job.server.runnable.LoadWaitExecuteJobRunnable;
import com.gitee.hengboy.micro.job.server.runnable.RetryExecuteJobRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

/**
 * job server 启动完成后执行的逻辑
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-11
 * Time：10:25
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class JobServerCommandLineRunner implements CommandLineRunner {
    /**
     * 节点信息
     */
    @Autowired
    private JobNodeInfoService jobNodeInfoService;
    /**
     * 上下文
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * job server config properties
     */
    @Autowired
    private JobServerProperties jobServerProperties;
    /**
     * 任务执行详情
     */
    @Autowired
    private JobNodeExecuteDetailService jobNodeExecuteDetailService;

    /**
     * 服务端运行后执行的逻辑
     * - 启动服务端心跳检查剔除线程
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

        // 启动服务端心跳检查剔除线程
        JobThread.scheduleWithFixedDelay(new HeartSyncCheckRunnable(jobNodeInfoService, jobServerProperties), jobServerProperties.getHeartCheckInitialDelay(), jobServerProperties.getHeartCheckTime());

        // 开始执行任务
        JobThread.execute(new ExecuteJobRunnable(applicationContext));

        // 开启读取数据库内WAIT | RETRY状态的任务，写入重试执行队列
        JobThread.execute(new LoadWaitExecuteJobRunnable(jobNodeExecuteDetailService));

        // 开启重试执行任务
        JobThread.execute(new RetryExecuteJobRunnable(applicationContext, jobNodeExecuteDetailService, jobServerProperties.getRetryTimes()));
    }
}
