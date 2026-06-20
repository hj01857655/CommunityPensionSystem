# 社区养老系统前端 React 迁移重构文档

## 1. 项目概述

### 1.1 项目背景
社区养老系统（Community Pension System）当前前端采用 **Vue 3 + Element Plus** 技术栈，为提升项目可维护性、团队技术栈统一性和生态系统支持，决定将前端迁移至 **React + TypeScript** 技术栈。

### 1.2 迁移目标
- 将现有 Vue 3 前端完整迁移到 React 18
- 保持现有业务逻辑和功能完整性
- 提升代码质量和类型安全
- 优化用户体验和性能

### 1.3 当前技术栈
**原技术栈（Vue）：**
- 框架：Vue 3.2
- 构建工具：Vite 6.2
- UI 组件库：Element Plus 2.9
- 状态管理：Pinia 2.3
- 路由：Vue Router 4
- HTTP 客户端：Axios 1.8
- 图表：ECharts 5.6 + Vue-ECharts 7.0
- 样式：SASS
- 工具库：dayjs, js-cookie, jwt-decode 等

**目标技术栈（React - 2026 最佳实践）：**
- **框架**：React 18.3（过渡期）→ React 19.x（稳定后）
- **语言**：TypeScript 5.3+（strict mode）
- **构建工具**：Vite 6.2+（最新稳定版）
- **UI 组件库**：Ant Design 5.12+（企业级组件）
- **样式方案**：Tailwind CSS 3.4+（布局与工具类）
- **状态管理**：Zustand 4.5+（轻量级状态管理）
- **路由**：React Router 6.28+（最新稳定版）
- **HTTP 客户端**：Axios 1.7+（保持一致）
- **图表库**：ECharts 5.5 + echarts-for-react 3.0
- **表单处理**：Ant Design Form（内置，无需额外库）
- **日期处理**：dayjs 1.11+（Ant Design 依赖）
- **Token 存储**：内存（Zustand）+ HttpOnly Cookie（最佳安全实践）
- **代码规范**：ESLint 9+ + Prettier 3+ + Husky 9+
- **测试框架**：Vitest 4.1+ + React Testing Library 16+
- **国际化**：react-i18next 15.0+
- **错误监控**：Sentry 8.0+
- **性能监控**：Web Vitals 4+

**重要说明：**
- ⚠️ **React 18.3 → 19.x 升级路径**：先使用 React 18.3（最稳定），待 Ant Design 完全适配 React 19 后再升级
- ✅ **Vite 6.2**：相比 Vite 5 有显著性能提升（HMR 速度提升 30%）
- ✅ **所有版本号均为 2026 年 6 月的最新稳定版**

---

## 2. 系统架构分析

### 2.1 现有系统模块

根据路由配置分析，系统分为**前台用户端**和**管理后台**两大部分：

#### 2.1.1 前台用户端（Elder & Kin）
- **登录模块**：`/login`
- **首页仪表盘**：`/home/dashboard`
- **健康中心**：`/home/health`
- **服务预约**：`/home/service`
- **社区活动**：`/home/activity`（包含活动列表、活动详情、报名功能）
- **通知公告**：`/home/notice`（通知列表、通知详情）
- **个人中心**：`/home/profile`

**用户角色：**
- `elder`：老年人用户
- `kin`：亲属用户

#### 2.1.2 管理后台（Admin & Staff）
- **登录模块**：`/admin/login`
- **首页**：`/admin/home`
- **系统管理中心**：`/admin/system`
  - 用户管理
  - 角色管理
  - 菜单管理
  - 字典管理
  - 系统设置
  - 个人中心
- **社区活动中心**：`/admin/activity`
  - 活动管理（发布和管理活动）
  - 活动报名管理
  - 活动签到管理
- **服务管理中心**：`/admin/services`
  - 服务项目管理
  - 服务工单管理
- **健康管理中心**：`/admin/health`
  - 健康档案管理
  - 健康监测管理
  - 体检报告管理
- **通知公告**：`/admin/notices`
  - 通知列表
  - 通知发布
- **通知中心**：`/admin/notifications`（系统通知）
- **消息中心**：`/admin/messages`（站内消息）

**用户角色：**
- `admin`：系统管理员
- `staff`：工作人员

#### 2.1.3 错误页面
- 403：无权限访问
- 404：页面不存在
- 500：服务器错误

### 2.2 认证与授权机制

系统采用 **Access Token + Refresh Token（双 Token 机制）** 结合 **Dual-Portal Token Separation（双门户 Token 分离）** 的认证架构。

#### 2.2.1 Token 架构设计

**核心机制：**
1. **Access Token + Refresh Token**
   - **Access Token**：短期访问令牌（建议 15-30 分钟过期，用于 API 请求）
   - **Refresh Token**：长期刷新令牌（建议 7-30 天过期，用于静默刷新）
   - **Token Rotation**：刷新时旧的 Refresh Token 失效，生成新的 Token Pair
   - **Token Blacklist**：已使用的 Refresh Token 加入黑名单，防止重放攻击
   - **Reuse Detection**：检测 Refresh Token 是否被重复使用（可能表示被盗）

2. **Dual-Portal Token Separation（双门户 Token 分离）**
   - **Admin Portal Token**：管理员端独立 Token 体系（当前存储在 `sessionStorage`）
   - **User Portal Token**：用户端独立 Token 体系（当前存储在 `localStorage`）
   - 两套 Token 互不干扰，实现门户级别的认证隔离

#### ⚠️ 2.2.2 当前实现的安全风险

**存储位置风险：**
- ❌ **localStorage/sessionStorage 易受 XSS 攻击**：任何恶意脚本都可以读取 Token
- ❌ **没有过期机制**：即使浏览器关闭，Token 仍然存在（localStorage）
- ❌ **明文存储**：Token 以明文形式存储，没有加密

**业界最佳实践（2024-2026）：**
- ✅ 使用 **HttpOnly Cookies** 存储 Refresh Token（JavaScript 无法读取）
- ✅ 使用 **Secure** 标志（仅通过 HTTPS 传输）
- ✅ 使用 **SameSite=Strict** 防止 CSRF 攻击
- ✅ Access Token 可以存储在内存中（React state/Zustand），页面刷新时通过 Refresh Token 重新获取

**推荐的改进方案：**
```typescript
// 推荐：Refresh Token 存储在 HttpOnly Cookie
// 后端设置 Cookie
res.cookie('refreshToken', token, {
  httpOnly: true,      // 防止 XSS
  secure: true,        // 仅 HTTPS
  sameSite: 'strict',  // 防止 CSRF
  maxAge: 7 * 24 * 60 * 60 * 1000  // 7天
});

// 前端：Access Token 存储在内存（Zustand store）
interface AuthStore {
  accessToken: string | null;
  setAccessToken: (token: string) => void;
}

export const useAuthStore = create<AuthStore>((set) => ({
  accessToken: null,
  setAccessToken: (token) => set({ accessToken: token })
}));
```

#### 2.2.3 前台认证（User Portal Token）
**当前实现：**
- **存储位置**：`localStorage`（⚠️ 存在 XSS 风险）
- **Access Token**：`localStorage.getItem('user-access-token')`
- **Refresh Token**：`localStorage.getItem('user-refresh-token')`
- **登录状态标识**：`localStorage.getItem('isLoggedIn')`
- **用户信息**：`localStorage.getItem('userInfo')`
- **角色验证**：检查 `roles` 数组是否包含 `elder` 或 `kin`

