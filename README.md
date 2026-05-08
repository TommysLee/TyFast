<p align="center">
    <img alt="TyFast LOGO" width="130" src="https://raw.githubusercontent.com/TommysLee/TyFast/refs/heads/master/tyfast-web/src/main/resources/public/images/logo/ty-greeen.png">    
</p>
<h1 align="center" style="font-weight: bold;">TyFast 多租户版</h1>
<h4 align="center">简约而不简单，用最务实的方式，让后端开发者真正用好Vue</h4>
<p align="center">
    <img alt="Build Passing" src="https://img.shields.io/badge/build-passing-brightgreen.svg">
    <img alt="TyFast-3.x" src="https://img.shields.io/badge/TyFast-v3.x-brightgreen.svg?logo=github">
    <img alt="Apache License" src="https://img.shields.io/badge/license-apache-brightgreen.svg">
</p>

一套代码，同时适配PC端、移动端、小程序端。基于TyFast，你可以快速搭建各类管理系统及平台后台，专注于自身业务，无需关心基础平台功能。

#### IMPORTANT TIPS

**当前主分支为多租户版，若需经典版，请访问[2.x分支](https://gitee.com/tommycloud/TyFast/tree/2.x/)**

### 为什么有TyFast?

#### 一个真实存在却很少有人发声的困境

在当前Vue与React等前端框架大行其道的环境下，一个真实而普遍的困境长期被忽视：**后端开发者想用Vue构建现代化UI界面，却被NodeJS、Webpack、Vite等前端工程化工具“劝退”。**

这种困境的代价是真实且沉重的：

- **人力成本翻倍**：前后端分离需要2班人马，中小公司前端业务饱和度低，却要承担双倍人力成本。
- **技术决策被“门”隔开了**：前端工程化的门槛将“用Vue”这个选项，从后端团队的技术选型表中天然排除。
- **对话消失了**：没有人（或极少有人）为后端团队的这种困境发声，系统性地被忽略了。

技术本身并无绝对的先进与落后之分，其价值完全取决于具体应用场景的适配度。然而对于真正的技术探索者而言，持续追踪前沿技术并将其转化为实际生产力，始终是专业成长道路上最具吸引力的征程。

#### TyFast的回答

**「想用Vue，却不想深陷NodeJS、Webpack配置泥潭」——完全不必纠结！**

TyFast 以Vue为内核，却完美复刻了JSP/Freemarker/Thymeleaf等传统模板引擎的开发范式：熟悉的MVC分层、直观的模板语法、零配置的构建流程。**你只需会Java Web、会用Thymeleaf，即可直接上手Vue 3组件开发。**

**不需要学习NodeJS，不需要配置Webpack，不需要理解npm、vite、构建环境——这些都与 TyFast 无关。**

### 设计哲学

**“使用新的技术，但要符合‘我’的习惯。”**

TyFast 坚持一个核心原则：**新技术必须被真正用起来，而不是停留在“会配置”的层面。**

- 我们不否认前端工程化的价值，它适合专门的前端团队和复杂的SPA场景。
- 我们也不抵触学习新技术，事实上 TyFast 在持续追踪Vue 3、Vuetify 3/4等前沿技术。

**但当技术门槛变成了技术使用的“准入证”，让有需求的开发者被挡在门外时，这就是一个值得被解决的问题。**

TyFast 希望提供一个**更低的起点、更平滑的坡度**——让后端开发者能够直接切入Vue 3的开发体验，而不是先翻越一座工程化的大山。

**这种“新技术外壳 + 旧习惯内核”的创新设计，既保留了Vue组件化开发的先进特性，又消除了前端工程化带来的认知负担，真正实现了技术升级与团队适应性的黄金平衡。**

### 核心特色

#### 1、一套代码，多端自适应

TyFast 支持Web端、移动端访问，自适应响应式适配非常丝滑。同一个页面在PC、平板、手机上自动适配，无需额外开发移动端页面。

#### 2、多端接口完全统一

认证、鉴权、注销等接口，在Web端、移动端、小程序端完全统一：

- **Web端**：基于Cookie的会话管理
- **移动端/小程序端**：基于Header的会话管理，接口完全通用

#### 3、权限控制（按钮级）

支持细粒度的按钮级权限控制，灵活配置角色与权限。

#### 4、免密登录

支持配置免密登录账户，适用于广告LED屏、Dashboard大屏看板等公开服务展示场景。

### 系统界面

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

### 内置功能

- 用户管理、角色管理、菜单管理、数据字典
- 登录日志、用户级登录互踢功能
- TPush消息推送服务(基于WebSocket STOMP实现)
- 中国行政区模块（数据更新至2026.5）
- Dark Mode模式
- 支持用户级默认首页设置
- 支持i18N国际化（中日英语言包）
- 支持微信登录
- 支持海康视频监控
- 支持网站信息动态配置（名称、LOGO、备案号等）

### 技术选型

#### 后端技术

- SpringBoot
- Thymeleaf
- MyBatis
- Shiro

#### 前端技术

- Vue3
- [Vuetify3](https://vuetifyjs.com/zh-Hans) — 纯手工精心打造的Google Material样式Vue UI组件库

#### 缓存

基于抽象工厂模式搭建，可在多个缓存中间件一键切换。

当前支持：

- Redis（推荐）
- Memcached
- Memory

### 快速开始

#### 初始化

请执行项目根目录下的 init.sql ，创建表结构和基础数据。

- 默认账户：admin 


- 密码：1


### 让开发飞起来

配合 [TyCode](https://github.com/TommysLee/TyCode) 代码生成器，单表CRUD自动生成，你可以去喝杯Coffee 😄

### 项目结构

![](https://github.com/TommysLee/images-bed/blob/main/ty-fast/tyfast-struture.png?raw=true)

- **TyFast**：父项目，管理依赖版本；
- **tyfast-api**：Service接口 + 实体类；
- **tyfast-common**：通用Util类、枚举、常量等；
- **tyfast-logic**：依赖tyfast-api 和 tyfast-common模块，包含业务逻辑实现、DAO等；
- **tyfast-web**：依赖tyfast-logic模块，包含Controller、静态资源和前端代码；

### 适用场景

- 企业后台管理系统
- 移动端管理后台（H5）
- Dashboard数据大屏（配合免密登录）
- 需要Web端 + 移动端同时覆盖的中小项目

### QA

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

### 最后

欢迎大家留言，看看大家有什么新的需求呢？
