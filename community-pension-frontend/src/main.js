import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import axios from 'axios';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';

import App from '@/App.vue';
import router from '@/router';

const app = createApp(App);

// 使用插件
app.use(router)
   .use(ElementPlus, { locale: zhCn })
   .use(createPinia());

// 注册ElementPlus图标组件
Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
    app.component(key, component);
});

// 设置axios默认配置
axios.defaults.baseURL = 'http://127.0.0.1:8080/';

// 挂载应用
app.mount('#app');
