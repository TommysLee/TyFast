# TyFast 多租户版

简约而不简单，助力项目快速交付，事半功倍。一套源码自适应Web端、移动端，提质增效。

#### IMPORTANT TIPS

**当前主分支为多租户版，若需经典版，请访问[2.x分支](https://gitee.com/tommycloud/TyFast/tree/2.x/)**

#### 介绍

基于SpringBoot的轻量级快速开发平台，面向个人和企业完全开源免费。

多年来一直想做一款基础开发平台，用于当下和将来的项目，让精力专注于项目业务逻辑分析和开发。期间，看了些许优秀的开源项目，但没有完全符合我的Style，于是便有了TyFast。

TyFast可用于所有Java Web项目，前后端代码都是封装过的，且十分容易上手。从架构层，也做了全局的异常处理，让开发者只专注于自己的业务逻辑开发，降低了出错的概率。

**My Style：使用新的技术，但要符合“我”的习惯。**

在Vue与React等前端框架大行其道的当下，后端开发者常陷入"欲用之而无门"的窘境，这种技术生态的割裂感难免令人心生唏嘘。当部分项目仍停留在jQuery技术栈时，是否就该被贴上"技术滞后"的标签？在此需明确一个核心观点：技术本身并无绝对的先进与落后之分，其价值完全取决于具体应用场景的适配度——就像内燃机与电动机各有其用武之地，技术的生命力始终源于对需求的精准回应。然而对于**真正的技术探索者而言，持续追踪前沿技术并将其转化为实际生产力，始终是专业成长道路上最具吸引力的征程**。

**面对**「想用Vue却不想深陷NodeJS、Webpack配置泥潭」的**困境**？完全**不必纠结**！**TyFast前端框架以Vue为内核**，却**完美复刻**了JSP/Freemarker/Thymeleaf等传统模板引擎的开发范式——熟悉的MVC分层、直观的模板语法、零配置的构建流程，让团队无需重构工作流即可无缝迁移。**这种「新技术外壳+旧习惯内核」的创新设计**，既保留了Vue组件化开发的先进特性，又消除了前端工程化带来的认知负担，**真正实现了技术升级与团队适应性的黄金平衡**。

**TyFast支持Web端、移动端访问，自适应响应适配非常丝滑。**

**TyFast权限控制细粒度较细，支持按钮级权限控制。**

**TyFast认证与鉴权接口，在Web端、移动端、小程序端均保持完全统一。真正做到一套代码，即可自适应多终端，让开发更加便捷高效。**

#### 系统界面

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/tyfast-login.png?raw=true)

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/tyfast-index_v3.png?raw=true)

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/binding_v3.png?raw=true)

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/setting-home_v3.png?raw=true)

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/video_v3.png?raw=true)

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/city-dark_v3.png?raw=true)

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/province-dark_v3.png?raw=true)

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/kickout_v3.png?raw=true)

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/tyfast-user_v3.png?raw=true)

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/tyfast-menu_v3.png?raw=true)

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/auto-login.png?raw=true)

#### 内置功能

- 支持海康视频监控
- 用户管理
- 角色管理
- 菜单管理
- 数据字典
- 登录日志
- TPush消息推送服务(基于WebSocket STOMP实现)
- 中国行政区模块（数据更新至2025.7）
- 用户级的登录互踢功能
- 支持Dark Mode模式
- 支持设置用户级默认首页
- 支持**免密自动登录** （可配置，多用于广告LED屏、Dashboard大屏看板等场景下的公开服务信息展示）
- 支持i18N国际化（已载入中日英语言包）
- 支持微信登录
- 支持网站信息动态配置，如：网站名称、LOGO、备案号、Copyright等。
- 登录、注销、权限验证等接口，在Web端、移动端、小程序端均保持完全统一

#### 技术选型

> **后端技术**

- SpringBoot
- Thymeleaf
- MyBatis
- Shiro

> **前端技术**

- Vue3
- [Vuetify3](https://vuetifyjs.com/zh-Hans)：纯手工精心打造的 Google Material 样式的 Vue UI 组件库。 不需要任何设计技能 — 创建叹为观止的应用程序所需的一切都触手可及。

> **缓存**：基于抽象工厂模式搭建，可实现一键在多个缓存中间件切换。

- Redis

#### 系统初始化

请执行项目根目录下的 init.sql ，创建表结构和基础数据。

默认账户：admin 

密码：1

#### 让开发飞起来

请使用 [TyCode](https://github.com/TommysLee/TyCode)，这是我的另一个开源项目，适用于所有项目，不仅仅是TyFast，其中附带的标准模板，是为TyFast量身定制的，单表CRUD，就让机器自己做吧，你可以去喝杯Coffee。

#### 项目结构说明

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/tyfast-struture.png?raw=true)

- TyFast：父项目，用于管理项目的依赖版本；
- tyfast-api：存放所有Service接口 和 实体类；
- tyfast-common：包含通用的Util类、枚举、常量等；
- tyfast-logic：依赖tyfast-api 和 tyfast-common模块，包含具体的业务逻辑实现、DAO等；
- tyfast-web：依赖tyfast-logic模块，包含所有Controller、静态资源和前端代码；

#### 其它

- **关于免密登录的说明**

  1. 免密登录Realm默认开启，无需代码配置；

  2. 哪个账户需要开启免密登录，请在 “数据字典” 的 “AUTO-LOGIN” 字典中配置，字典项名称可任意，值为账户；

  3. 免密进入系统的链接为：http://localhost/show/{字典项名称}

     如：http://localhost/show/adm
  
- **关于登录与注销接口多端统一化的说明**

  - **web端**

    会话管理通过采用 Cookie 的方式来实现，其中 Session ID 的名称可在 YAML 配置文件中进行设定。

  - **移动端/小程序端**

    会话管理采用基于 Header 的实现方式，具体做法是将 Session ID 置于请求头内，其中 Header 的名称即作为 Session ID 的标识。此外，该 Session ID 的名称与 Web 端保持一致。

#### 最后

欢迎大家留言，看看大家有什么新的需求呢？
