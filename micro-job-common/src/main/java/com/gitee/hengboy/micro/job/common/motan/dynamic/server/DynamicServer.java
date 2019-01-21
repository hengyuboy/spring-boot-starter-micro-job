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
package com.gitee.hengboy.micro.job.common.motan.dynamic.server;

import com.gitee.hengboy.micro.job.common.motan.dynamic.config.BaseDynamicConfig;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RegistryConfig;
import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.BindException;

/**
 * 动态创建服务端
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-10
 * Time：14:01
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class DynamicServer {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(DynamicServer.class);

    private DynamicServer() {
    }

    /**
     * 获取动态创建的服务端信息
     *
     * @param dynamicServerConfig 动态创建服务端的配置信息
     */
    public static void export(DynamicServerConfig dynamicServerConfig) {
        try {
            // 服务端配置
            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.setInterface(dynamicServerConfig.getInterfaceClass());
            serviceConfig.setRef(dynamicServerConfig.getServiceRef().newInstance());
            serviceConfig.setGroup(dynamicServerConfig.getGroup());

            // 配置注册中心
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setRegProtocol(dynamicServerConfig.getRegProtocol());
            serviceConfig.setRegistry(registryConfig);

            // 配置RPC协议配置信息
            ProtocolConfig protocolConfig = new ProtocolConfig();
            protocolConfig.setId(dynamicServerConfig.getProtocolName());
            protocolConfig.setName(dynamicServerConfig.getProtocolName());
            protocolConfig.setFilter(dynamicServerConfig.getProtocolFilter());
            serviceConfig.setProtocol(protocolConfig);

            // 设置共享端口号
            serviceConfig.setShareChannel(true);
            serviceConfig.setExport(dynamicServerConfig.getProtocolName() + ":" + dynamicServerConfig.getLocalPort());
            serviceConfig.export();

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
        } catch (Exception e) {
            logger.error("动态创建服务端遇到异常");
            if (e instanceof BindException) {
                logger.error("端口号：{}，已被占用，请更换.", dynamicServerConfig.getRegProtocol());
            }
            e.printStackTrace();
        }
    }

    /**
     * 动态创建服务端的配置信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicServerConfig extends BaseDynamicConfig {
        /**
         * 获取默认的配置信息
         *
         * @return 动态服务端配置
         */
        public static DynamicServerConfig getDefaultConfig() {
            return new DynamicServerConfig();
        }

        /**
         * 设置服务实现类
         *
         * @param serviceRef 服务实现类类型
         * @return 自身实例
         */
        public DynamicServerConfig setServiceRef(Class serviceRef) {
            this.serviceRef = serviceRef;
            return this;
        }

        /**
         * 设置服务端端口号
         *
         * @param localPort 本地端口号
         * @return 自身实例
         */
        public DynamicServerConfig setPort(int localPort) {
            this.localPort = localPort;
            return this;
        }

        /**
         * registry protocol
         */
        private String regProtocol = "local";
        /**
         * service ref
         */
        private Class serviceRef;
    }
}
