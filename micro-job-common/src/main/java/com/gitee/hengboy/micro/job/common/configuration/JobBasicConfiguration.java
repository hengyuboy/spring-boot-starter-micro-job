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
package com.gitee.hengboy.micro.job.common.configuration;

import com.gitee.hengboy.micro.job.common.configuration.properties.JobAnnotationProperties;
import com.gitee.hengboy.micro.job.common.configuration.properties.JobProtocolProperties;
import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * job basic config
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-07
 * Time：15:05
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Configuration
@EnableConfigurationProperties({JobProtocolProperties.class, JobAnnotationProperties.class})
@Import({JobServiceConfiguration.class})
public class JobBasicConfiguration {
    /**
     * 协议配置
     *
     * @param jobProtocolProperties 相关协议配置参数
     * @return 协议配置实体
     */
    @Bean("job")
    @ConditionalOnMissingBean
    public ProtocolConfigBean protocolConfigBean(JobProtocolProperties jobProtocolProperties) {
        ProtocolConfigBean protocolConfigBean = new ProtocolConfigBean();
        protocolConfigBean.setName(jobProtocolProperties.getName());
        protocolConfigBean.setFilter(jobProtocolProperties.getFilter());
        return protocolConfigBean;
    }

    /**
     * 注解方式配置
     *
     * @param jobAnnotationProperties 相关注解方式配置信息
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AnnotationBean annotationBean(JobAnnotationProperties jobAnnotationProperties) {
        AnnotationBean annotationBean = new AnnotationBean();
        annotationBean.setPackage(jobAnnotationProperties.getBasePackage());
        return annotationBean;
    }
}
