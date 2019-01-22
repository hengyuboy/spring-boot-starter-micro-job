package com.hengboy.micro.job.example.node.jobs;

import com.gitee.hengboy.micro.job.common.annotation.Job;
import com.gitee.hengboy.micro.job.common.exception.JobException;
import com.gitee.hengboy.micro.job.common.model.JobExecuteParam;
import com.gitee.hengboy.micro.job.common.model.JobExecuteResult;
import com.gitee.hengboy.micro.job.common.trigger.JobTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务循环执行
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-22
 * Time：10:31
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Job(cron = "0/10 * * * * ?", autoStart = true)
public class JobLoopExample implements JobTrigger {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(JobLoopExample.class);

    @Override
    public JobExecuteResult execute(JobExecuteParam param) throws JobException {
        // 自启动的任务，参数为null
        // 如果通过JobExecuteService创建的任务可以传递自定义的参数
        logger.info("执行任务：{}，携带的参数：{}", param.getJobKey(), param.getJsonParam());
        return JobExecuteResult.JOB_EXECUTE_SUCCESS;
    }
}
