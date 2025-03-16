import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
// 配置vite
export default defineConfig({
  // 插件 
  plugins: [vue()],
  // 定义
  define: {
    __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: 'false', // 修复为字符串
    __VUE_OPTIONS_API__: 'true', // 添加必要的Vue配置
    __VUE_PROD_DEVTOOLS__: process.env.NODE_ENV !== 'production' ? 'true' : 'false' // 仅在开发环境启用devtools
  },
  // 配置别名
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@assets': path.resolve(__dirname, './src/assets'), 
      '@components': path.resolve(__dirname, './src/components'),
      '@views': path.resolve(__dirname, './src/views'),
      '@router': path.resolve(__dirname, './src/router'),
      '@utils': path.resolve(__dirname, './src/utils'),
      '@store': path.resolve(__dirname, './src/store'),
      '@api': path.resolve(__dirname, './src/api'),
    },
    extensions: ['.js', '.vue', '.json'] // 添加文件扩展名支持
  },
  // 配置
  server:{
    host:'0.0.0.0', // 允许外部访问 
    port:8000,      // 端口号
    open:true,      // 是否自动打开浏览器
    // 配置代理
    proxy:{
      '/api':{
        target:'http://127.0.0.1:9000', //后端服务地址
        changeOrigin:true,     //是否跨域
        rewrite:(path)=>path  //不重写路径，保留/api前缀
      }
    }
  },
  // 构建配置
  build: {
    minify: 'terser', // 使用terser进行代码压缩
    terserOptions: {
      compress: {
        drop_console: process.env.NODE_ENV === 'production', // 生产环境下移除console
        drop_debugger: process.env.NODE_ENV === 'production' // 生产环境下移除debugger
      }
    },
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router', 'pinia'] // 将常用库分离为单独的chunk
        }
      }
    }
  }
})
