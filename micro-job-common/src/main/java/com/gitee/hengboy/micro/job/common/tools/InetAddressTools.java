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
package com.gitee.hengboy.micro.job.common.tools;

import java.net.Inet4Address;

/**
 * 网卡工具类
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-09
 * Time：16:36
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class InetAddressTools {
    /**
     * default ip address
     */
    private static final String DEFAULT_IP_ADDRESS = "127.0.0.1";

    /**
     * get local ip address
     *
     * @return local ip address
     */
    public static String getLocalIp() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_IP_ADDRESS;
    }

    /**
     * 格式化地址
     *
     * @param ipAddress ip地址
     * @param port      端口号
     * @return 格式化后的地址，格式为：ipAddress:port
     */
    public static String formatterAddress(String ipAddress, int port) {
        return ipAddress + ":" + port;
    }

    /**
     * 分割地址
     *
     * @param nodeAddress 节点地址
     * @return 根据":"分割节点地址字符串
     */
    public static String[] splitNodeAddress(String nodeAddress) {
        return nodeAddress.split(":");
    }
}
