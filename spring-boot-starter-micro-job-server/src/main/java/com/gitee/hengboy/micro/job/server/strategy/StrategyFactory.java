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
package com.gitee.hengboy.micro.job.server.strategy;

import com.gitee.hengboy.micro.job.common.enums.LoadBalanceStrategy;
import com.gitee.hengboy.micro.job.common.exception.JobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 负载均衡策略工厂
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-16
 * Time：14:57
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class StrategyFactory {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(StrategyFactory.class);

    private StrategyFactory() {
    }

    /**
     * 获取负载均衡策略实现实例
     *
     * @param loadBalanceStrategy
     * @return
     */
    public static LbStrategy getStrategy(LoadBalanceStrategy loadBalanceStrategy) throws JobException {
        try {
            LbStrategy lbStrategy = (LbStrategy) Class.forName(loadBalanceStrategy.getImplClassName()).newInstance();
            return lbStrategy;
        } catch (Exception e) {
            logger.error("load balance strategy error", e);
        }
        throw new JobException("Unable to initialize load balance strategy");
    }
}
