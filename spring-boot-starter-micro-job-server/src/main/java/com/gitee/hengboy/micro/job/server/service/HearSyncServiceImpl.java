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
package com.gitee.hengboy.micro.job.server.service;

import com.gitee.hengboy.micro.job.common.exception.JobException;
import com.gitee.hengboy.micro.job.common.model.JobNode;
import com.gitee.hengboy.micro.job.common.service.HeartSyncService;
import com.gitee.hengboy.micro.job.server.event.HeartSyncEvent;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * 心跳检查service实现
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-08
 * Time：16:31
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@MotanService(shareChannel = true)
public class HearSyncServiceImpl implements HeartSyncService {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 收到客户端心跳检查
     * - 处理心跳检查记录
     * - 动态创建任务执行客户端
     *
     * @param jobNode 客户端信息
     * @throws JobException
     */
    @Override
    public void sync(JobNode jobNode) throws JobException {
        applicationContext.publishEvent(new HeartSyncEvent(this, jobNode));
    }

}
