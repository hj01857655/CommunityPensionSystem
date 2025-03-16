# Community Pension Frontend

这是社区养老系统的前端项目。

## 项目简介

基于 Spring Boot 和 Vue 的社区养老系统的设计与实现。

## 项目功能
 -前台（老人、家属）
    -用户登录
    -首页
        -天气展板
        -服务预约进度展板
        -健康档案展板
        -活动参与情况展板
        -通知公告展板
        -一键紧急呼叫展板
    -服务预约模块
        -服务浏览与预约
        -服务评价
        -服务记录
    -健康记录模块
        -健康监测
        -健康档案录入、查看、编辑
    -社区活动模块
        -活动浏览、详情查看
        -活动报名
        -活动签到
    -通知公告
        -通知公告浏览
        -通知公告详情查看
 -后台（管理员）
    -用户管理
        -用户信息管理
        -用户角色管理
    -服务管理
        -服务类型管理
        -服务预约管理（推荐、发布、审核）
        -服务统计（预约情况、评价情况）
    -社区活动管理
        -活动类型管理
        -活动推荐、发布
        -活动审核（社区工作人员）
        -活动统计（报名、签到）
    -通知公告管理
        -通知公告类型
        -通知公告推荐、发布
        -通知公告审核（社区工作人员）
        -通知公告统计（阅读、点赞）
    -系统设置
        -角色管理【老人、家属、社区工作人员、管理员】
        -权限管理(基于RBAC的权限管理)
        -日志管理（登录、操作）
        -系统配置（
## 项目设计
-数据库表设计：需要设计共10张表
    
    

## 项目技术栈

- **Vue 3**: 前端框架
- **Vue Router 4**: 路由管理
- **Element Plus**: UI 组件库
- **Axios**: HTTP 请求库
- **Echarts**: 图表库
- **Webpack**: 模块打包工具
- **Vite**: 开发服务器和构建工具

## 项目结构

community-pension-frontend

    - public    # 公共资源
        - index.html # 主页面
    - src
        - api      # 接口文件
            - back  # 后台接口
            - fore # 前台接口
        - assets   # 资源文件
        - components # 组件文件
        - views    # 页面文件
            - back # 后台页面
            - fore # 前端页面
        - router   # 路由文件
        - utils    # 工具文件
        - store    # 状态管理文件
            - back  # 后台状态管理
            - fore # 前端状态管理
        - main.js   # 主入口文件
        - App.vue   # 主组件
    -babel.config.js # babel配置文件
    -index.html # 主页面
    -jsconfig.json # js配置文件
    -package.json # 依赖文件
    -vite.config.js # vite配置文件
    -vue.config.js # vue配置文件
    -README.md # 项目简介
    -.env # 环境变量
    -.gitignore # 忽略文件
