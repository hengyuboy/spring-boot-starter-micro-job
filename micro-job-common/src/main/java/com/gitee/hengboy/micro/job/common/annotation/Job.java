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
package com.gitee.hengboy.micro.job.common.annotation;

import com.gitee.hengboy.micro.job.common.enums.LoadBalanceStrategy;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 任务标识注解
 * 任务上添加该注解
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-12
 * Time：13:41
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface Job {
    /**
     * job key
     * 默认值：类名
     *
     * @return 触发器key
     */
    String jobKey() default "";

    /**
     * cron表达式
     *
     * @return cron表达式
     */
    String cron() default "";

    /**
     * 自动启动，默认为false（手动启动）
     *
     * @return 是否自启动
     */
    boolean autoStart() default false;

    /**
     * 负载均衡策略，默认使用权重轮询方式
     *
     * @return 负载策略
     */
    LoadBalanceStrategy strategy() default LoadBalanceStrategy.POLL_WEIGHT;

    /**
     * 负载均衡权重配置，默认为1，值越大权重越大
     *
     * @return 负载权重
     */
    int weight() default 1;
}
