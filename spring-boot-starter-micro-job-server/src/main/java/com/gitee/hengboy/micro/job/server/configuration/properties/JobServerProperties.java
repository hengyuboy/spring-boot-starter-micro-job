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
package com.gitee.hengboy.micro.job.server.configuration.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.quartz.JobStoreType;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceInitializationMode;

/**
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-08
 * Time：15:14
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Data
@ConfigurationProperties(prefix = "hengboy.job.server")
public class JobServerProperties {
    /**
     * 任务执行服务运行端口号
     */
    private Integer regPort = 9999;
    /**
     * 注册中心协议
     */
    private String regProtocol = "local";
    /**
     * registry protocol
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
     * quartz Config Properties
     */
    private QuartzConfigProperties quartz;
    /**
     * 执行任务重试次数
     */
    private int retryTimes = 2;
    /**
     * 心跳检查间隔时间
     * 单位：秒
     */
    private int heartCheckTime = 2;
    /**
     * 心跳检查线程启动等待时间
     * 单位：秒
     */
    private int heartCheckInitialDelay = 1;
    /**
     * 心跳检查超时剔除的时间
     * 单位：秒
     */
    private int heartCheckOverTime = 10;

    /**
     * 如果并未自定义配置信息
     * 使用默认的配置信息
     *
     * @return
     */
    public QuartzConfigProperties getQuartz() {
        if (quartz == null) {
            // init
            quartz = new QuartzConfigProperties();

            // 设置任务存储方式为数据库方式
            quartz.setJobStoreType(JobStoreType.JDBC);

            // 设置schema初始化模式
            quartz.getJdbc().setInitializeSchema(DataSourceInitializationMode.EMBEDDED);

            // 设置属性配置
            quartz.getProperties().put("org.quartz.scheduler.instanceName", "jobScheduler");
            quartz.getProperties().put("org.quartz.scheduler.instanceId", "AUTO");
            quartz.getProperties().put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
            quartz.getProperties().put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
            quartz.getProperties().put("org.quartz.jobStore.tablePrefix", "JOB_NODE_QRTZ_");
            quartz.getProperties().put("org.quartz.jobStore.isClustered", "true");
            quartz.getProperties().put("org.quartz.jobStore.clusterCheckinInterval", "20000");
            quartz.getProperties().put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        }
        return quartz;
    }

    /**
     * quartz config
     */
    @Data
    public static class QuartzConfigProperties extends QuartzProperties {
        public QuartzConfigProperties() {
            super();
        }
    }
}
