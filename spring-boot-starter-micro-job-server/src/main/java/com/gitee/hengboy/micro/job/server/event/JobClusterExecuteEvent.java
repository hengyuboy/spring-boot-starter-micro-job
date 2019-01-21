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
package com.gitee.hengboy.micro.job.server.event;

import com.gitee.hengboy.micro.job.data.model.tables.records.JobNodeExecuteDetailRecord;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 任务远程执行事件
 *
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-15
 * Time：15:55
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
@Getter
public class JobClusterExecuteEvent extends ApplicationEvent {
    /**
     * 任务执行信息
     */
    private JobNodeExecuteDetailRecord executeDetail;

    public JobClusterExecuteEvent(Object source, JobNodeExecuteDetailRecord executeDetail) {
        super(source);
        this.executeDetail = executeDetail;
    }
}
