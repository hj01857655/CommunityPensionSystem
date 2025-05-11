# 社区养老系统 (Community Pension System)

## 项目定位
这是一个基于Spring Boot 3和Vue 3开发的社区养老管理系统，作为毕业设计项目。本系统采用前后端分离架构，实现了基础的社区养老服务功能，包括用户管理、服务预约、活动管理等核心功能。项目适合作为学习Spring Boot和Vue的实践项目，也适合作为毕业设计展示。

## 开发者信息
- 开发者：在校大学生
- 开发目的：毕业设计
- 开发周期：1个月
- 技术特点：
  - 采用主流技术栈，注重基础功能实现
  - 代码结构清晰，便于维护
  - 适合作为学习Spring Boot和Vue的实践项目
- 项目亮点：
  - 基础的用户权限管理
  - 简单的服务预约功能
  - 基础的社区活动管理
  - 清晰的项目文档

## 项目介绍
基于 Spring Boot 3 和 Vue 3 的前后端分离架构社区养老管理系统。本系统旨在为社区老年人提供便捷的养老服务管理平台，包括基础健康记录、服务预约、活动管理等功能，提升社区养老服务质量和管理效率。

### 项目特色
- 基础健康记录：支持简单的健康信息录入和查看
- 便捷服务预约：提供在线服务预约功能
- 活动管理：支持社区活动发布和报名
- 权限管理：基于RBAC的基础权限控制

## 功能特性

### 前台门户（老人、家属）

#### 1. 用户中心
- 账号管理：登录、密码修改
- 个人信息管理
  - 基本资料
    - 用户名
    - 姓名
    - 性别
    - 手机号码
    - 居住地址
  - 角色信息
    - 老人信息
      - 身份证号码
      - 出生日期
      - 年龄
      - 紧急联系人
    - 家属信息
      - 绑定老人
      - 与老人关系

#### 2. 首页
- 服务预约进度
- 活动参与情况
- 通知公告展示
- 基础健康信息

#### 3. 服务预约
- 服务列表浏览
- 在线预约服务
- 预约记录查看

#### 4. 健康记录
- 基础信息录入
  - 身高体重
  - 血压记录
  - 基础病史
- 健康记录查看

#### 5. 社区活动
- 活动信息浏览
- 在线活动报名
- 活动参与记录查看

#### 6. 消息中心
- 通知公告查看
- 系统消息查看

### 后台管理（管理员）

#### 1. 系统设置
- 用户管理（基础CRUD）
- 角色管理（基础角色：老人、家属、管理员）
- 菜单管理（基础菜单配置）
- 字典管理
  - 字典类型管理
    - 性别
    - 活动状态
    - 服务类型
    - 系统配置
  - 字典数据管理
    - 数据列表
    - 数据状态
    - 数据排序

#### 2. 服务预约管理
- 服务项目管理
  - 服务列表
  - 服务信息管理
  - 基础服务操作（新增、编辑、删除）
- 预约记录管理

#### 3. 健康监测管理
- 健康档案管理
  - 基础档案管理
  - 健康记录查看

#### 4. 社区活动管理
- 活动管理
  - 活动列表
  - 活动发布
  - 活动编辑
  - 活动状态管理
- 报名管理
  - 报名记录查看
  - 报名审核

#### 5. 通知公告管理
- 通知列表
- 通知发布

## 技术架构

### 关键技术实现
- 基础数据统计
- RBAC权限控制
- 服务预约管理
- 健康记录管理
### 后端技术栈
- **核心框架**: Spring Boot 3.x
- **数据库**: MySQL 8.4.0
- **ORM框架**: MyBatis Plus
- **工具集**: 
  - Lombok: 简化开发
  - Swagger OpenAPI 3: 接口文档
  - State Machine: 状态流转
  - JWT: 身份认证
  - Redis: 缓存服务

### 前端技术栈
- **核心框架**: Vue 3 (JavaScript)
- **路由管理**: Vue Router 4
- **状态管理**: Pinia
- **UI框架**: Element Plus
- **HTTP客户端**: Axios
- **图表库**: ECharts 5
- **构建工具**: Vite
- **包管理器**: Yarn

## 项目结构
```
community-pension-system/
├── community-pension-admin/          # 后端服务
│   ├── src/
│   |   ├── main/
│   |   │   ├── java/
│   │   │   │   ├── com/
│   │   │   │   │   ├── community/
│   │   │   │   │   │   ├── admin/
│   │   │   │   │   │   │   ├── config/
│   │   │   │   │   │   │   ├── controller/
│   │   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   │   ├── entity/
│   │   │   │   │   │   │   ├── enums/
│   │   │   │   │   │   │   ├── exception/
│   │   │   │   │   │   │   ├── mapper/
│   │   │   │   │   │   │   ├── query/
│   │   │   │   │   │   │   ├── service/
│   │   │   │   │   │   │   ├── utils/
│   │   │   │   │   │   │   ├── vo/
│   |   │   ├── resources/
│   |   │   │   ├── mapper/
│   |   │   │   ├── static/
│   |   │   │   │   ├── sql/
│   |   │   │   │   │   ├── community_pension.sql
│   |   │   │   ├── templates/
│   |   │   │   ├── application.yml
│   |   │   │   ├── application-dev.yml
│   |   ├── test/
│
├── community-pension-frontend/       # 前端项目
│   ├── src/
│   |  ├── api/
│   |  ├── assets/
│   |  ├── components/
│   |  ├── router/
│   |  ├── stores/
│   |  ├── utils/
│   |  ├── views/
│   |  ├── App.vue
│   |  ├── main.js
│   ├── public/
│   ├── package.json
│   ├── vite.config.ts
│   └── index.html
│
└── docs/                            # 项目文档
    └── deployment/                  # 部署文档
```

## 快速开始

### 环境要求
- JDK: 17+
- Node.js: 16+
- MySQL: 8.4.0
- Redis: 6
- Maven: 3.8.8
- Yarn: 1.22+

### 开发环境搭建
1. 克隆项目
```bash
git clone https://github.com/hj01857655/community-pension-system.git
```

2. 后端服务启动
```bash
cd community-pension-admin
mvn spring-boot:run
```

3. 前端项目启动
```bash
cd community-pension-frontend
yarn install
yarn dev
```

## 部署指南
详见 [部署文档](./docs/deployment/README.md)

## 贡献指南
1. Fork 本仓库
2. 创建特性分支
3. 提交代码
4. 创建 Pull Request

## 版本历史
- v1.0.0 (2024-03) - 初始版本发布

## 许可证
[MIT](LICENSE)

## 联系方式
- 项目负责人：[liaojun2719]
- 邮箱：[hj6395759@gmail.com]



