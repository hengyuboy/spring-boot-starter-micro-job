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
package com.gitee.hengboy.micro.job.common.thread;

import java.util.concurrent.*;

/**
 * @author：于起宇 <p>
 * ================================
 * Created with IDEA.
 * Date：2019-01-08
 * Time：15:52
 * 个人博客：http://blog.yuqiyu.com
 * 简书：http://www.jianshu.com/u/092df3f77bca
 * 码云：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 * ================================
 * </p>
 */
public class JobThread {
    /**
     * 定时普通线程池
     */
    private static final ExecutorService pool = Executors.newFixedThreadPool(100);
    /**
     * 定义定时线程池
     */
    private static final ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(10);

    /**
     * 执行一个线程
     *
     * @param runnable 线程接口实例
     */
    public static void execute(Runnable runnable) {
        pool.execute(runnable);
    }

    /**
     * 定时任务循环执行线程
     *
     * @param runnable 线程接口实例
     * @param delay    间隔执行的时间，单位：秒
     */
    public static ScheduledFuture<String> scheduleWithFixedDelay(Runnable runnable, int initialDelay, int delay) {
        return (ScheduledFuture<String>) scheduledPool.scheduleWithFixedDelay(runnable, initialDelay, delay, TimeUnit.SECONDS);
    }

    /**
     * 提交执行一个回调
     *
     * @param callable 线程接口实例
     * @return 执行回执
     */
    public static Future<Object> submit(Callable callable) {
        return pool.submit(callable);
    }

}
