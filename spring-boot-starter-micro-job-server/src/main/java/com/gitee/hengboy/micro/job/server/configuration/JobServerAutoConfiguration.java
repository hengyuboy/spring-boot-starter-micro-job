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
package com.gitee.hengboy.micro.job.server.configuration;

import com.gitee.hengboy.micro.job.common.configuration.JobBasicConfiguration;
import com.gitee.hengboy.micro.job.server.configuration.properties.JobServerProperties;
import com.gitee.hengboy.micro.job.server.listener.HeartSyncListener;
import com.gitee.hengboy.micro.job.server.listener.HeartSyncSaveNodeTriggerListener;
import com.gitee.hengboy.micro.job.server.listener.JobClusterExecuteListener;
import com.gitee.hengboy.micro.job.server.quartz.execute.JobExecuteService;
import com.weibo.api.motan.config.springsupport.BasicServiceConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * job server config
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-07
 * Time：15:19
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Configuration
@EnableConfigurationProperties({JobServerProperties.class})
@Import({JobBasicConfiguration.class, JobServerCommandLineRunnerConfiguration.class})
public class JobServerAutoConfiguration {
    /**
     * job server properties
     */
    private JobServerProperties jobServerProperties;

    public JobServerAutoConfiguration(JobServerProperties jobServerProperties) {
        this.jobServerProperties = jobServerProperties;
    }

    /**
     * basic service config
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public BasicServiceConfigBean basicServiceConfig() {
        BasicServiceConfigBean basicServiceConfigBean = new BasicServiceConfigBean();
        basicServiceConfigBean.setGroup(jobServerProperties.getGroup());
        basicServiceConfigBean.setExport(jobServerProperties.getProtocol() + ":" + jobServerProperties.getRegPort());
        basicServiceConfigBean.setRegistry(jobServerProperties.getRegistryName());
        basicServiceConfigBean.setThrowException(false);
        return basicServiceConfigBean;
    }


    /**
     * job 注册中心配置
     *
     * @return
     */
    @Bean("jobRegistry")
    @ConditionalOnMissingBean
    public RegistryConfigBean registryConfigBean() {
        RegistryConfigBean registryConfigBean = new RegistryConfigBean();
        registryConfigBean.setRegProtocol(jobServerProperties.getRegProtocol());
        return registryConfigBean;
    }

    /**
     * 心跳同步事件监听
     *
     * @return
     */
    @Bean
    HeartSyncListener heartSyncListener() {
        return new HeartSyncListener();
    }

    /**
     * 上报触发器后自启动事件监听
     *
     * @return
     */
    @Bean
    HeartSyncSaveNodeTriggerListener reportTriggerAfterListener() {
        return new HeartSyncSaveNodeTriggerListener();
    }

    /**
     * 任务远程执行事件监听
     *
     * @return
     */
    @Bean
    JobClusterExecuteListener jobclusterExecuteListener() {
        return new JobClusterExecuteListener();
    }

    /**
     * 任务执行业务逻辑
     *
     * @return
     */
    @Bean
    JobExecuteService jobExecuteService() {
        return new JobExecuteService();
    }
}
