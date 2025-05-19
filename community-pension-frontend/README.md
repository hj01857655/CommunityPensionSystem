# 社区养老系统 (Community Pension System)

## 项目介绍
基于 Spring Boot 3 和 Vue 3 的前后端分离架构社区养老管理系统。本系统旨在为社区老年人提供便捷的养老服务管理平台，包括健康监测、服务预约、活动管理等功能，提升社区养老服务质量和管理效率。

### 项目特色
- 智能健康监测：支持实时健康数据采集和异常预警
- 便捷服务预约：提供在线服务预约和评价功能
- 活动管理：支持社区活动发布、报名和签到
- 权限管理：基于RBAC的细粒度权限控制

## 功能特性

### 前台门户（老人、家属）

#### 1. 用户中心
- 账号管理：登录、密码修改
- 个人信息管理
- 个人信息管理（分组展示）
  - 基本资料
    - 用户名
    - 姓名
    - 性别
    - 手机号码
    - 电子邮件
    - 头像
    - 居住地址
  - 角色信息（根据用户角色动态显示）
    - 老人信息组
      - 身份证号码
      - 出生日期
      - 年龄
      - 健康状况
      - 紧急联系人信息
      - 已绑定家属
    - 家属信息组
      - 绑定老人
      - 与老人关系
  - 账号安全
    - 密码修改
    - 账号状态
- 绑定家庭成员

#### 2. 智能首页
- 天气信息展示
- 服务预约进度追踪
- 健康档案概览
- 活动参与情况统计
- 通知公告展示
- 一键紧急呼叫功能

#### 3. 服务预约
- 服务列表浏览
- 在线预约服务
- 服务记录查询

#### 4. 健康监测管理
- 健康档案首次录入
- 实时健康监测

#### 5. 社区活动
- 活动信息浏览
- 在线活动报名
- 活动签到打卡

#### 6. 消息中心
- 通知公告查看
- 系统消息(已读设置)
- 服务通知提醒

### 后台管理（管理员）

#### 1. 系统设置
- 用户管理（头部工具栏：新增、编辑、删除、查询、导出、导入、表格工具栏：编辑、删除、更多操作<重置密码>）
- 角色管理(头部工具栏：新增、编辑、删除、查询、导出、导入、表格工具栏：编辑、删除、更多操作<分配菜单>)
  - ①角色：老人、家属、工作人员、管理员
  - ②基于RBAC的权限控制
- 菜单管理（树形结构）
  菜单名称（树形结构）、图标、排序、权限标识、组件路径、状态、

- 字典管理
 -  字典列表(包含字典数据)

#### 2. 服务预约管理
- 服务项目管理
  - 服务列表：支持服务项目的展示、搜索和筛选
  - 服务信息管理：
    - 服务名称
    - 服务类型
    - 服务描述
    - 服务价格
    - 服务状态（上架/下架）
  - 服务操作：
    - 新增服务
    - 编辑服务
    - 删除服务
    - 查看详情
  - 数据展示：
    - 创建时间
    - 创建者
    - 更新时间
    - 更新者
    - 备注信息
- 服务工单管理（工单流程管理）
- 服务评价管理
#### 3. 健康监测管理
- 健康档案管理
  - 基础档案管理（基本信息、既往病史、遗传史、过敏史、）
    - 体检记录
    - 体检报告上传
    - 体检结果分析
- 健康监测管理
  - 生命体征监测（血压、血糖、心率、体温）：支持接入第三方智能设备实时监测
  - 异常预警管理
    - 预警记录查看
    - 预警处理流程

#### 4. 社区活动管理
- 活动管理
  - 活动列表：支持活动信息的展示、搜索和筛选
  - 活动发布：发布新的社区活动，包含标题、描述、时间、地点等信息
  - 活动编辑：修改活动信息，包括基本信息、时间安排等
  - 活动状态管理：管理活动状态（筹备中、报名中、进行中、已结束、已取消）
  - 活动详情查看：查看活动的详细信息，包括参与情况等
