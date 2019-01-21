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
package com.gitee.hengboy.micro.job.common.motan.dynamic.config;

import com.gitee.hengboy.micro.job.common.service.JobClusterExecuteService;
import lombok.Data;

/**
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-10
 * Time：14:05
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Data
public class BaseDynamicConfig {
    /**
     * interface class
     */
    protected Class interfaceClass = JobClusterExecuteService.class;
    /**
     * default job group
     */
    protected String group = "jobGroup";
    /**
     * local ip address
     */
    protected String localAddress = "127.0.0.1";
    /**
     * default local port
     */
    protected int localPort = 9998;
    /**
     * protocol name
     */
    protected String protocolName = "motan";
    /**
     * protocol filter
     */
    protected String protocolFilter = "statistic";
    /**
     * request time out
     */
    protected Integer requestTimeOut = 5000;
}
