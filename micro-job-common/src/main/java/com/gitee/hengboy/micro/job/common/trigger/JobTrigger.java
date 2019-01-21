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
package com.gitee.hengboy.micro.job.common.trigger;

import com.gitee.hengboy.micro.job.common.exception.JobException;
import com.gitee.hengboy.micro.job.common.model.JobExecuteParam;
import com.gitee.hengboy.micro.job.common.model.JobExecuteResult;

/**
 * 任务触发器接口
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-12
 * Time：14:05
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public interface JobTrigger {
    /**
     * 任务执行方法
     *
     * @param param 执行任务请求的参数对象
     * @return 执行后返回的结果信息
     * @throws JobException 执行任务时遇到的错误信息
     */
    JobExecuteResult execute(JobExecuteParam param) throws JobException;
}
