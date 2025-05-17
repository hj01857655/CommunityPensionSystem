import '@/assets/styles/index.scss'; // global css
import '@/assets/styles/dark-theme.scss'; // 自定义暗黑模式样式
import '@/styles/unified-components.scss'; // 统一组件样式
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import { createApp } from 'vue';

import setupDirective from '@/directive/permission/index';
import store from '@/stores';
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
import SearchForm from '@/components/common/table/SearchForm.vue';
import TableToolbar from '@/components/common/table/TableToolbar.vue';
import DataTable from '@/components/common/table/DataTable.vue';

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
