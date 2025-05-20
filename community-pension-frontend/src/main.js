import '@/assets/styles/dark-theme.scss'; // 自定义暗黑模式样式
import '@/assets/styles/index.scss'; // global css
import '@/styles/unified-components.scss'; // 统一组件样式
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import { createApp } from 'vue';

import setupDirective from '@/directive/permission/index';
import store from '@/stores';
import AdminWebSocketClient from '@/utils/adminWebsocket'; // 导入管理员WebSocket客户端
import WebSocketClient from '@/utils/websocket';
import App from './App.vue';
import router from './router';

import RightToolbar from '@/components/back/system/menu/RightToolbar.vue';
import DictTag from '@/components/common/base/DictTag';
import Pagination from '@/components/common/table/Pagination.vue';
import { useDict } from '@/utils/dict';

// 1. 引入 ECharts 主模块和渲染器
import { LineChart } from 'echarts/charts';
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components';
import * as echarts from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';

// 2. 注册渲染器和组件
echarts.use([
  CanvasRenderer,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
]);

// 3. 全局注册 vue-echarts
import VueECharts from 'vue-echarts';

// 全局处理滚动事件监听器的passive选项
const originalAddEventListener = EventTarget.prototype.addEventListener;
EventTarget.prototype.addEventListener = function(type, listener, options) {
  const passiveEvents = ['touchstart', 'touchmove', 'wheel', 'mousewheel'];
  
  let newOptions = options;
  if (passiveEvents.includes(type)) {
    if (newOptions === false || newOptions === undefined) {
      newOptions = { passive: true };
    } else if (typeof newOptions === 'object' && newOptions.passive === undefined) {
      newOptions.passive = true;
    }
  }
  
  return originalAddEventListener.call(this, type, listener, newOptions);
};

const app = createApp(App);

// 全局方法挂载
app.config.globalProperties.useDict = useDict;

// 全局组件注册
app.component('DictTag', DictTag);//字典标签
app.component('Pagination', Pagination);//分页组件
app.component('RightToolbar', RightToolbar);//右侧工具栏
app.component('VueECharts', VueECharts);

// 注册统一组件
import DataTable from '@/components/common/table/DataTable.vue';
import SearchForm from '@/components/common/table/SearchForm.vue';
import TableToolbar from '@/components/common/table/TableToolbar.vue';

app.component('SearchForm', SearchForm); // 统一搜索表单
app.component('TableToolbar', TableToolbar); // 统一表格工具栏
app.component('DataTable', DataTable); // 统一数据表格

// 使用插件
app.use(router);
app.use(store);
app.use(ElementPlus);

// 注册ElementPlus图标组件
Object.keys(ElementPlusIconsVue).forEach(key => {
    app.component(key, ElementPlusIconsVue[key]);
});

// 注册自定义指令
setupDirective(app);

// 挂载应用
app.mount('#app');

// 初始化WebSocket连接
// 根据当前路径判断是前台还是后台
const isAdmin = window.location.pathname.includes('/admin/');

// 根据不同系统获取对应的token并初始化对应的WebSocket客户端
if (isAdmin) {
  // 后台系统 - 从会话存储获取admin-access-token
  let adminToken = sessionStorage.getItem('admin-access-token');
  console.log('检测到后台管理系统路径，使用adminWebsocket');
  
  if (adminToken) {
    // 确保token格式正确，移除Bearer前缀
    if (adminToken.startsWith('Bearer ')) {
      adminToken = adminToken.substring(7);
    }
    
    // 初始化管理员WebSocket客户端
    AdminWebSocketClient.init(adminToken);
    
    // 设置消息处理器
    AdminWebSocketClient.setMessageHandlers({
      // 紧急消息处理
      onEmergency: (message) => {
        console.log('管理员收到紧急呼叫，显示紧急弹窗');
        // 紧急消息处理逻辑已在adminWebsocket.js中实现
      },
      // 连接成功
      onConnected: () => {
        console.log('管理员WebSocket连接成功');
      },
      // 连接断开
      onDisconnected: () => {
        console.log('管理员WebSocket连接断开');
      }
    });
  } else {
    console.warn('管理员未登录，无法初始化WebSocket');
  }
} else {
  // 前台系统 - 从本地存储获取user-access-token
  let userToken = localStorage.getItem('user-access-token');
  console.log('检测到前台用户系统路径，使用普通websocket');
  
  if (userToken) {
    // 确保token格式正确，移除Bearer前缀
    if (userToken.startsWith('Bearer ')) {
      userToken = userToken.substring(7);
    }
    
    // 初始化普通用户WebSocket客户端
    WebSocketClient.init(userToken);
    
    // 设置消息处理器
    WebSocketClient.setMessageHandlers({
      // 紧急消息处理
      onEmergency: (message) => {
        console.log('用户收到紧急消息，显示弹窗提醒');
        // 紧急消息处理逻辑已在websocket.js中实现
      },
      // 连接成功
      onConnected: () => {
        console.log('用户WebSocket连接成功');
      },
      // 连接断开
      onDisconnected: () => {
        console.log('用户WebSocket连接断开');
      }
    });
  } else {
    console.warn('用户未登录，无法初始化WebSocket');
  }
}



