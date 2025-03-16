const webpack = require('webpack');

module.exports = {
  mode: process.env.NODE_ENV,
  // 优化配置
  optimization: {
    minimize: process.env.NODE_ENV === 'production', // 生产环境开启压缩
    splitChunks: {
      chunks: 'all', // 分割所有代码块
      minSize: 20000, // 最小分割大小
      maxSize: 244000, // 最大分割大小
      minChunks: 1, // 最小引用次数
      cacheGroups: {
        vendors: {
          test: /[\\/]node_modules[\\/]/,
          priority: -10
        },
        default: {
          minChunks: 2,
          priority: -20,
          reuseExistingChunk: true
        }
      }
    }
  },
  plugins: [
    new webpack.DefinePlugin({
      __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: JSON.stringify(false),
      __VUE_OPTIONS_API__: JSON.stringify(true), 
      __VUE_PROD_DEVTOOLS__: JSON.stringify(process.env.NODE_ENV !== 'production'), // 开发环境启用devtools
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV)
    }),
    new webpack.ProgressPlugin(), // 显示打包进度
  ],
  performance: {
    hints: process.env.NODE_ENV === 'production' ? 'warning' : false // 生产环境显示性能提示
  }
};