- 活动报名管理
  - 报名记录：查看所有活动的报名记录
  - 报名审核：审核老人的活动报名申请
  - 报名统计：统计活动的报名人数、报名率等数据
  - 报名导出：导出活动报名记录数据

#### 5. 通知公告管理
- 通知列表、发布

## 技术架构

### 关键技术实现
- 基于ECharts的数据可视化看板
- RBAC权限控制系统
- 智能服务预约系统
- 实时健康监测预警机制

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
│   ├── pom.xml                      # Maven配置
│   ├── application.yml              # 应用配置
│   ├── application-dev.yml          # 开发环境配置
│   ├── application-prod.yml         # 生产环境配置
│
├── community-pension-frontend/       # 前端项目
│   ├── src/

│   ├── public/
│   ├── package.json
│   ├── vite.config.ts
│   └── index.html
│
└── docs/                            # 项目文档
    ├── api/                         # API文档
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
git clone https://github.com/yourusername/community-pension-system.git
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
- 邮箱：[1299027544@qq.com]

## 前端开发规范

### 1. 项目结构
```
src/
  ├── api/                # API接口目录
  │   ├── back/          # 后台接口
  │   └── fore/          # 前台接口
  ├── assets/            # 静态资源
  ├── components/        # 公共组件
  ├── stores/            # 状态管理
  ├── utils/             # 工具函数
  ├── views/             # 页面组件
  ├── router/            # 路由配置
  ├── constants/         # 常量定义
  ├── hooks/             # 组合式函数
  ├── locales/           # 国际化
  ├── App.vue            # 根组件
  ├── main.js            # 入口文件
  └── env.js             # 环境配置
```

### 2. Store 开发规范

#### 2.1 基本结构
使用组合式 API 风格定义 store：

```javascript
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useStore = defineStore('storeName', () => {
  // 1. 状态定义
  const state = ref([]);
  const loading = ref(false);
  
  // 2. 重置状态方法
  const resetState = () => {
    state.value = [];
    loading.value = false;
  };
  
  // 3. 业务方法
  const getList = async () => {
    loading.value = true;
    try {
      // 业务逻辑
    } finally {
      loading.value = false;
    }
  };
  
  return {
    // 状态
    state,
    loading,
    
    // 方法
    resetState,
    getList
  };
});
```

#### 2.2 命名规范
- Store 文件名：使用小写字母，多个单词用连字符（-）连接，如 `user-profile.js`
- Store 名称：使用 `use` 前缀，如 `useUserProfileStore`
- 状态变量：使用驼峰命名，如 `userList`、`loading`
- 方法名：使用动词开头，如 `getList`、`updateUser`

#### 2.3 状态管理
- 使用 `ref` 定义响应式状态
- 状态变量名要语义化，避免使用 `data`、`list` 等通用名称
- 复杂状态考虑使用 `reactive` 对象组织
- 必须提供 `resetState` 方法重置所有状态

#### 2.4 方法规范
- 异步方法使用 `async/await`
- 统一错误处理，使用 `ElMessage` 提示
- 方法返回值统一为 `Promise`
- 成功返回数据，失败返回 `null`

#### 2.5 错误处理
```javascript
try {
  const res = await api.getData();
  if (res.code === 200) {
    ElMessage.success('操作成功');
    return res.data;
  } else {
    ElMessage.error(res.message || '操作失败');
    return null;
  }
} catch (error) {
  console.error('操作失败:', error);
  ElMessage.error('操作失败');
  return null;
}
```

#### 2.6 模块化
- 按功能模块拆分 store
- 相关功能放在同一个 store 中
- 避免 store 之间的循环依赖
- 使用 `index.js` 统一导出

#### 2.7 性能优化
- 合理使用计算属性
- 避免不必要的状态更新
- 大数据列表使用分页
- 及时清理不需要的状态

#### 2.8 类型支持
- 使用 JavaScript 定义接口
- 为状态和方法添加类型注解
- 导出类型定义供组件使用

### 3. 组件开发规范
// ... 其他前端规范内容 ...



