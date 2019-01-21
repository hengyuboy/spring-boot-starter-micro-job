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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * springBean获取工具类
 *
 * @author hengyuboy
 * ===============================
 * Created with IntelliJ IDEA.
 * User：于起宇
 * Date：2017/8/20
 * Time：23:37
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * ================================
 */
public class JobSpringBean implements ApplicationContextAware {

    /**
     * spring 上下文对象
     */
    private static ApplicationContext applicationContext;

    /**
     * 设置spring上下文对象
     *
     * @param applicationContext spring上下文
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JobSpringBean.applicationContext = applicationContext;
    }

    /**
     * 获取applicationContext
     *
     * @return spring上下文对象
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz 实例类型全路径
     * @param <T>   实例类型泛型
     * @return 从ioc内取得的实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }
}