**建议改进为：**
- **Refresh Token** → HttpOnly Cookie
- **Access Token** → 内存存储（Zustand store）
- **用户信息** → sessionStorage 或内存

#### 2.2.4 后台认证（Admin Portal Token）
**当前实现：**
- **存储位置**：`sessionStorage`（⚠️ 存在 XSS 风险）
- **Token 管理**：`TokenManager.admin.getAccessToken()`
- **Access Token**：通过请求头 `Authorization: Bearer <token>` 发送
- **Refresh Token**：通过请求头 `Refresh-Token: <token>` 发送
- **用户信息**：`sessionStorage.getItem('admin-user-info')`
- **角色验证**：检查路由 `meta.roles` 与用户角色匹配

**建议改进为：**
- **Refresh Token** → HttpOnly Cookie
- **Access Token** → 内存存储（Zustand store）
- **用户信息** → sessionStorage（相对安全）

#### 2.2.5 Token 刷新流程（含重放检测）
1. 前端请求时携带 Access Token（从内存读取）
2. 后端验证 Access Token 失效（401）
3. 前端自动使用 Refresh Token（从 Cookie 自动发送）请求新 Token
4. 后端验证 Refresh Token 并检查：
   - ✅ Token 是否在黑名单中
   - ✅ Token 是否已被使用（Reuse Detection）
   - ✅ 如果检测到重放，可能表示 Token 被盗，应立即失效所有该用户的 Token
5. 后端生成新的 Token Pair 并将旧 Refresh Token 加入黑名单
6. 后端返回新 Access Token（响应体）和新 Refresh Token（HttpOnly Cookie）
7. 前端接收新 Access Token 存入内存并重试原请求

#### 2.2.6 Route Guard（路由守卫）逻辑
1. **Public Routes（公开路由）**：错误页面、登录页直接放行
2. **Admin Routes（后台路由）** (`/admin/*`)：
   - 检查 Admin Access Token
   - 验证用户角色权限（RBAC - Role-Based Access Control）
   - Token 失效时自动尝试使用 Refresh Token 刷新
   - 失败则跳转 `/admin/login` 或 `/403`
3. **User Routes（前台路由）** (`/home/*`)：
   - 检查 User Access Token 和登录状态
   - 验证角色（elder/kin）权限
   - Token 失效时自动尝试使用 Refresh Token 刷新
   - 失败则跳转 `/login` 或 `/403`

### 2.3 状态管理

#### 2.3.1 已识别的 Store
- `tagsViewStore`：后台标签页视图管理（Pinia）
- 其他状态管理待进一步分析源码

---

## 3. 迁移策略

### 3.1 迁移原则
1. **Incremental Migration（渐进式迁移）**：采用 Module-by-Module 分模块迁移策略，先完成基础框架，再逐步迁移各业务模块
2. **Feature Parity（功能对等）**：确保迁移后功能与原系统完全一致，保持 100% 业务逻辑兼容性
3. **Bottom-Up Approach（自底向上）**：优先迁移公共组件、工具函数和基础设施，再迁移业务页面
4. **Parallel Development（并行开发）**：迁移过程中保持原系统正常运行，采用 Feature Flag 控制切换

### 3.2 迁移阶段

#### 第一阶段：基础框架搭建（2-3天）
**目标：** 完成 React 项目脚手架和核心基础设施

**任务清单：**
1. ✅ 初始化 React + TypeScript + Vite 项目
2. ⬜ 集成 Ant Design 5.x
   - 安装依赖：`npm install antd`
   - 配置主题（ConfigProvider）
   - 按需引入配置
3. ⬜ 集成 Tailwind CSS
   - 安装依赖：`npm install -D tailwindcss postcss autoprefixer`
   - 初始化配置：`npx tailwindcss init -p`
   - 配置与 Ant Design 兼容
4. ⬜ 配置路由系统（React Router 6）
5. ⬜ 配置状态管理（Zustand）
6. ⬜ 集成 Axios 并配置拦截器
7. ⬜ 搭建项目目录结构
8. ⬜ 配置 ESLint + Prettier
9. ⬜ 配置主题系统（Ant Design + Tailwind 统一）

**目录结构设计：**
```
community-pension-frontend-react/
├── public/
├── src/
│   ├── api/                 # API 请求封装
│   ├── assets/              # 静态资源
│   ├── components/          # 公共组件
│   │   ├── common/          # 通用组件
│   │   ├── layout/          # 布局组件
│   │   └── business/        # 业务组件
│   ├── config/              # 配置文件
│   ├── constants/           # 常量定义
│   ├── hooks/               # 自定义 Hooks
│   ├── layouts/             # 页面布局
│   │   ├── FrontLayout/     # 前台布局
│   │   └── AdminLayout/     # 后台布局
│   ├── pages/               # 页面组件
│   │   ├── front/           # 前台页面
│   │   └── admin/           # 后台页面
│   ├── router/              # 路由配置
│   ├── store/               # 状态管理
│   ├── styles/              # 全局样式
│   ├── types/               # TypeScript 类型定义
│   ├── utils/               # 工具函数
│   ├── App.tsx
│   └── main.tsx
├── .eslintrc.js
├── .prettierrc
├── tsconfig.json
├── vite.config.ts
└── package.json
```

#### 第二阶段：公共模块迁移（3-4天）
**目标：** 完成通用组件、工具函数和核心服务的迁移

**任务清单：**
1. ⬜ 迁移 Axios 配置和 TokenManager
2. ⬜ 迁移认证相关工具（登录、Token 管理、权限验证）
3. ⬜ 迁移路由守卫逻辑
4. ⬜ 迁移通用组件：
   - ErrorPage（403/404/500）
   - Header/Footer
   - Loading/Spinner
   - 表单组件
   - 弹窗组件
5. ⬜ 迁移工具函数：
   - 日期格式化（dayjs）
   - Cookie 管理
   - JWT 解析
   - 数据导出（xlsx, jspdf, html2canvas）
6. ⬜ 迁移常量和类型定义

#### 第三阶段：前台页面迁移（5-7天）
**目标：** 完成前台用户端所有页面的迁移

**迁移顺序：**
1. ⬜ 登录页面 (`/login`)
2. ⬜ 前台布局组件 (`Home.vue` → `FrontLayout`)
3. ⬜ 首页仪表盘 (`/home/dashboard`)
4. ⬜ 个人中心 (`/home/profile`)
5. ⬜ 健康中心 (`/home/health`)
6. ⬜ 服务预约 (`/home/service`)
7. ⬜ 社区活动 (`/home/activity`, `/home/activity/:id`)
8. ⬜ 通知公告 (`/home/notice`, `/home/notice/:id`)

**关键点：**
- 保持 URL 路径不变
- 迁移 Pinia store 到 Zustand（如用户信息、通知状态等）
- 确保角色权限控制（elder, kin）正确实现

#### 第四阶段：后台页面迁移（7-10天）
**目标：** 完成管理后台所有页面的迁移

