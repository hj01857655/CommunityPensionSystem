# 开发规范文档

## 1. 项目开发规范概述

### 1.1 开发环境要求
- JDK: 17+
- Node.js: 16+
- MySQL: 8.4.0
- Redis: 6
- Maven: 3.8.8
- Yarn: 1.22+

### 1.2 开发工具推荐
- IDE: IntelliJ IDEA / VS Code
- 数据库工具: Navicat / DataGrip
- API测试: Postman
- 版本控制: Git

### 1.3 开发流程规范
1. 从 dev 分支创建功能分支
2. 在功能分支上开发
3. 提交代码前进行代码审查
4. 合并到 dev 分支
5. 定期合并到 main 分支

## 2. 后端开发规范

## 3. 前端开发规范

### 3.1 项目结构
```
src/
  ├── api/                # API接口目录
  │   ├── back/          # 后台接口
  │   │   ├── system/    # 系统管理
  │   │   ├── notice/    # 通知管理
  │   │   ├── activity/  # 活动管理
  │   │   └── elder/     # 老人管理
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

### 3.2 组件开发规范

#### 3.2.1 组件命名规范
- 文件名：使用 PascalCase，如 `UserProfile.vue`
- 组件名：使用 PascalCase，如 `UserProfile`
- 基础组件：以 Base 开头，如 `BaseButton.vue`
- 业务组件：以业务模块命名，如 `ActivityList.vue`

#### 3.2.2 组件结构规范
```vue
<template>
  <div class="component-name">
    <!-- 模板内容 -->
  </div>
</template>

<script setup>
// 1. 导入语句
import { ref, onMounted } from 'vue'
import { useStore } from '@/stores/storeName'

// 2. 组件名称
defineOptions({
  name: 'ComponentName'
})

// 3. 属性定义
const props = defineProps({
  title: {
    type: String,
    required: true
  }
})

// 4. 事件定义
const emit = defineEmits(['update', 'delete'])

// 5. 响应式数据
     const loading = ref(false)
const data = ref([])

// 6. 计算属性
const computedData = computed(() => {
  return data.value.filter(item => item.status === 1)
})

// 7. 方法定义
const handleSubmit = async () => {
       loading.value = true
       try {
    // 业务逻辑
       } finally {
         loading.value = false
       }
     }

// 8. 生命周期钩子
onMounted(() => {
  // 初始化逻辑
})
</script>

<style lang="scss" scoped>
.component-name {
  // 样式定义
}
</style>
```

#### 3.2.3 组件通信规范
1. Props 传递：
   - 使用 TypeScript 类型定义
   - 必须指定默认值
   - 复杂对象使用 v-bind

2. 事件通信：
   - 使用 emit 发送事件
   - 事件名使用 kebab-case
   - 携带必要的数据

3. 状态管理：
   - 全局状态使用 Pinia
   - 组件状态使用 ref/reactive
   - 避免状态冗余

### 3.3 Store 开发规范

### 3.4 路由开发规范

#### 3.4.1 路由配置
   ```javascript
// router/index.js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: {
          title: '首页',
          icon: 'home'
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

#### 3.4.2 路由命名规范
- 路由名称：使用 PascalCase
- 路由路径：使用 kebab-case
- 组件路径：使用相对路径

#### 3.4.3 路由元信息
   ```javascript
{
  meta: {
    title: '页面标题',
    icon: '图标名称',
    roles: ['admin', 'user'],
    permissions: ['system:user:view'],
    keepAlive: true,
    hidden: false
  }
}
```

### 3.5 API 开发规范

#### 3.5.1 API 接口文件规范
   ```javascript
// api/back/activity/checkin.js
import axios from '@/utils/axios'

/**
 * 获取签到记录列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页条数
 * @param {string|number} params.activityId - 活动ID
 * @param {string} params.status - 签到状态
 * @param {string} params.name - 参与者姓名
 * @param {string} params.phone - 联系电话
 * @param {string} params.startTime - 开始时间
 * @param {string} params.endTime - 结束时间
 * @returns {Promise<{
 *   code: number,
 *   data: {
 *     records: Array<{
 *       id: number,
 *       activityId: number,
 *       participantId: number,
 *       status: number,
 *       checkinTime: string,
 *       remark: string
 *     }>,
 *     total: number
 *   },
 *   msg: string
 * }>}
 */
export const getList = (params) => {
  return axios.get('/api/activity/checkin/list', { params })
}

/**
 * 签到
 * @param {Object} data - 签到数据
 * @param {string|number} data.activityId - 活动ID
 * @param {string|number} data.participantId - 参与者ID
 * @param {string} data.checkinTime - 签到时间
 * @param {string} data.remark - 签到备注
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const create = (data) => {
  return axios.post('/api/activity/checkin', data)
}
```

