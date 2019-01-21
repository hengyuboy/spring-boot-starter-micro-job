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

import com.gitee.hengboy.micro.job.common.trigger.JobTrigger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Set;

/**
 * class工具类
 * - 根据指定package name 查询指定接口的所有实现类等
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-12
 * Time：13:53
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class ClassTools {
    /**
     * 获取指定package下的接口实现类
     *
     * @param scannerPackage 扫描的package
     * @return 实现类集合
     */
    public static Set<Class<? extends JobTrigger>> getJobs(String scannerPackage) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setScanners(new TypeAnnotationsScanner(), new SubTypesScanner());
        configurationBuilder.filterInputsBy(new FilterBuilder().includePackage(scannerPackage));
        configurationBuilder.addUrls(ClasspathHelper.forPackage(scannerPackage));
        Reflections reflections = new Reflections(scannerPackage);
        return reflections.getSubTypesOf(JobTrigger.class);
    }
}
