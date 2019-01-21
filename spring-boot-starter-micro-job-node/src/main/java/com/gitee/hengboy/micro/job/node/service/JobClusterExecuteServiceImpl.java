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
package com.gitee.hengboy.micro.job.node.service;

import com.gitee.hengboy.micro.job.common.exception.JobException;
import com.gitee.hengboy.micro.job.common.model.JobExecuteParam;
import com.gitee.hengboy.micro.job.common.model.JobExecuteResult;
import com.gitee.hengboy.micro.job.common.service.JobClusterExecuteService;
import com.gitee.hengboy.micro.job.common.tools.JobSpringBean;
import com.gitee.hengboy.micro.job.common.trigger.JobTrigger;
import com.gitee.hengboy.micro.job.node.constants.NodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动态任务执行业务逻辑
 * 该类属于动态创建motan所使用，不需要添加@MotanService注解
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-10
 * Time：14:27
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class JobClusterExecuteServiceImpl implements JobClusterExecuteService {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(JobClusterExecuteServiceImpl.class);

    /**
     * 执行本地任务
     * 1. 根据参数传递的jobKey进行缓存内读取执行
     * 2. 反射执行方法
     *
     * @param param 执行参数
     * @return
     * @throws JobException
     */
    @Override
    public JobExecuteResult execute(JobExecuteParam param) throws JobException {
        try {
            Class<? extends JobTrigger> triggerClass = NodeConstants.CLUSTER_EXECUTE_SERVICE.get(param.getJobKey());
            // 获取执行的JobTrigger实现类
            JobTrigger jobTrigger = JobSpringBean.getBean(triggerClass);
            if (jobTrigger != null) {
                // 执行任务逻辑
                return jobTrigger.execute(param);
            } else {
                logger.error("Task instance was not found, see if annotations @Job are configured", new JobException("Task instance was not found, see if annotations @Job are configured"));
            }
        }
        // 存在异常，返回重试
        catch (Exception e) {
            logger.error("Execute job encountered exceptions.", e);
            return JobExecuteResult.JOB_EXECUTE_RETRY;
        }
        // 如果并未找到执行的实现，返回重试
        return JobExecuteResult.JOB_EXECUTE_RETRY;
    }
}
