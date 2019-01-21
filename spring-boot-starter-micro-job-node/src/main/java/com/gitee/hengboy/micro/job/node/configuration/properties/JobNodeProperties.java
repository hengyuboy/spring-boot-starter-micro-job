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
package com.gitee.hengboy.micro.job.node.configuration.properties;

import com.weibo.api.motan.config.springsupport.BasicRefererConfigBean;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.gitee.hengboy.micro.job.node.configuration.properties.JobNodeProperties.JOB_NODE_PREFIX;

/**
 * job client config properties
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-05
 * Time：16:05
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@ConfigurationProperties(prefix = JOB_NODE_PREFIX)
@Data
public class JobNodeProperties extends BasicRefererConfigBean {
    /**
     * config properties prefix
     */
    public static final String JOB_NODE_PREFIX = "hengboy.job.node";
    /**
     * register protocol
     */
    private String protocol = "job";
    /**
     * group name
     */
    private String group = "jobGroup";
    /**
     * registry name
     */
    private String registryName = "jobRegistry";
    /**
     * set
     */
    private boolean throwException = false;
    /**
     * 任务执行最大重试次数
     */
    private Integer retries = 5;
    /**
     * registry protocol
     * direct is local protocol consumer
     */
    private String regProtocol = "direct";
    /**
     * 任务执行服务端端口号
     */
    private Integer regPort = 9999;
    /**
     * 任务执行服务端IP地址
     */
    private String regAddress = "127.0.0.1";
    /**
     * 本地执行任务的RPC端口号
     * 默认为9998
     */
    private int localPort = 9998;
    /**
     * 请求超时时长，单位：毫秒
     */
    private Integer requestTimeout = 5000;
    /**
     * 扫描Trigger接口实现类根目录
     * 如果不配置则为SpringBoot默认扫描路径
     */
    private String jobBasePackage;
    /**
     * 心跳检查间隔时间
     */
    private int sendHeartSyncTime = 5;
    /**
     * 项目启动后等待启动心跳发送线程时间，单位：秒
     */
    private int sendHeartInitialDelay = 5;
}
