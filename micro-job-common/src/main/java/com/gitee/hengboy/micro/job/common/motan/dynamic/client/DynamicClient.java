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
package com.gitee.hengboy.micro.job.common.motan.dynamic.client;

import com.gitee.hengboy.micro.job.common.motan.dynamic.config.BaseDynamicConfig;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.RegistryConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 动态创建客户端
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-10
 * Time：13:41
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class DynamicClient {
    private DynamicClient() {
    }

    /**
     * 动态创建客户端
     *
     * @param dynamicClientConfig 动态客户端配置信息
     * @return 客户端实例
     */
    public static RefererConfig getInstance(DynamicClientConfig dynamicClientConfig) {
        // 客户端配置
        RefererConfig referer = new RefererConfig();
        referer.setInterface(dynamicClientConfig.getInterfaceClass());
        referer.setGroup(dynamicClientConfig.getGroup());
        referer.setRequestTimeout(dynamicClientConfig.getRequestTimeOut());

        // 注册中心配置
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setRegProtocol(dynamicClientConfig.getRegProtocol());
        registryConfig.setAddress(dynamicClientConfig.getLocalAddress() + ":" + dynamicClientConfig.getLocalPort());
        referer.setRegistry(registryConfig);

        // 配置RPC协议
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setId(dynamicClientConfig.getProtocolName());
        protocolConfig.setName(dynamicClientConfig.getProtocolName());
        protocolConfig.setFilter(dynamicClientConfig.getProtocolFilter());
        referer.setProtocol(protocolConfig);
        // 设置共享端口号
        referer.setShareChannel(true);
        return referer;
    }

    /**
     * 动态客户端配置实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicClientConfig extends BaseDynamicConfig {
        /**
         * 获取默认的配置信息
         *
         * @return 客户端默认配置实例
         */
        public static DynamicClientConfig getDefaultConfig() {
            return new DynamicClientConfig();
        }

        /**
         * 设置客户端IP地址
         *
         * @param ipAddress 客户端ip地址
         * @return 自身配置
         */
        public DynamicClientConfig setAddress(String ipAddress) {
            this.localAddress = ipAddress;
            return this;
        }

        /**
         * 设置客户端端口号
         *
         * @param port 端口号
         * @return 自身配置
         */
        public DynamicClientConfig setPort(int port) {
            this.localPort = port;
            return this;
        }

        /**
         * registry protocol
         */
        private String regProtocol = "direct";
    }
}
