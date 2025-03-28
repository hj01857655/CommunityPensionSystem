import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import 'virtual:svg-icons-register';

// 添加以下代码，解决滚动事件警告
// 设置passive为true，提高滚动性能
document.addEventListener('DOMContentLoaded', () => {
  const wheelOpt = { passive: true };
  const wheelEvent = 'onwheel' in document.createElement('div') ? 'wheel' : 'mousewheel';
  document.addEventListener(wheelEvent, () => {}, wheelOpt);
});

import App from './App.vue';
import router from './router';
import backStore from '@/stores/back';
import foreStore from '@/stores/fore';
import setupDirective from '@/directive/permission/index';

const app = createApp(App);
console.log("app.version", app.version);

// 创建pinia实例
const pinia = createPinia();

// 使用插件
app.use(router)
    .use(pinia)
    .use(ElementPlus);

// 注册ElementPlus图标组件
Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
    app.component(key, component);
});

// 注册自定义指令
setupDirective(app);

// 挂载应用
app.mount('#app');