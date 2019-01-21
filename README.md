[TOC]

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/weibocom/motan/blob/master/LICENSE)[![Maven Central](https://img.shields.io/maven-central/v/com.gitee.hengboy/spring-boot-starter-micro-job.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.gitee.hengboy/spring-boot-starter-micro-job-server)

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

