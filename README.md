[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/weibocom/motan/blob/master/LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/com.gitee.hengboy/spring-boot-starter-micro-job.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.gitee.hengboy/spring-boot-starter-micro-job-server)

## 基本介绍

`micro-job`是一款轻量级的`分布式任务执行框架`，内部集成了`quartz`框架来完成任务的分布式调度，`quartz`是一个强大的任务执行框架，但是`quartz`为我们提供的功能却是有限，我们较为关心的`执行日志采集`、`任务失败重试`、`任务权重调度`等在原生的`quartz`框架内实现会较为麻烦。

### 架构设计模式

`micro-job`采用了`server`与`node`的概念进行编写。

- `server`一般就是我们的`业务端`，是发起创建定时任务的模块，我们可以通过`注入`内置的`JobExecuteService`类进行对`Job`的基本操作。

- `node`是任务执行的节点，是执行发起定时任务逻辑的模块，当`server`发起了任务后，会直接通过`RPC`框架的`NIO`协议通信给`node`，接收到任务执行的任务节点执行完成后将结果反馈给`server`。

  >  具体在执行时选择的任务节点是什么，完全根据配置的`负载执行策略`。

### 最新版本说明

- v0.0.1.RELEASE （2019-1-21发布）
  - 任务上报
  - 自动执行
  - ipHash负载执行策略
  - 平滑轮询权重负载执行策略
  - 随机权重负载执行策略
  - 自动重连
  - 心跳检查
  - 任务重试
  - 任务操作

#### 任务上报

在`node`节点启动完成后会自动扫描本项目内实现`JobTrigger`接口的类，一同携带`node`节点的网络信息通过心跳的方式发送给`server`，`server`接收到后会将相应的信息进行持久化到数据库。

####自动执行

自动执行是指`任务上报后`就会`自动执行任务`，根据配置在`JobTrigger`实现类上的注解`@Job`内的`autoStart`属性进行决定的，如果该属性配置`true`并且通过`cron`属性配置了`cron`任务表达式，那么在任务上报完成后会自动在`server`通过`JobExecuteService`来进行启动任务。 

#### ipHash负载执行策略

在`micro-job`内提供了三种负载执行策略，`ip-hash`是其中的一种，这种负载均衡策略一般在相同任务在多个执行`node`下才能够更好的提现出来，如果你的任务执行`node`较少，建议采用`轮询权重策略`。

该策略根据执行任务`@Job`配置属性的`jobKey`的`hashCode`值来自动`就近分配`。

在负载均衡选择执行`node`时会把`jobKey`所绑定的所有`node`的`ip:port`信息的`hash`值通过`SortedMap`进行排序处理，如果`jobKey`的`hash`值大于所有节点的最大值时，返回最大`hash`的任务执行`node`，否则通过`tailMap`返回就近的任务执行`node`。

#### 平滑轮询权重负载执行策略

`轮询权重负载策略`在之前就被广大的应用，最大特点是可以根据权重任意配置某一个节点的执行的次数，你可以根据每一个`node`的承受压力的能力进行均衡配置。

如果相同`jobKey`的两个任务执行`node`策略配置相同，则会轮流执行。

`权重`可以通过`@Job`注解的`weight`属性进行配置，默认为：`1`

#### 随机权重负载执行策略

这种策略其实跟`ip-hash`有一部分是差不多的

在初始化负载执行节点时，随机的权重会根据上一个节点权重+当前节点的权重作为新的权重值，然后通过最后放入集合节点的权重 * `Math.random()`方法进行获取`随机权重值`，通过`tailMap`返回的`SortedMap`的集合获取随机权重最近值的`第一个`作为本次随机出来的`执行节点`。

#### 自动重连

重连有两种模式

1. `node`断开重连

   如果`node`断开后，`server`会检查超过`10秒`未心跳检查的`node`的列表进行剔除，剔除时会自动断开与`node`的`NIO`连接。

   当`node`发起第一次心跳检查时，又会自动的创建与`node`的`NIO`连接。

2. `server`断开重连

   如果`server`断开后，`node`都会不停的重试与`server`连接，当`server`启动后收到`node`发起`心跳`请求后，`server`会将该`node`信息持久化到`内存`以及`数据库`。

#### 心跳检查

`心跳检查`是一个`server`/`node`模式的基本设计，保持两端的`心跳`连接才能更好地进行通信，`server`在执行定时任务时至少在较短时间内保证`node`是有效的，当然也不能保证完全的`node`有效，所以我们才应该有了下面的`任务重试`机制

#### 任务重试

任务重试场景比较多，下面是一个简单的场景：

任务执行时负载分配到了`node1`，如果`node1`这时`停止`了，或者出现了`网络超时`的问题，导致任务执行失败，这时任务`JobTrigger.exexute`方法就会返回`JobExecuteResult.JOB_EXECUTE_RETRY`，这时`server`得到反馈后就会写入重试的`Queue`内，然后通过线程自动读取`Queue`内等待重试的任务再次负载分配执行`node`进行执行，从而实现了任务重试机制。

