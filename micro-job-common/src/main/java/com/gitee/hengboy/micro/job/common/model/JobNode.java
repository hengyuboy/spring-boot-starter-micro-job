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
package com.gitee.hengboy.micro.job.common.model;

import com.gitee.hengboy.micro.job.common.tools.InetAddressTools;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户端任务配置属性
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-05
 * Time：13:44
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Data
public class JobNode implements Serializable {
    /**
     * ip address
     */
    private String ipAddress;
    /**
     * node port
     */
    private int port;
    /**
     * ip:port
     */
    private String nodeAddress;

    /**
     * get formatter node address
     *
     * @return 获取节点的格式化后的地址
     */
    public String getNodeAddress() {
        return InetAddressTools.formatterAddress(this.ipAddress, this.port);
    }

    /**
     * local node triggers
     */
    private List<JobNodeTrigger> triggers = new ArrayList();
}