**迁移顺序：**
1. ⬜ 管理员登录页面 (`/admin/login`)
2. ⬜ 后台布局组件 (`index.vue` → `AdminLayout`)
3. ⬜ 标签页视图组件（TagsView）
4. ⬜ 首页仪表盘 (`/admin/home`)
5. ⬜ 系统管理中心 (`/admin/system/*`)
   - 用户管理
   - 角色管理
   - 菜单管理
   - 字典管理
   - 系统设置
6. ⬜ 社区活动中心 (`/admin/activity/*`)
   - 活动管理
   - 活动报名管理
   - 活动签到管理
7. ⬜ 服务管理中心 (`/admin/services/*`)
   - 服务项目管理
   - 服务工单管理
8. ⬜ 健康管理中心 (`/admin/health/*`)
   - 健康档案管理
   - 健康监测管理
   - 体检报告管理
9. ⬜ 通知公告 (`/admin/notices/*`)
10. ⬜ 通知中心 (`/admin/notifications`)
11. ⬜ 消息中心 (`/admin/messages`)

**关键点：**
- 实现复杂表格组件（过滤、排序、分页）
- 实现表单验证逻辑
- 迁移图表组件（ECharts）
- 保持后台权限控制逻辑（admin, staff）

#### 第五阶段：集成测试与优化（3-5天）
**目标：** 完成端到端测试和性能优化

**任务清单：**
1. ⬜ 功能测试（对比 Vue 版本）
2. ⬜ 跨浏览器兼容性测试
3. ⬜ 响应式布局测试（移动端、平板、桌面端）
4. ⬜ 性能优化：
   - 代码分割（React.lazy + Suspense）
   - 组件懒加载
   - 图片优化
   - 打包优化
5. ⬜ 无障碍性（Accessibility）检查
6. ⬜ SEO 优化（如需要）
7. ⬜ 错误边界（Error Boundary）实现
8. ⬜ 日志和监控集成

#### 第六阶段：部署与切换（1-2天）
**目标：** 部署 React 版本并切换生产环境

**任务清单：**
1. ⬜ 构建生产版本
2. ⬜ 配置生产环境变量
3. ⬜ 部署到测试环境
4. ⬜ UAT 测试
5. ⬜ 部署到生产环境
6. ⬜ 灰度发布（部分用户试用）
7. ⬜ 全量切换
8. ⬜ 监控和应急预案

---

## 4. 最终技术方案（已确定）

### 4.1 UI 组件库：Ant Design 5.12+ ✅

**最终选择：Ant Design 5.12+**

**核心理由：**
1. ✅ **迁移成本最低**：与 Element Plus 组件一一对应（Table、Form、Modal 等）
2. ✅ **最适合企业级管理后台**：60+ 组件专为后台场景设计
3. ✅ **中文生态完善**：文档、社区、案例全面
4. ✅ **用户习惯一致**：老年人和工作人员无需重新适应 UI 风格
5. ✅ **TypeScript 原生支持**：完整类型定义

**关键特性：**
- 强大的 Table 组件（排序、筛选、分页、可编辑、固定列）
- 完善的 Form 系统（验证、联动、动态字段、嵌套表单）
- 丰富的数据展示组件（Statistic、Card、Descriptions、Timeline）
- 内置国际化（中文、英文）

**组件映射表（Element Plus → Ant Design）：**

| 功能 | Element Plus | Ant Design | 迁移难度 |
|------|-------------|------------|---------|
| 数据表格 | el-table | Table | ⭐ 简单 |
| 表单 | el-form | Form | ⭐ 简单 |
| 日期选择 | el-date-picker | DatePicker | ⭐ 简单 |
| 下拉选择 | el-select | Select | ⭐ 简单 |
| 对话框 | el-dialog | Modal | ⭐⭐ 中等 |
| 消息提示 | el-message | message | ⭐ 简单 |
| 分页 | el-pagination | Pagination | ⭐ 简单 |
| 文件上传 | el-upload | Upload | ⭐⭐ 中等 |
| 按钮 | el-button | Button | ⭐ 简单 |
| 输入框 | el-input | Input | ⭐ 简单 |

---

### 4.2 样式方案：Tailwind CSS 3.3+ ✅

**最终选择：Tailwind CSS 3.3+**

**核心理由：**
1. ✅ **与 Ant Design 完美协作**：各司其职，不冲突
   - Ant Design：复杂组件的样式（Table、Form、Modal）
   - Tailwind：布局、间距、响应式、简单样式
2. ✅ **开发效率高**：Utility-first，无需命名 CSS 类
3. ✅ **体积优化**：生产环境自动 Tree Shaking，仅 10-30KB
4. ✅ **响应式简单**：`md:`、`lg:` 等断点前缀

**配置要点：**
```javascript
// tailwind.config.js
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  corePlugins: {
    preflight: false // 关键：避免与 Ant Design 样式冲突
  },
  theme: {
    extend: {
      colors: {
        primary: '#1890ff', // 与 Ant Design 主题色统一
      }
    }
  }
}
```

---

### 4.2.1 主题管理方案：Design Tokens + CSS Variables ✅

**最终选择：Design Tokens + CSS Variables（2026 年最佳实践）**

**核心理由：**
1. ✅ **运行时动态切换主题**：无需重新编译，秒切换
2. ✅ **性能最优**：浏览器原生 CSS Variables
3. ✅ **Bundle Size 更小**：比 Less/Sass 编译后体积小
4. ✅ **支持暗黑模式**：轻松实现 light/dark 切换
5. ✅ **统一 Ant Design + Tailwind**：共享同一套 Design Tokens

**架构设计：**

```
Design Tokens (tokens.ts)
    ↓
    ├→ Ant Design Theme (ConfigProvider)
    └→ Tailwind CSS (tailwind.config.js)
         ↓
    CSS Variables (:root)
```

**实现方案：**

**1. 定义 Design Tokens（核心）**
```typescript
// src/theme/tokens.ts
export const designTokens = {
  // Seed Tokens（基础 token）
  colors: {
    primary: '#1890ff',
    success: '#52c41a',
    warning: '#faad14',
    error: '#f5222d',
    info: '#1890ff',
    
    // 灰度色
    gray: {
      50: '#fafafa',
      100: '#f5f5f5',
      200: '#e8e8e8',
      300: '#d9d9d9',
      400: '#bfbfbf',
      500: '#8c8c8c',
      600: '#595959',
      700: '#434343',
      800: '#262626',
      900: '#1f1f1f',
    }
  },
  
  spacing: {
    xs: '8px',
    sm: '12px',
    md: '16px',
    lg: '24px',
    xl: '32px',
  },
  
  borderRadius: {
    sm: '2px',
    base: '4px',
    md: '6px',
    lg: '8px',
  },
  
  fontSize: {
    xs: '12px',
    sm: '14px',
    base: '16px',
    lg: '18px',
    xl: '20px',
    '2xl': '24px',
  }
} as const;

// Dark Mode Tokens
export const darkTokens = {
  colors: {
    primary: '#177ddc',
    gray: {
      50: '#1f1f1f',
      100: '#262626',
      // ... 反转灰度色
    }
  }
};
```