#### 任务操作

任务操作目前可以通过注入`JobExecuteService`类来进行，通过该类提供的对应的方法进行操作任务的`启动`、`删除`、`暂停`、`判断是否存在`等。

## 开始使用

`micro-job`已经上传到`maven center`中央仓库，所以我们可以通过`maven`、`gradle`等中央仓库`支持的依赖方式`就可以进行添加依赖。

### 工程依赖

这里是`maven`方式的依赖方式示例，如果你是其他方式，请访问[spring-boot-starter-micro-job](https://search.maven.org/artifact/com.gitee.hengboy/spring-boot-starter-micro-job-server)仓库地址，查看对应版本的依赖方式。

#### 服务端依赖

```java
<dependency>
  <groupId>com.gitee.hengboy</groupId>
  <artifactId>spring-boot-starter-micro-job-server</artifactId>
  <version>{lastVersion}</version>
</dependency>
```

#### 任务节点依赖

```java
<dependency>
  <groupId>com.gitee.hengboy</groupId>
  <artifactId>spring-boot-starter-micro-job-node</artifactId>
  <version>{lastVersion}</version>
</dependency>
```

依赖时建议采用最新的版本，把`lastVersion`更换为`maven center`内最新版本。

### 数据库初始化

我们通过集成`quartz`的`JDBC`方式进行封装，在`quartz`的基础表上进行了扩展，具体的表结构在项目的工程下的`schema_mysql.sql`文件内，这个脚本可以在`MySQL`、`MariaDB`数据库内直接执行，其他数据库版本待整理！



>  注意：v0.0.1.RELEASE版本目前使用项目的数据源进行操作任务相应的数据库表信息，暂时不支持单独的数据源。

## 常见问题

1. 怎么修改执行策略？

   可以通过修改`@Job`注解的`strategy`属性就行修改，该属性所属的值为`LoadBalanceStrategy`枚举实例。

2. 怎么开启自动执行？

   通过修改`@Job`注解的`autoStart`属性修改，配置`true`时任务上报后就会自动执行，默认为：`false`

3. `JobKey`生成的规则是什么？

   `JobKey`默认使用`@Job`注解的`jobKey`属性，如果并未配置则使用配置`@Job`注解`JobTrigger`实现类的`类名首字母小写`来配置。

4. 任务怎么动态删除？

   目前有两种方式：

   - 使用`JobExecuteService`的`remove`方法来根据`jobKey`进行删除
   - 如果是在`JobTrigger`实现类内的`execute`方法内进行删除，直接`return JobExecuteResult.JOB_EXECUTE_REMOVE;`会在本次任务执行后删除。

5. 任务重试次数怎么配置？

   在`server`端的`application.yml`、`application.properties`文件内进行配置`hengboy.job.server.retry-times`参数的值即可，默认为：`2`

6. 任务的重试次数怎么计算？

   任务在执行的时，如果存在该任务绑定的执行`node`，并且本次执行并未成功就会被计入消耗`1次`重试次数，当然如果`node`执行后返回`JobExecuteResult.JOB_EXECUTE_RETRY`也不会计算次数。

7. 支持一次性执行任务吗？

   目前可以`TriggerJob`实现类方法`execute`通过`JobExecuteResult.JOB_EXECUTE_REMOVE`删除任务的方式来进行一次性执行。

8. 更多问题请issuse



## 配置参数列表

### server配置

```yml
hengboy:
  job:
    server:
      heart-check-time: 5
      retry-times: 5
      heart-check-over-time: 10
      reg-port: 9999
```

- heart-check-time：心跳检查剔除间隔执行的时间，单位：秒，默认为：`5秒`
- retry-times：配置任务重试的次数，超过重试次数会丢弃任务，默认为：`2次`
- heart-check-over-time：心跳检查剔除的超时时长，单位：秒，如配置超时10秒后就会被剔除，默认为`10秒`
- reg-port：`server`的监听的端口号，默认为`9999`

### client 配置

```yaml
hengboy:
  job:
    node:
      local-port: 9997
      reg-address: 192.168.1.75
      reg-port: 9999
      request-timeout: 5000
      job-base-package: com.gitee.hengboy.job.node.demo
      send-heart-sync-time: 5
      send-heart-initial-delay: 5
```

- local-port：`node`的端口号，该端口号会上报到`server`
- reg-address：`server`的`IP`地址，默认为：`127.0.0.1`
- reg-port：`server`的绑定端口号，默认为：`9999`
- request-timeout：`NIO`执行超时时间，单位：毫秒，默认：`5000毫秒`
- job-base-package：扫描`JobTrigger`实现类的`base package`，如果不配置则采用`SpringBoot`默认的`base package`
- send-heart-sync-time：发送心跳检查的间隔执行事件，单位：秒，默认：`5秒`
- send-heart-initial-delay：`node`启动后延时心跳的时间，单位：秒，默认：`5秒`



> 如果存在默认值的相关参数，无需在配置文件内配置。
