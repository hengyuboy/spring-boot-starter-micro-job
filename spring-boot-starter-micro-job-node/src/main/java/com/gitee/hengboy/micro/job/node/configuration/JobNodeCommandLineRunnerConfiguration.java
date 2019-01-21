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
package com.gitee.hengboy.micro.job.node.configuration;

import com.gitee.hengboy.micro.job.common.service.HeartSyncService;
import com.gitee.hengboy.micro.job.common.thread.JobThread;
import com.gitee.hengboy.micro.job.node.configuration.properties.JobNodeProperties;
import com.gitee.hengboy.micro.job.node.runnable.ExportJobExecuteServerRunnable;
import com.gitee.hengboy.micro.job.node.runnable.HeartSyncRunnable;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * job node command line runner
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-08
 * Time：15:30
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Configuration
@EnableConfigurationProperties(JobNodeProperties.class)
public class JobNodeCommandLineRunnerConfiguration {
    /**
     * job node config properties
     */
    private JobNodeProperties jobNodeProperties;

    public JobNodeCommandLineRunnerConfiguration(JobNodeProperties jobNodeProperties) {
        this.jobNodeProperties = jobNodeProperties;
    }

    /**
     * job node startup execute
     * - 开启心跳检查线程
     * - 开启执行任务检查线程
     *
     * @return
     */
    @Bean(name = "jobCommandLineRunner")
    public CommandLineRunner commandLineRunner() {
        return new JobCommandLineRunner();
    }

    /**
     * job command line runner
     */
    class JobCommandLineRunner implements CommandLineRunner {
        /**
         * 心跳检查RPC Service
         */
        @MotanReferer(basicReferer = "basicRefererConfig")
        private HeartSyncService heartSyncService;
        /**
         * 注入spring bean factory
         * 用于获取springboot默认的package
         */
        @Autowired
        private BeanFactory beanFactory;

        @Override
        public void run(String... args) throws Exception {

            // 开启任务执行服务端
            JobThread.execute(new ExportJobExecuteServerRunnable(jobNodeProperties));

            // 开启心跳检查同步
            startHeartSync();
        }

        /**
         * 开启上报节点触发器列表线程
         */
        private void startHeartSync() {
            // 使用配置文件配置的路径
            String jobBasePackage = jobNodeProperties.getJobBasePackage();
            // 如果并未配置，则使用springboot默认扫描的package
            if (StringUtils.isEmpty(jobBasePackage)) {
                jobBasePackage = AutoConfigurationPackages.get(beanFactory).get(0);
            }
            // 开启心跳检查,每隔5秒执行同步一次
            JobThread.scheduleWithFixedDelay(new HeartSyncRunnable(heartSyncService, jobNodeProperties, jobBasePackage), jobNodeProperties.getSendHeartInitialDelay(), jobNodeProperties.getSendHeartSyncTime());
        }
    }
}