**2. Ant Design 配置（使用 CSS Variables）**
```typescript
// src/App.tsx
import { ConfigProvider, theme } from 'antd';
import { designTokens } from './theme/tokens';

function App() {
  const [isDark, setIsDark] = useState(false);
  
  return (
    <ConfigProvider
      theme={{
        // 🔥 关键：启用 CSS Variables 模式
        cssVar: { prefix: 'ant' },
        
        // Token 配置
        token: {
          colorPrimary: designTokens.colors.primary,
          colorSuccess: designTokens.colors.success,
          colorWarning: designTokens.colors.warning,
          colorError: designTokens.colors.error,
          borderRadius: 4,
          fontSize: 14,
        },
        
        // 🔥 关键：动态切换主题算法
        algorithm: isDark ? theme.darkAlgorithm : theme.defaultAlgorithm,
        
        // 组件级别定制
        components: {
          Button: {
            controlHeight: 32,
            borderRadius: 4,
          },
          Table: {
            headerBg: designTokens.colors.gray[50],
          }
        }
      }}
    >
      {/* 你的应用 */}
    </ConfigProvider>
  );
}
```

**3. Tailwind CSS 配置（共享 Tokens）**
```javascript
// tailwind.config.js
const { designTokens } = require('./src/theme/tokens');

module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  
  corePlugins: {
    preflight: false, // 关键：避免与 Ant Design 冲突
  },
  
  theme: {
    extend: {
      // 🔥 关键：从 Design Tokens 引入
      colors: {
        primary: designTokens.colors.primary,
        success: designTokens.colors.success,
        warning: designTokens.colors.warning,
        error: designTokens.colors.error,
        gray: designTokens.colors.gray,
      },
      
      spacing: designTokens.spacing,
      borderRadius: designTokens.borderRadius,
      fontSize: designTokens.fontSize,
    }
  },
  
  // 🔥 暗黑模式支持
  darkMode: 'class', // 或 'media'
}
```

**4. 全局 CSS Variables（可选，增强灵活性）**
```css
/* src/styles/variables.css */
:root {
  /* 从 Design Tokens 生成 */
  --color-primary: #1890ff;
  --color-success: #52c41a;
  --color-warning: #faad14;
  --color-error: #f5222d;
  
  --spacing-xs: 8px;
  --spacing-sm: 12px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  
  --border-radius-base: 4px;
}

[data-theme='dark'] {
  --color-primary: #177ddc;
  /* ... dark mode 变量 */
}
```

**5. 主题切换 Hook**
```typescript
// src/hooks/useTheme.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface ThemeStore {
  theme: 'light' | 'dark';
  toggleTheme: () => void;
  setTheme: (theme: 'light' | 'dark') => void;
}

export const useTheme = create<ThemeStore>()(
  persist(
    (set) => ({
      theme: 'light',
      toggleTheme: () => set((state) => {
        const newTheme = state.theme === 'light' ? 'dark' : 'light';
        document.documentElement.setAttribute('data-theme', newTheme);
        return { theme: newTheme };
      }),
      setTheme: (theme) => {
        document.documentElement.setAttribute('data-theme', theme);
        set({ theme });
      }
    }),
    { name: 'theme-storage' }
  )
);
```

**使用示例：**
```typescript
// 在任何组件中使用
function ThemeToggle() {
  const { theme, toggleTheme } = useTheme();
  
  return (
    <Button 
      icon={theme === 'light' ? <MoonOutlined /> : <SunOutlined />}
      onClick={toggleTheme}
    >
      切换主题
    </Button>
  );
}
```

**优势总结：**
- ✅ **统一管理**：一处修改，全局生效（Ant Design + Tailwind + 自定义组件）
- ✅ **运行时切换**：无需重新编译，秒切主题（light/dark/自定义）
- ✅ **类型安全**：TypeScript 支持，智能提示
- ✅ **性能最优**：CSS Variables 浏览器原生支持
- ✅ **可扩展性强**：轻松添加新主题（如：老年人大字体主题）

---

### 4.3 状态管理：Zustand 4.4+ ✅

**最终选择：Zustand 4.4+**

**核心理由：**
1. ✅ **与 Pinia 最相似**：API 风格接近，学习成本低
2. ✅ **极致轻量**：仅 1.2KB gzipped
3. ✅ **性能优秀**：基于 React Hooks，按需渲染
4. ✅ **TypeScript 完美支持**：原生 TS 实现

**对比 Pinia：**
```typescript
// Pinia (Vue)
export const useUserStore = defineStore('user', {
  state: () => ({ name: '', role: '' }),
  actions: { setUser(user) { this.name = user.name; } }
});

// Zustand (React) - 几乎相同
export const useUserStore = create<UserStore>((set) => ({
  name: '',
  role: '',
  setUser: (user) => set({ name: user.name })
}));
```

**Store 划分建议：**
- `authStore`：认证状态（Access Token、用户信息、角色）
- `tagsViewStore`：标签页管理（后台）
- `notificationStore`：通知消息
- `globalStore`：全局配置（主题、语言）

---

### 4.4 路由：React Router 6.20+ ✅

**最终选择：React Router 6.20+**

**核心理由：**
1. ✅ **React 官方推荐**：生态最成熟
2. ✅ **声明式路由**：与 Vue Router 概念相近
3. ✅ **数据加载**：Loader API（类似 Vue 的 beforeEnter）
4. ✅ **嵌套路由**：支持复杂的后台布局

**Route Guard 实现：**
```typescript
// 类似 Vue Router 的 beforeEach
function ProtectedRoute({ children, roles }) {
  const { accessToken, userRole } = useAuthStore();
  
  if (!accessToken) return <Navigate to="/login" />;
  if (!roles.includes(userRole)) return <Navigate to="/403" />;
  
  return children;
}
```

---

### 4.5 HTTP 客户端：Axios 1.6+ ✅

**最终选择：Axios 1.6+（保持不变）**

**核心理由：**
1. ✅ **团队已熟悉**：无需学习新工具
2. ✅ **拦截器完善**：Token 刷新逻辑已实现
3. ✅ **生态成熟**：插件、工具丰富

**增强建议：**
- 添加请求取消（AbortController）
- 添加请求重试机制
- 添加请求缓存（axios-cache-interceptor）

---

### 4.6 图表：ECharts 5.4 + echarts-for-react 3.0 ✅

**最终选择：echarts-for-react 3.0**

**核心理由：**
1. ✅ **配置完全兼容**：ECharts 配置无需修改
2. ✅ **官方 React 封装**：性能优化、生命周期管理
3. ✅ **迁移成本为零**：直接复用 Vue 版本的图表配置

---

### 4.7 表单处理：Ant Design Form（内置）✅

**最终选择：Ant Design Form（内置）**

**核心理由：**
1. ✅ **功能完善**：验证、联动、动态字段、嵌套表单
2. ✅ **与 Element Plus Form 相似**：降低学习成本
3. ✅ **完美集成**：与 Ant Design 组件无缝配合

**不推荐 React Hook Form：**
- 虽然性能更好，但会增加学习成本
- Ant Design Form 已满足需求

---

### 4.8 日期处理：dayjs 1.11+ ✅

**最终选择：dayjs 1.11+（Ant Design 依赖）**

