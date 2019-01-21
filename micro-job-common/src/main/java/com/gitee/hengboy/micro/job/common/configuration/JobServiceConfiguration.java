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

import com.gitee.hengboy.micro.job.common.tools.JobSpringBean;
import com.gitee.hengboy.micro.job.data.service.JobNodeExecuteDetailService;
import com.gitee.hengboy.micro.job.data.service.JobNodeInfoService;
import com.gitee.hengboy.micro.job.data.service.JobNodeTriggerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 所需service配置
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-11
 * Time：10:53
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Configuration
public class JobServiceConfiguration {
    /**
     * 任务节点基本信息
     *
     * @return 任务节点基本信息实例
     */
    @Bean
    public JobNodeInfoService jobNodeInfoService() {
        return new JobNodeInfoService();
    }

    /**
     * 任务节点触发器
     *
     * @return 节点触发器信息实例
     */
    @Bean
    public JobNodeTriggerService jobNodeTriggerService() {
        return new JobNodeTriggerService();
    }

    /**
     * 任务执行详情
     *
     * @return 节点任务执行详情对象实例
     */
    @Bean
    public JobNodeExecuteDetailService jobNodeExecuteDetailService() {
        return new JobNodeExecuteDetailService();
    }

    /**
     * 提供在普通类内获取Spring IOC容器内的类实例工具
     *
     * @return Spring Bean操作工具类
     */
    @Bean
    public JobSpringBean springBeanTools() {
        return new JobSpringBean();
    }
}
