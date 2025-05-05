import '@/assets/styles/index.scss'; // global css
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import { createApp } from 'vue';

import setupDirective from '@/directive/permission/index';
import store from '@/stores';
import App from './App.vue';
import router from './router';

import DictTag from '@/components/DictTag';
import Pagination from '@/components/Pagination';
import RightToolbar from '@/components/back/system/menu/RightToolbar.vue';
import { useDict } from '@/utils/dict';

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

console.log("app.version", app.version);

// 全局方法挂载
app.config.globalProperties.useDict = useDict;


// 全局组件注册
app.component('DictTag', DictTag);//字典标签
app.component('Pagination', Pagination);//分页组件
app.component('RightToolbar', RightToolbar);//右侧工具栏
// 使用插件
app.use(router)
app.use(store)
app.use(ElementPlus);

// 注册ElementPlus图标组件
Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
    app.component(key, component);
});

// 注册自定义指令
setupDirective(app);

// 挂载应用
app.mount('#app');