**核心理由：**
1. ✅ **Ant Design 内置依赖**：无需额外安装
2. ✅ **轻量**：仅 2KB（相比 Moment.js 的 66KB）
3. ✅ **API 与 Moment.js 兼容**：易于使用

---

### 4.9 代码规范：ESLint + Prettier + Husky ✅

**最终选择：ESLint + Prettier + Husky（Git Hooks）**

**配置方案：**
- **ESLint**：`@typescript-eslint/recommended` + `plugin:react/recommended`
- **Prettier**：统一代码格式
- **Husky**：Pre-commit 检查（lint + format）
- **lint-staged**：仅检查暂存区文件

---

### 4.10 测试框架：Vitest + React Testing Library ✅

**最终选择：Vitest + React Testing Library**

**核心理由：**
1. ✅ **Vitest**：Vite 原生集成，速度快
2. ✅ **React Testing Library**：官方推荐，测试用户行为而非实现细节
3. ✅ **与 Jest 兼容**：API 相同，迁移成本低

---

## 5. 最终技术栈总结（2026 年最佳实践）

| 分类 | 技术 | 版本 | 状态 | 理由 |
|------|-----|------|------|------|
| 框架 | React | 18.3 → 19.x | ✅ 最佳 | 稳定过渡策略 |
| 语言 | TypeScript | 5.7+ | ✅ 最佳 | 类型安全 + 最新特性 |
| 构建 | Vite | 6.2+ | ✅ 最佳 | 性能提升 30% |
| UI | Ant Design | 5.12+ | ✅ 最佳 | 企业级首选 |
| 样式 | Tailwind CSS | 3.4+ | ✅ 最佳 | Utility-first |
| 主题 | Design Tokens + CSS Var | - | ✅ 最佳 | 运行时切换 |
| 状态管理 | Zustand | 4.5+ | ✅ 最佳 | 轻量高性能 |
| 路由 | React Router | 6.28+ | ✅ 最佳 | 官方标准 |
| HTTP | Axios | 1.7+ | ✅ 最佳 | 成熟稳定 |
| 图表 | echarts-for-react | 3.0 | ✅ 最佳 | 配置兼容 |
| 表单 | Ant Design Form | 内置 | ✅ 最佳 | 功能完善 |
| 日期 | dayjs | 1.11+ | ✅ 最佳 | 轻量级 |
| 测试 | Vitest | 4.1+ | ✅ 最佳 | 速度最快 |
| 测试库 | React Testing Library | 16+ | ✅ 最佳 | 官方推荐 |
| 国际化 | react-i18next | 15.0+ | ✅ 最佳 | 业界标准 |
| 错误监控 | Sentry | 8.0+ | ✅ 最佳 | 功能最全 |
| 性能监控 | Web Vitals | 4+ | ✅ 最佳 | 官方标准 |
| Token 存储 | HttpOnly Cookie + Zustand | - | ✅ 最佳 | 安全最优 |
| 代码规范 | ESLint + Prettier | 9+ / 3+ | ✅ 最佳 | 行业标准 |
| Git Hooks | Husky | 9+ | ✅ 最佳 | Pre-commit 检查 |

### React 18.3 vs 19.x 升级策略

**阶段 1：初始开发（React 18.3）**
```json
{
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1"
  }
}
```
- ✅ 最稳定的版本
- ✅ Ant Design 5.x 完全兼容
- ✅ 生态成熟

**阶段 2：平滑升级（React 19.x）**
```json
{
  "dependencies": {
    "react": "^19.0.0",
    "react-dom": "^19.0.0"
  }
}
```
**升级时机：**
1. Ant Design 官方声明完全支持 React 19
2. 核心依赖库（Zustand、React Router）适配完成
3. 项目稳定运行 3 个月后

**React 19 带来的优势：**
- ✅ **React Compiler**：自动优化，无需手动 memo
- ✅ **更快的渲染速度**：并发渲染优化
- ✅ **更好的 TypeScript 支持**
- ✅ **Actions**：表单处理简化

**Bundle Size 预估（生产环境 gzipped）：**
- React 18.3 + React-DOM: ~45KB
- Ant Design: ~250KB（按需引入后）
- Tailwind CSS: ~15KB（PurgeCSS 后）
- Zustand: ~1.2KB
- React Router: ~12KB
- Axios: ~16KB
- ECharts: ~300KB（按需引入模块）
- react-i18next: ~12KB
- Sentry SDK: ~35KB
- **总计**: ~685KB（首次加载，后续按需加载）

---

## 5.1 关键补充：生产级功能

### 5.1.1 国际化（i18n）✅ 必须

**最终选择：react-i18next 13.0+**

**核心理由：**
1. ✅ **业界标准**：React 国际化最流行的方案
2. ✅ **Ant Design 完美集成**：支持组件国际化
3. ✅ **命名空间隔离**：前台/后台翻译分离
4. ✅ **懒加载语言包**：按需加载，减少初始体积

**实现方案：**

```typescript
// src/i18n/index.ts
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

// 语言资源
import zhCN from './locales/zh-CN';
import enUS from './locales/en-US';

i18n
  .use(LanguageDetector) // 自动检测语言
  .use(initReactI18next)
  .init({
    resources: {
      'zh-CN': zhCN,
      'en-US': enUS
    },
    fallbackLng: 'zh-CN',
    defaultNS: 'common',
    interpolation: {
      escapeValue: false
    }
  });

export default i18n;
```

```typescript
// 使用示例
import { useTranslation } from 'react-i18next';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import enUS from 'antd/locale/en_US';

function App() {
  const { t, i18n } = useTranslation();
  
  const antdLocale = i18n.language === 'zh-CN' ? zhCN : enUS;
  
  return (
    <ConfigProvider locale={antdLocale}>
      <h1>{t('welcome')}</h1>
    </ConfigProvider>
  );
}
```

**关键点：**
- 前台用户端：中文为主（老年人友好）
- 管理后台：中英文切换
- 日期、数字格式国际化

---

### 5.1.2 错误监控（Error Tracking）✅ 必须

**最终选择：Sentry 8.0+**

**核心理由：**
1. ✅ **生产级必备**：实时错误追踪、堆栈分析
2. ✅ **React 完美集成**：Error Boundary + 性能监控
3. ✅ **Source Map 支持**：定位压缩后的错误
4. ✅ **用户上下文**：记录用户操作路径

**实现方案：**

```typescript
// src/main.tsx
import * as Sentry from '@sentry/react';

Sentry.init({
  dsn: import.meta.env.VITE_SENTRY_DSN,
  environment: import.meta.env.MODE,
  integrations: [
    Sentry.browserTracingIntegration(),
    Sentry.replayIntegration()
  ],
  tracesSampleRate: 0.1, // 10% 性能追踪
  replaysSessionSampleRate: 0.1, // 10% 会话录制
  beforeSend(event) {
    // 过滤敏感信息
    if (event.request?.cookies) {
      delete event.request.cookies;
    }
    return event;
  }
});

// Error Boundary 包裹
<Sentry.ErrorBoundary fallback={<ErrorFallback />}>
  <App />
</Sentry.ErrorBoundary>
```

**关键功能：**
- 前端异常捕获
- 未处理的 Promise rejection
- 网络请求失败
- 用户行为回放（Session Replay）
- 性能监控（Web Vitals）

---

