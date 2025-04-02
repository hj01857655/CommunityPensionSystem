# 前端开发规范文档

## 1. 项目结构
```
src/
  ├── api/           # API 接口
  ├── assets/        # 静态资源
  ├── components/    # 公共组件
  ├── hooks/         # 自定义 hooks
  ├── layouts/       # 布局组件
  ├── router/        # 路由配置
  ├── store/         # 状态管理
  ├── styles/        # 全局样式
  ├── utils/         # 工具函数
  └── views/         # 页面组件
```

## 2. 开发环境规范

### 2.1 Node.js 版本
- 使用 Node.js 16+ 版本
- 推荐使用 yarn 作为包管理工具

### 2.2 代码规范
- 使用 ESLint + Prettier 进行代码格式化
- 遵循 Vue3 组件命名规范
- 使用 JavaScript (ES6+) 进行开发
- 使用 JSDoc 注释提供类型提示

### 2.3 组件开发规范
- 组件名使用大驼峰命名（PascalCase）
- 文件名与组件名保持一致
- 必须使用组合式API (Composition API)
- 示例：
  ```vue
  <!-- UserProfile.vue -->
  <template>
    <div class="user-profile">
      <h2>{{ userInfo.name }}</h2>
      <div class="user-profile__content">
        <p>{{ userInfo.email }}</p>
        <button @click="handleUpdate">更新信息</button>
      </div>
    </div>
  </template>

  <script setup>
  import { ref, onMounted } from 'vue'
  import { useUserStore } from '@/stores/user'

  // 状态定义
  const userInfo = ref({
    name: '',
    email: ''
  })

  // store使用
  const userStore = useUserStore()

  // 方法定义
  const handleUpdate = async () => {
    await userStore.updateUserInfo(userInfo.value)
  }

  // 生命周期
  onMounted(async () => {
    userInfo.value = await userStore.getUserInfo()
  })
  </script>

  <style lang="scss" scoped>
  .user-profile {
    &__content {
      margin-top: 20px;
    }
  }
  </style>
  ```

- 组件结构规范：
  1. `<script setup>` 中的代码组织顺序：
     - 组件导入
     - 工具函数导入
     - 组合式函数（hooks）导入
     - props/emits 定义
     - 响应式数据定义
     - 组合式函数使用
     - 计算属性定义
     - 方法定义
     - 监听器定义
     - 生命周期钩子

  2. 使用组合式函数（hooks）抽取复用逻辑
  3. 使用 `defineProps` 和 `defineEmits` 定义属性和事件
  4. 使用 `defineExpose` 暴露组件属性和方法

- 组件通信规范：
  ```vue
  <script setup>
  // props定义
  const props = defineProps({
    title: {
      type: String,
      required: true,
      default: ''
    }
  })

  // emits定义
  const emit = defineEmits(['update', 'delete'])

  // 暴露方法
  defineExpose({
    someMethod: () => {
      // 方法实现
    }
  })
  </script>
  ```

### 2.4 样式规范
- 使用 SCSS 预处理器
- BEM 命名规范
- 示例：
  ```scss
  .block {
    &__element {
      &--modifier {
        // 样式
      }
    }
  }
  ```

### 2.5 API 接口规范
- 统一使用封装的 axios 实例
- 接口请求统一管理在 api 目录下
- 按照功能模块划分目录结构：
  ```
  api/
  ├── back/                # 后台管理接口
  │   ├── system/         # 系统管理模块
  │   ├── service/        # 服务管理模块
  │   ├── activity/       # 活动管理模块
  │   ├── health/         # 健康管理模块
  │   ├── notice/         # 通知公告模块
  │   └── login.js        # 登录相关接口
  └── fore/               # 前台展示接口
  ```

- 接口文件命名规范：
  - 使用小写字母
  - 使用语义化名称
  - 按功能模块命名，如：`user.js`, `role.js`
  - 通用功能可直接放在模块根目录，如：`login.js`

- 接口定义规范：
  ```js
  import axios from '@/utils/axios'

  /**
   * 用户信息管理相关接口
   */

  /**
   * 分页获取用户列表
   * @param {Object} query - 查询参数
   * @param {number} [query.current=1] - 当前页码
   * @param {number} [query.size=10] - 每页显示条数
   * @param {string} [query.username] - 用户名（可选）
   * @returns {Promise<{code: number, data: {records: Array<Object>, total: number}, msg: string}>}
   */
  export const getUserList = query => {
    return axios.get('/api/system/user/list', {
      params: {
        current: query.current || 1,
        size: query.size || 10,
        username: query.username || ''
      }
    })
  }

  /**
   * 获取用户详细信息
   * @param {number} userId - 用户ID
   * @returns {Promise<Object>}
   */
  export const getUserInfo = userId => {
    return axios.get(`/api/system/user/${userId}`)
  }

  /**
   * 更新用户信息
   * @param {number} userId - 用户ID
   * @param {Object} data - 用户信息
   * @returns {Promise<{code: number, msg: string}>}
   */
  export const updateUser = (userId, data) => {
    return axios.put(`/api/system/user/${userId}`, data)
  }
  ```

- 接口规范要求：
  1. 必须使用JSDoc注释说明接口用途、参数和返回值
  2. 参数类型必须明确标注
  3. 可选参数必须标注默认值
  4. 返回值类型必须明确标注
  5. 使用 ES6+ 的箭头函数语法
  6. 统一使用 RESTful 风格的 API
  7. URL 路径必须语义化，使用kebab-case命名
  8. 请求方法规范：
     - GET：查询数据
     - POST：新增数据
     - PUT：更新数据
     - DELETE：删除数据

- 错误处理规范：
  - 统一在axios实例中处理通用错误
  - 特殊错误在具体业务代码中处理
  - 必须处理异步请求的异常情况

- 数据处理规范：
  - 请求参数必须进行数据校验
  - 响应数据必须进行类型检查
  - 分页接口必须处理页码和条数默认值
  - 文件上传必须指定正确的Content-Type

### 2.6 状态管理规范
- 使用 Pinia 进行状态管理
- Store 按模块划分
- 使用组合式API方式定义store
- 示例：
  ```js
  // store/user.js
  import { defineStore } from 'pinia'
  import { ref, computed } from 'vue'

  export const useUserStore = defineStore('user', () => {
    // 状态
    const userInfo = ref(null)
    
    // 计算属性
    const isLoggedIn = computed(() => !!userInfo.value)
    
    // actions
    function setUserInfo(info) {
      userInfo.value = info
    }

    return {
      userInfo,
      isLoggedIn,
      setUserInfo
    }
  })
  ```

### 2.7 路由规范
- 路由配置集中管理
- 使用路由守卫进行权限控制
- 示例：
  ```js
  // router/index.js
  import { createRouter } from 'vue-router'

  const routes = [
    {
      path: '/user',
      component: () => import('@/views/user/index.vue'),
      meta: {
        title: '用户管理',
        auth: true
      }
    }
  ]
  ```

### 2.8 Git 提交规范
- 使用 commitlint 规范提交信息
- 提交格式：`type(scope): message`
- type 类型：
  - feat: 新功能
  - fix: 修复
  - docs: 文档
  - style: 格式
  - refactor: 重构
  - test: 测试
  - chore: 其他

## 3. 构建部署规范
- 使用 Vite 作为构建工具
- 区分开发环境和生产环境配置
- 生产环境进行代码压缩和优化
