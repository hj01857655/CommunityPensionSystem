import { createApp } from 'vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import '@/assets/styles/index.scss' // global css

import App from './App.vue';
import router from './router';
import store from '@/stores';
import setupDirective from '@/directive/permission/index';

import { useDict } from '@/utils/dict';
import DictTag from '@/components/DictTag';
import Pagination from '@/components/Pagination';
import RightToolbar from '@/components/back/system/menu/RightToolbar.vue';
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