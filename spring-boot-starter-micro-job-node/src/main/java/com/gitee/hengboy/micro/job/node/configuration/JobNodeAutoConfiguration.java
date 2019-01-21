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

import com.gitee.hengboy.micro.job.common.configuration.JobBasicConfiguration;
import com.gitee.hengboy.micro.job.node.configuration.properties.JobNodeProperties;
import com.weibo.api.motan.config.springsupport.BasicRefererConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * node自动化配置类
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-05
 * Time：16:01
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Configuration
@EnableConfigurationProperties(JobNodeProperties.class)
@Import({JobBasicConfiguration.class, JobNodeCommandLineRunnerConfiguration.class})
public class JobNodeAutoConfiguration {
    /**
     * 自定义配置属性类
     */
    private final JobNodeProperties jobNodeProperties;

    /**
     * 构造函数注入配置属性类
     *
     * @param jobNodeProperties 配置属性类
     */
    public JobNodeAutoConfiguration(JobNodeProperties jobNodeProperties) {
        this.jobNodeProperties = jobNodeProperties;
    }

    /**
     * 配置motan客户端
     *
     * @return
     */
    @Bean
    public BasicRefererConfigBean basicRefererConfig() {
        BasicRefererConfigBean config = new BasicRefererConfigBean();
        config.setProtocol(jobNodeProperties.getProtocol());
        config.setGroup(jobNodeProperties.getGroup());
        config.setCheck(jobNodeProperties.getCheck());
        config.setRequestTimeout(jobNodeProperties.getRequestTimeout());
        config.setRegistry(jobNodeProperties.getRegistryName());
        config.setRetries(jobNodeProperties.getRetries());
        config.setThrowException(jobNodeProperties.isThrowException());
        return config;
    }


    /**
     * job 注册中心配置
     *
     * @return
     */
    @Bean("jobRegistry")
    public RegistryConfigBean registryConfigBean() {
        RegistryConfigBean registryConfigBean = new RegistryConfigBean();
        registryConfigBean.setRegProtocol(jobNodeProperties.getRegProtocol());
        registryConfigBean.setAddress(jobNodeProperties.getRegAddress() + ":" + jobNodeProperties.getRegPort());
        return registryConfigBean;
    }
}
