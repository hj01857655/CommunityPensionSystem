# community-pension-frontend

## 项目简介
    基于Spring boot+Vue的社区养老系统的设计与实现
## 项目结构
    - node_modules # 项目依赖
    - public # 公共资源文件
    - src
        - api # 接口文件
        - assets # 静态资源文件
        - components # 组件文件
        - router # 路由文件
            - index.js # 路由配置
        - store # 状态管理文件
        - views # 视图文件
            -admin # 管理员目录
                - Dashboard.vue # 首页
            - Auth # 认证目录
            - Home.vue # 老人首页
            - Login.vue # 登录页
        - App.vue # 主组件
        - main.js # 主入口文件
        - utils # 工具函数文件
    - .cursorrules # 项目配置文件
    - .gitignore # 忽略文件
    - babel.config.js # 配置babel
    - jsconfig.json # 配置js
    - package.json # 项目配置文件
    - package-lock.json # 项目配置文件
    - vite.config.js # 配置vite
    - vue.config.js # 配置vue
    - yarn.lock # 项目配置文件
    - README.md # 项目需求文档
## 项目技术栈
    - Vue3.5.13
    - Element-Plus2.9.5
    - Axios1.7.7
    - Pinia2.2.6
    - Vue Router4.5.0
    - Node.js 22.14.0
    - npm 10.9.2

##项目目标
    - 实现社区养老系统的功能模块
        1. 用户管理模块
        2. 服务预约模块
        3. 健康监测模块
        4. 社区活动模块
        5. 数据分析看板模块
    -角色
        1. 管理员
        2. 老人
        3. 老人家属
        4. 社区工作人员
## 项目运行
    1. 安装依赖
        ```bash
        yarn install
        ```
    2. 运行项目
        ```bash
        yarn serve
        ```
    3. 访问前端项目
        ```bash
        http://localhost:8081
        ```
    