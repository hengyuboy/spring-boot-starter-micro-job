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

import com.gitee.hengboy.micro.job.common.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 任务执行结果
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-05
 * Time：14:43
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobExecuteResult implements Serializable {
    /**
     * SUCCESS
     */
    public static final JobExecuteResult JOB_EXECUTE_SUCCESS = new JobExecuteResult(ResultCode.SUCCESS, null);
    /**
     * ERROR
     */
    public static final JobExecuteResult JOB_EXECUTE_ERROR = new JobExecuteResult(ResultCode.ERROR, null);
    /**
     * RETRY
     */
    public static final JobExecuteResult JOB_EXECUTE_RETRY = new JobExecuteResult(ResultCode.RETRY, null);
    /**
     * 删除任务
     * 客户端执行后如果返回该类型的字段执行删除任务操作
     */
    public static final JobExecuteResult JOB_EXECUTE_REMOVE = new JobExecuteResult(ResultCode.REMOVE, null);

    /**
     * 执行结果码
     */
    private ResultCode code;
    /**
     * 任务执行参数
     */
    private JobExecuteParam param;
}