#### 3.5.2 API 开发规范要点
1. 文件组织
   - 按模块划分 API 文件
   - 前台接口放在 `api/fore` 目录
   - 后台接口放在 `api/back` 目录
   - 每个模块一个文件，如 `user.js`、`activity.js`

2. 接口命名
   - 使用动词+名词的形式
   - 获取列表：getList
   - 获取详情：getDetail
   - 创建：create
   - 更新：update
   - 删除：delete
   - 批量操作：batchCreate、batchUpdate、batchDelete

3. 接口路径规范
   - 所有接口必须以 `/api` 开头
   - 前台接口：`/api/操作名`（如：`/api/login`、`/api/activity/list`）
   - 后台接口：`/api/模块名/操作`（如：`/api/system/user/list`）
   - 示例：
     ```javascript
     // 前台接口
     /api/login              // 用户登录
     /api/activity/list      // 活动列表
     /api/notice/list        // 通知列表
     
     // 后台接口
     /api/system/user/list   // 用户列表
     /api/activity/checkin   // 签到管理
     ```

4. 参数规范
   - 查询参数使用 params
   - 请求体数据使用 data
   - 路径参数使用模板字符串
   - 文件上传使用 FormData

5. 类型注释
   - 使用 JSDoc 注释
   - 详细描述参数类型
   - 描述返回值类型
   - 说明可能的错误情况

6. 错误处理
   - 统一在 axios 实例中处理
   - 业务错误在组件中处理
   - 网络错误统一提示
   - 权限错误跳转登录

7. 接口文档
   - 保持与后端接口文档一致
   - 及时更新接口变更
   - 记录接口废弃情况
   - 标注接口权限要求

8. 接口设计参考
   - 请求头设置
     ```javascript
     headers: {
       'Content-Type': 'application/json;charset=UTF-8',
       'Accept': 'application/json'
     }
     ```
   - 登录接口特殊处理
     ```javascript
     const loginPaths = ['/api/auth/login', '/api/auth/adminLogin'];
     if (loginPaths.some(path => config.url === path)) {
       return config;
     }
     ```
   - 权限验证
     ```javascript
     // 根据路径判断用户类型
     const isAdmin = window.location.pathname.includes('/admin/');
     const token = isAdmin ? 
       TokenManager.admin.getAccessToken() : 
       TokenManager.user.getAccessToken();
     ```
   - 响应数据处理
     ```javascript
     if (response.status === 200) {
       const responseData = response.data;
       // 处理 token 刷新
       if (responseData.data) {
         const { accessToken, refreshToken } = responseData.data;
         if (accessToken && refreshToken) {
           // 更新 token
         }
       }
       return responseData;
     }
     ```
   - 错误状态码处理
     ```javascript
     const errorHandler = {
       400: () => '请求参数错误',
       401: () => '认证失败，请重新登录',
       403: () => '权限不足，请联系管理员',
       404: () => '资源不存在',
       500: () => '服务器繁忙，请稍后重试',
       default: () => error.response?.data?.message || '未知错误'
     };
     ```

### 3.6 样式开发规范

#### 3.6.1 样式命名
- 使用 BEM 命名规范
- 组件样式使用 scoped
- 全局样式使用 common.scss

#### 3.6.2 样式组织
```scss
// variables.scss
$primary-color: #409EFF;
$success-color: #67C23A;
$warning-color: #E6A23C;
$danger-color: #F56C6C;
$info-color: #909399;

// mixins.scss
@mixin flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

// common.scss
.flex-center {
  @include flex-center;
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
```

## 4. Maven 依赖管理规范

## 5. Git 开发规范及提交记录