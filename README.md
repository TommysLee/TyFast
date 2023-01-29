# TyFast

简约而不简单，助力项目快速交付，事半功倍。一套源码自适应Web端、移动端，降本增效。

#### 介绍

基于SpringBoot的轻量级快速开发平台，面向个人和企业完全开源免费。

多年来一直想做一款基础开发平台，用于当下和将来的项目，让精力专注于项目业务逻辑分析和开发。期间，看了些许优秀的开源项目，但没有完全符合我的Style，于是便有了TyFast。

TyFast可用于所有Java Web项目，前后端代码都是封装过的，且十分容易上手。从架构层，也做了全局的异常处理，让开发者只专注于自己的业务逻辑开发，降低了出错的概率。

**My Style：使用新的技术，但要符合“我”的习惯。**

在这个前端（Vue、React）大行其道的背景下，后端人员想用但很无奈，是不是略显尴尬呢。如果还在用jQuery系列，是不是被人说技术落后了呢？（这里，表明一个观点，技术没有落后之分，只有应用场景之分）但对于一个热衷于技术的人来说，研究新技术并落地到项目是一件非常有意义的事情。

使用Vue，但又不想学习NodeJS、Webpack之类的怎么办呢？No problem at all！TyFast 前端基于Vue开发，但整个开发习惯与以前使用JSP/Freemarker/Thymeleaf的习惯几乎一致，极大降低了学习成本，让项目组现有成员可直接上手，门槛极低。这就是我们追求的目标，找到了成本与新技术的平衡点。

**TyFast支持Web端、移动端访问，自适应响应适配非常丝滑。**

**TyFast权限控制细粒度较细，支持按钮级权限控制。**

#### 系统界面

![](https://gitee.com/tommycloud/TyStudy/raw/master/src/main/resources/assets/ty-fast/tyfast-login.png)

![](https://gitee.com/tommycloud/TyStudy/raw/master/src/main/resources/assets/ty-fast/tyfast-index.png)

![](https://gitee.com/tommycloud/TyStudy/raw/master/src/main/resources/assets/ty-fast/city-dark.png)

![](https://gitee.com/tommycloud/TyStudy/raw/master/src/main/resources/assets/ty-fast/province-dark.png)

![](https://gitee.com/tommycloud/TyStudy/raw/master/src/main/resources/assets/ty-fast/kickout.png)

![](https://gitee.com/tommycloud/TyStudy/raw/master/src/main/resources/assets/ty-fast/ty-user.png)

![](https://gitee.com/tommycloud/TyStudy/raw/master/src/main/resources/assets/ty-fast/tyfast-menu.png)

#### 内置功能

- 用户管理
- 角色管理
- 菜单管理
- 数据字典
- 登录日志
- TPush消息推送服务(基于WebSocket STOMP实现)
- 中国行政区模块（数据更新至2020.12）
- 用户级的登录互踢功能
- 支持Dark Mode模式
- 支持设置用户级默认首页
- 支持i18N国际化（已载入中日英语言包）

#### 技术选型

> **后端技术**

- SpringBoot
- Thymeleaf
- MyBatis
- Shiro

> **前端技术**

- Vue
- [Vuetify](https://vuetifyjs.com/zh-Hans)：纯手工精心打造的 Google Material 样式的 Vue UI 组件库。 不需要任何设计技能 — 创建叹为观止的应用程序所需的一切都触手可及。

> **缓存**：基于抽象工厂模式搭建，可实现一键在多个缓存中间件切换。

- Redis

#### 系统初始化

请执行项目根目录下的 init.sql ，创建表结构和基础数据。

默认账户：admin 

密码：1

#### 让开发飞起来

请使用 [TyCode](https://gitee.com/tommycloud/tycode)，这是我的另一个开源项目，适用于所有项目，不仅仅是TyFast，其中附带的标准模板，是为TyFast量身定制的，单表CRUD，就让机器自己做吧，你可以去喝杯Coffee。

#### 项目结构说明

![](https://gitee.com/tommycloud/TyStudy/raw/master/src/main/resources/assets/ty-fast/tyfast-struture.png)

- TyFast：父项目，用于管理项目的依赖版本；
- tyfast-api：存放所有Service接口 和 实体类；
- tyfast-common：包含通用的Util类、枚举、常量等；
- tyfast-logic：依赖tyfast-api 和 tyfast-common模块，包含具体的业务逻辑实现、DAO等；
- tyfast-web：依赖tyfast-logic模块，包含所有Controller、静态资源和前端代码；

#### 后期计划

- 计划推出 TyFast 多租户版本；
- 计划推出 TyFast Dubbo微服务版本；

> 说明：TyFast Dubbo微服务版本，整体结构与TyFast几乎一致，只是换几个注解而已。这也是为什么一个单体应用，设计为上面的项目结构。

#### 最后

欢迎大家留言，看看大家有什么新的需求呢？