### 5.1.3 性能监控（Performance Monitoring）✅ 必须

**最终选择：Web Vitals + Sentry Performance**

**核心理由：**
1. ✅ **用户体验量化**：FCP、LCP、FID、CLS、TTFB
2. ✅ **真实用户监控（RUM）**：了解真实性能
3. ✅ **瓶颈定位**：慢查询、大组件、网络问题

**实现方案：**

```typescript
// src/utils/performance.ts
import { onCLS, onFCP, onLCP, onFID, onTTFB } from 'web-vitals';

export function initPerformanceMonitoring() {
  onCLS(console.log); // Cumulative Layout Shift
  onFCP(console.log); // First Contentful Paint
  onLCP(console.log); // Largest Contentful Paint
  onFID(console.log); // First Input Delay
  onTTFB(console.log); // Time to First Byte
  
  // 发送到监控服务
  onLCP((metric) => {
    Sentry.captureMessage(`LCP: ${metric.value}ms`, 'info');
  });
}
```

**性能目标（针对养老系统老年人用户）：**
- **FCP（首次内容绘制）**：< 1.5s（老年人等待耐心较低）
- **LCP（最大内容绘制）**：< 2.5s
- **FID（首次输入延迟）**：< 100ms
- **CLS（累积布局偏移）**：< 0.1（避免误点击）
- **TTFB（首字节时间）**：< 600ms

---

### 5.1.4 日志系统（Logging）✅ 推荐

**最终选择：Browser Console + 结构化日志**

**实现方案：**

```typescript
// src/utils/logger.ts
type LogLevel = 'debug' | 'info' | 'warn' | 'error';

class Logger {
  private isDev = import.meta.env.DEV;
  
  log(level: LogLevel, message: string, context?: any) {
    if (!this.isDev && level === 'debug') return;
    
    const timestamp = new Date().toISOString();
    const logData = {
      timestamp,
      level,
      message,
      ...context,
      userAgent: navigator.userAgent,
      url: window.location.href
    };
    
    // 开发环境：控制台输出
    if (this.isDev) {
      console[level](message, logData);
    }
    
    // 生产环境：发送到日志服务
    if (level === 'error') {
      Sentry.captureMessage(message, { level, extra: logData });
    }
  }
  
  debug = (msg: string, ctx?: any) => this.log('debug', msg, ctx);
  info = (msg: string, ctx?: any) => this.log('info', msg, ctx);
  warn = (msg: string, ctx?: any) => this.log('warn', msg, ctx);
  error = (msg: string, ctx?: any) => this.log('error', msg, ctx);
}

export const logger = new Logger();
```

---

### 5.1.5 无障碍性（Accessibility）✅ 必须

**核心理由：**
- ✅ **法律要求**：WCAG 2.1 AA 级别
- ✅ **老年人友好**：您的用户群体特殊
- ✅ **键盘导航**：部分老年人不习惯鼠标

**实现要点：**

1. **语义化 HTML**
```tsx
// ❌ 错误
<div onClick={handleClick}>点击</div>

// ✅ 正确
<button onClick={handleClick} aria-label="提交表单">
  点击
</button>
```

2. **键盘导航**
```tsx
// 确保所有交互元素可通过 Tab 键访问
<div 
  role="button" 
  tabIndex={0}
  onKeyDown={(e) => e.key === 'Enter' && handleClick()}
>
```

3. **对比度**
```css
/* 文字与背景对比度 ≥ 4.5:1（WCAG AA） */
/* 大文字（18pt+）对比度 ≥ 3:1 */
```

4. **焦点指示**
```css
/* 确保焦点可见 */
button:focus-visible {
  outline: 2px solid #1890ff;
  outline-offset: 2px;
}
```

5. **eslint-plugin-jsx-a11y**
```json
// .eslintrc.json
{
  "extends": ["plugin:jsx-a11y/recommended"]
}
```

**老年人特殊优化：**
- ✅ 字体最小 16px（可调至 20px）
- ✅ 按钮最小 44x44px（易点击）
- ✅ 行高 1.5 倍（易阅读）
- ✅ 避免纯红绿色区分（色盲友好）

---

### 5.1.6 SEO 优化（可选）

**适用场景：** 如果前台用户端需要搜索引擎收录

**方案：** React Helmet + 预渲染

```typescript
import { Helmet } from 'react-helmet-async';

function HomePage() {
  return (
    <>
      <Helmet>
        <title>社区养老系统 - 首页</title>
        <meta name="description" content="..." />
      </Helmet>
      <div>...</div>
    </>
  );
}
```

---

### 5.1.7 PWA 支持（渐进式 Web 应用）⚠️ 可选

**适用场景：** 如果需要离线访问或添加到主屏幕

**方案：** Vite PWA Plugin

```typescript
// vite.config.ts
import { VitePWA } from 'vite-plugin-pwa';

export default defineConfig({
  plugins: [
    VitePWA({
      registerType: 'autoUpdate',
      manifest: {
        name: '社区养老系统',
        short_name: '养老系统',
        theme_color: '#1890ff',
        icons: [...]
      }
    })
  ]
});
```

**注意：** 养老系统通常在内网环境，PWA 优先级较低。

---

---

## 6. 关键迁移点

### 5.1 路由迁移

**Vue Router → React Router 6**

**Vue（当前）：**
```javascript
const routes = [
  {
    path: '/home',
    component: Home,
    redirect: '/home/dashboard',
    meta: { requiresAuth: true, roles: ['elder', 'kin'] },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: DashBoard,
        meta: { title: '首页' }
      }
    ]
  }
];
```

**React（目标）：**
```tsx
import { Navigate } from 'react-router-dom';

const routes = [
  {
    path: '/home',
    element: <ProtectedRoute roles={['elder', 'kin']}><FrontLayout /></ProtectedRoute>,
    children: [
      { index: true, element: <Navigate to="/home/dashboard" replace /> },
      { path: 'dashboard', element: <Dashboard />, meta: { title: '首页' } }
    ]
  }
];

// ProtectedRoute 组件实现 Route Guard（路由守卫）
function ProtectedRoute({ roles, children }) {
  const isAuthenticated = localStorage.getItem('isLoggedIn') === 'true';
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
  const hasRole = roles.some(role => userInfo.roles?.includes(role));

  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (!hasRole) return <Navigate to="/403" replace />;
  
  return children;
}
```

### 5.2 状态管理迁移

**Pinia → Zustand**

**Vue（tagsViewStore）：**
```javascript
import { defineStore } from 'pinia';

export const useTagsViewStore = defineStore('tagsView', {
  state: () => ({
    visitedViews: []
  }),
  actions: {
    addView(view) {
      this.visitedViews.push(view);
    }
  }
});
```

**React（Zustand）：**
```typescript
import create from 'zustand';

interface View {
  path: string;
  name: string;
  meta: any;
}

interface TagsViewStore {
  visitedViews: View[];
  addView: (view: View) => void;
}

export const useTagsViewStore = create<TagsViewStore>((set) => ({
  visitedViews: [],
  addView: (view) => set((state) => ({ 
    visitedViews: [...state.visitedViews, view] 
  }))
}));
```

### 5.3 组件迁移模式

**Vue 组件 → React 组件**

