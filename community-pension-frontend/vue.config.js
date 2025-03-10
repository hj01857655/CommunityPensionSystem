const path = require('path')
const webpack = require('webpack')
const { defineConfig } = require('@vue/cli-service')
const nodePolyfills = require('node-polyfills')
module.exports = defineConfig({
  transpileDependencies: true,
  configureWebpack: {
    
    plugins: [
      nodePolyfills(),
      new webpack.DefinePlugin({
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: 'false'
      })
    ],
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
    }
  },
})
