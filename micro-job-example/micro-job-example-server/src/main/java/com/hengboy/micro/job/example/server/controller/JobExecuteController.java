package com.hengboy.micro.job.example.server.controller;

import com.gitee.hengboy.micro.job.server.quartz.execute.JobExecuteService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-22
 * Time：11:01
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@RestController
@RequestMapping(value = "/job")
public class JobExecuteController {
    /**
     * 任务执行
     */
    @Autowired
    private JobExecuteService jobExecuteService;

    /**
     * 启动任务
     * 参数jobKey要保证在任务执行节点是存在的
     * 也就是@Job(jobKey="")是需要配置的，具体的jobKey生成规则查看文档
     * 该示例浏览器访问地址：http://localhost:8081/job/start?jobKey=jobDynamicExample
     *
     * @param jobKey 任务执行key
     * @return
     */
    @GetMapping(value = "/start")
    public String start(String jobKey) {
        // 参数
        StartParam startParam = new StartParam(UUID.randomUUID().toString(), "测试任务名称");
        // 启动
        jobExecuteService.startJob(jobKey, startParam);

        return "start success";
    }

    /**
     * 删除任务
     * 该示例浏览器访问地址：
     * http://localhost:8081/job/remove?jobKey=jobDynamicExample
     * @param jobKey
     * @return
     */
    @GetMapping(value = "/remove")
    public String remove(String jobKey) {
        jobExecuteService.removeJob(jobKey);
        return "remove success";
    }

    @Data
    @AllArgsConstructor
    class StartParam implements Serializable {
        private String orderNo;
        private String name;
    }
}