**Vue（示例）：**
```vue
<template>
  <div class="dashboard">
    <h1>{{ title }}</h1>
    <button @click="handleClick">点击</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const title = ref('首页');
const handleClick = () => {
  console.log('clicked');
};
</script>

<style scoped>
.dashboard { padding: 20px; }
</style>
```

**React（目标）：**
```tsx
import { useState } from 'react';
import styles from './Dashboard.module.css';

export const Dashboard: React.FC = () => {
  const [title] = useState('首页');
  
  const handleClick = () => {
    console.log('clicked');
  };

  return (
    <div className={styles.dashboard}>
      <h1>{title}</h1>
      <button onClick={handleClick}>点击</button>
    </div>
  );
};
```

### 5.4 Token 管理迁移（双 Token + 多租户隔离）

**原 TokenManager（需迁移）：**
```javascript
// utils/axios.js
export const TokenManager = {
  admin: {
    getAccessToken: () => sessionStorage.getItem('admin-access-token'),
    setAccessToken: (token) => sessionStorage.setItem('admin-access-token', token),
    getRefreshToken: () => sessionStorage.getItem('admin-refresh-token'),
    setRefreshToken: (token) => sessionStorage.setItem('admin-refresh-token', token)
  },
  user: {
    getAccessToken: () => localStorage.getItem('user-access-token'),
    setAccessToken: (token) => localStorage.setItem('user-access-token', token),
    getRefreshToken: () => localStorage.getItem('user-refresh-token'),
    setRefreshToken: (token) => localStorage.setItem('user-refresh-token', token)
  }
};
```

**React 版本（TypeScript + Token Rotation）：**
```typescript
// utils/tokenManager.ts

interface TokenPair {
  accessToken: string;
  refreshToken: string;
}

export const TokenManager = {
  admin: {
    getAccessToken: (): string | null => 
      sessionStorage.getItem('admin-access-token'),
    
    setAccessToken: (token: string): void => 
      sessionStorage.setItem('admin-access-token', token),
    
    getRefreshToken: (): string | null => 
      sessionStorage.getItem('admin-refresh-token'),
    
    setRefreshToken: (token: string): void => 
      sessionStorage.setItem('admin-refresh-token', token),
    
    setTokenPair: (tokenPair: TokenPair): void => {
      sessionStorage.setItem('admin-access-token', tokenPair.accessToken);
      sessionStorage.setItem('admin-refresh-token', tokenPair.refreshToken);
    },
    
    removeTokens: (): void => {
      sessionStorage.removeItem('admin-access-token');
      sessionStorage.removeItem('admin-refresh-token');
    }
  },
  
  user: {
    getAccessToken: (): string | null => 
      localStorage.getItem('user-access-token'),
    
    setAccessToken: (token: string): void => 
      localStorage.setItem('user-access-token', token),
    
    getRefreshToken: (): string | null => 
      localStorage.getItem('user-refresh-token'),
    
    setRefreshToken: (token: string): void => 
      localStorage.setItem('user-refresh-token', token),
    
    setTokenPair: (tokenPair: TokenPair): void => {
      localStorage.setItem('user-access-token', tokenPair.accessToken);
      localStorage.setItem('user-refresh-token', tokenPair.refreshToken);
    },
    
    removeTokens: (): void => {
      localStorage.removeItem('user-access-token');
      localStorage.removeItem('user-refresh-token');
    }
  }
} as const;
```

**Axios 拦截器实现（Token Rotation）：**
```typescript
// utils/axios.ts
import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import { TokenManager } from './tokenManager';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
});

// 请求拦截器：添加 Access Token
axiosInstance.interceptors.request.use(
  (config) => {
    // 根据请求路径判断使用哪个 Token
    const isAdminRoute = config.url?.startsWith('/admin');
    const tokenManager = isAdminRoute ? TokenManager.admin : TokenManager.user;
    
    const accessToken = tokenManager.getAccessToken();
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    
    const refreshToken = tokenManager.getRefreshToken();
    if (refreshToken) {
      config.headers['Refresh-Token'] = refreshToken;
    }
    
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器：处理 Token 刷新
axiosInstance.interceptors.response.use(
  (response) => {
    // 检查响应头是否有新的 Access Token（Token Rotation）
    const newAccessToken = response.headers['new-access-token'];
    if (newAccessToken) {
      const isAdminRoute = response.config.url?.startsWith('/admin');
      const tokenManager = isAdminRoute ? TokenManager.admin : TokenManager.user;
      tokenManager.setAccessToken(newAccessToken);
    }
    
    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };
    
    // 401 错误且未重试过，尝试使用 Refresh Token 刷新
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        const isAdminRoute = originalRequest.url?.startsWith('/admin');
        const tokenManager = isAdminRoute ? TokenManager.admin : TokenManager.user;
        const refreshToken = tokenManager.getRefreshToken();
        
        if (!refreshToken) {
          throw new Error('No refresh token available');
        }
        
        // 调用刷新 Token 接口
        const refreshEndpoint = isAdminRoute ? '/admin/auth/refresh' : '/auth/refresh';
        const { data } = await axios.post(refreshEndpoint, null, {
          headers: { 'Refresh-Token': refreshToken }
        });
        
        // 保存新的 Token Pair（Token Rotation）
        tokenManager.setTokenPair({
          accessToken: data.accessToken,
          refreshToken: data.refreshToken
        });
        
        // 重试原请求
        originalRequest.headers!.Authorization = `Bearer ${data.accessToken}`;
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        // Refresh Token 失效，跳转登录
        const isAdminRoute = originalRequest.url?.startsWith('/admin');
        const tokenManager = isAdminRoute ? TokenManager.admin : TokenManager.user;
        tokenManager.removeTokens();
        
        window.location.href = isAdminRoute ? '/admin/login' : '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export default axiosInstance;
```

---

## 6. 风险与挑战

### 6.1 技术风险
1. **学习曲线**：团队对 React 生态的熟悉程度
2. **第三方库兼容性**：部分 Vue 特定库需要找 React 替代品
3. **性能差异**：React 和 Vue 的渲染机制不同，需要注意性能优化

**应对措施：**
- 提前进行技术培训
- 建立组件库和最佳实践文档
- 性能监控和持续优化

### 6.2 业务风险
1. **功能遗漏**：迁移过程中可能遗漏部分功能
2. **用户体验差异**：UI 交互细节可能有差异
3. **数据一致性**：前后端接口保持一致

**应对措施：**
- 详细的功能清单和测试用例
- UI/UX 设计师参与审查
- 完善的回归测试

### 6.3 时间风险
1. **进度延期**：迁移工作量可能超出预期
2. **并行开发冲突**：原系统可能还在迭代

**应对措施：**
- 合理的时间评估和缓冲
- 版本管理和分支策略
- 定期同步进度

---

## 7. 测试策略

### 7.1 单元测试
- 工具：Jest + React Testing Library
- 覆盖率目标：核心工具函数和 Hooks > 80%

### 7.2 集成测试
- 工具：Jest + MSW（Mock Service Worker）
- 测试范围：API 调用、路由跳转、权限验证

### 7.3 E2E 测试
- 工具：Playwright 或 Cypress
- 测试范围：关键业务流程（登录、预约、报名等）

