import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

import App from '@/App.vue';
import router from '@/router';

const app = createApp(App);
console.log("app.version", app.version);
// 使用插件
app.use(router)
    .use(ElementPlus)
    .use(createPinia());

// 注册ElementPlus图标组件
Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
    app.component(key, component);
});

// 挂载应用
app.mount('#app');