import vue from '@vitejs/plugin-vue'
import path from 'path'
import { defineConfig } from 'vite'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
// 配置vite
export default defineConfig({
  // 插件 
  plugins: [
    vue(),
    createSvgIconsPlugin({
      // 指定需要缓存的图标文件夹
      iconDirs: [path.resolve(process.cwd(), 'src/assets/icons/svg')],
      // 指定symbolId格式
      symbolId: 'icon-[dir]-[name]'
    })
  ],
  // 定义
  define: {
    __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: 'false', // 修复为字符串
    __VUE_OPTIONS_API__: 'true', // 添加必要的Vue配置
    __VUE_PROD_DEVTOOLS__: process.env.NODE_ENV !== 'production' ? 'true' : 'false' // 仅在开发环境启用devtools
  },
  // 配置别名
  resolve: {
    alias: {
      '~': path.resolve(__dirname, './'),
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
  
  // 服务器配置
  server: {
    host: '0.0.0.0',
    port: 5173,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:9000',
        changeOrigin: true,
        rewrite: (path) => path
      },
      '/upload': {
        target: 'http://localhost:9000',
        changeOrigin: true,
        // 不重写路径，保持原样
      },
      '/holiday': {
        target: 'https://api.jiejiariapi.com',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/holiday/, '')
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