### 7.4 视觉回归测试
- 工具：Percy 或 Chromatic
- 确保 UI 与原系统一致

---

## 8. 部署与发布

### 8.1 环境配置
- **开发环境**：`dev.env`
- **测试环境**：`test.env`
- **生产环境**：`prod.env`

### 8.2 构建优化
- **Code Splitting（代码分割）**：按路由和组件懒加载
- **Tree Shaking**：移除未使用的代码
- **Bundle Compression**：Gzip/Brotli 压缩
- **CDN Integration**：静态资源 CDN 加速
- **Asset Optimization**：图片压缩、WebP 格式转换

### 8.3 发布策略
- **Canary Release（金丝雀发布）**：先发布 10% 流量进行灰度测试
- **Monitoring Metrics（监控指标）**：错误率、页面加载时间、API 响应时间、用户反馈
- **Rollback Strategy（回滚方案）**：保留 Vue 版本作为备份，支持一键回滚
- **Feature Flag（特性开关）**：通过配置控制新旧版本切换

---

## 9. 后续优化

### 9.1 性能优化
- **Memoization**：React.memo、useMemo、useCallback 的使用
- **Virtual Scrolling（虚拟滚动）**：使用 react-window 处理长列表
- **Lazy Loading（懒加载）**：图片、组件按需加载
- **Performance Profiling**：使用 React DevTools Profiler 分析性能瓶颈
- **Web Vitals Optimization**：优化 LCP、FID、CLS 指标

### 9.2 DX（Developer Experience）优化
- **Component Documentation**：Storybook 组件文档和 Playground
- **Code Quality**：Husky + lint-staged 代码检查、Pre-commit Hooks
- **CI/CD Pipeline**：自动化构建、测试、部署流程
- **Type Safety**：严格的 TypeScript 配置（strict mode）
- **Hot Module Replacement（HMR）**：快速开发反馈

### 9.3 可维护性提升
- **Design System**：建立统一的组件设计规范和 UI 标准
- **Code Review Process**：Pull Request 审查流程和 Checklist
- **Documentation**：架构文档、API 文档、组件使用指南
- **Coding Standards**：ESLint + Prettier 统一代码风格
- **Knowledge Base**：技术决策记录（ADR - Architecture Decision Records）

---

## 10. 时间线（预估）

| 阶段 | 任务 | 预计时间 | 责任人 |
|------|------|----------|--------|
| 阶段一 | 基础框架搭建 | 2-3 天 | TBD |
| 阶段二 | 公共模块迁移 | 3-4 天 | TBD |
| 阶段三 | 前台页面迁移 | 5-7 天 | TBD |
| 阶段四 | 后台页面迁移 | 7-10 天 | TBD |
| 阶段五 | 集成测试与优化 | 3-5 天 | TBD |
| 阶段六 | 部署与切换 | 1-2 天 | TBD |
| **总计** | | **21-31 天** | |

---

## 11. 参考资料

- [React 官方文档](https://react.dev/)
- [React Router 文档](https://reactrouter.com/)
- [Ant Design 文档](https://ant.design/)
- [Zustand 文档](https://zustand-demo.pmnd.rs/)
- [Vite 官方文档](https://vitejs.dev/)
- [TypeScript 手册](https://www.typescriptlang.org/docs/)

---

## 12. 附录

### 12.1 依赖包清单（已确定）

```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "antd": "^5.12.0",
    "zustand": "^4.4.7",
    "axios": "^1.6.2",
    "echarts": "^5.4.3",
    "echarts-for-react": "^3.0.2",
    "dayjs": "^1.11.10",
    "js-cookie": "^3.0.5",
    "jwt-decode": "^4.0.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.43",
    "@types/react-dom": "^18.2.17",
    "@types/node": "^20.10.5",
    "@types/js-cookie": "^3.0.6",
    "@vitejs/plugin-react": "^4.2.1",
    "typescript": "^5.3.3",
    "vite": "^5.0.8",
    "eslint": "^8.55.0",
    "eslint-plugin-react": "^7.33.2",
    "eslint-plugin-react-hooks": "^4.6.0",
    "@typescript-eslint/eslint-plugin": "^6.14.0",
    "@typescript-eslint/parser": "^6.14.0",
    "prettier": "^3.1.1",
    "tailwindcss": "^3.3.6",
    "autoprefixer": "^10.4.16",
    "postcss": "^8.4.32"
  }
}
```

### 12.2 Ant Design + Tailwind CSS 配置示例

**vite.config.ts:**
```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  css: {
    preprocessorOptions: {
      less: {
        javascriptEnabled: true,
        modifyVars: {
          // Ant Design 主题定制
          '@primary-color': '#1890ff',
          '@link-color': '#1890ff',
          '@border-radius-base': '4px'
        }
      }
    }
  }
});
```

**tailwind.config.js:**
```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  // 重要：添加 corePlugins 配置避免与 Ant Design 冲突
  corePlugins: {
    preflight: false, // 禁用 Tailwind 的基础样式重置，避免影响 Ant Design
  },
  theme: {
    extend: {
      colors: {
        // 与 Ant Design 主题色保持一致
        primary: '#1890ff',
        success: '#52c41a',
        warning: '#faad14',
        error: '#f5222d',
      }
    },
  },
  plugins: [],
}
```

**App.tsx (Ant Design ConfigProvider 示例):**
```typescript
import { ConfigProvider, theme } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import 'dayjs/locale/zh-cn';

function App() {
  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        token: {
          colorPrimary: '#1890ff',
          borderRadius: 4,
        },
        algorithm: theme.defaultAlgorithm, // 或 theme.darkAlgorithm
      }}
    >
      {/* 你的应用 */}
    </ConfigProvider>
  );
}
```

### 12.3 组件迁移示例

**Element Plus Table → Ant Design Table:**

```typescript
// Element Plus (Vue)
<el-table :data="tableData" stripe>
  <el-table-column prop="name" label="姓名" />
  <el-table-column prop="age" label="年龄" />
</el-table>

// Ant Design (React)
import { Table } from 'antd';

const columns = [
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '年龄', dataIndex: 'age', key: 'age' }
];

<Table 
  dataSource={tableData} 
  columns={columns} 
  rowClassName={(record, index) => index % 2 === 0 ? 'bg-gray-50' : ''}
/>
```

**Element Plus Form → Ant Design Form:**

```typescript
// Element Plus (Vue)
<el-form :model="form" :rules="rules">
  <el-form-item label="用户名" prop="username">
    <el-input v-model="form.username" />
  </el-form-item>
</el-form>

// Ant Design (React)
import { Form, Input } from 'antd';

<Form
  form={form}
  initialValues={{ username: '' }}
  onFinish={onFinish}
>
  <Form.Item 
    label="用户名" 
    name="username"
    rules={[{ required: true, message: '请输入用户名' }]}
  >
    <Input />
  </Form.Item>
</Form>
```

### 12.4 Git 分支策略
- **主分支**：`feat/react-migration`（当前所在）
- **开发分支**：`feat/react-migration-dev`
- **功能分支**：`feat/react-migration-{module-name}`
- **测试分支**：`feat/react-migration-test`

---

**文档版本**：v1.0  
**创建日期**：2026-06-20  
**最后更新**：2026-06-20  
**文档维护人**：TBD
