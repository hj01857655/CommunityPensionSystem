const webpack = require('webpack');

module.exports = {
  // 其他配置
  plugins: [
    new webpack.DefinePlugin({
      __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: JSON.stringify(false), // 或者 true，根据需要
      // 其他特性标志可以在这里定义
      __VUE_OPTIONS_API__: JSON.stringify(true),
      __VUE_PROD_DEVTOOLS__: JSON.stringify(false),
    }),
  ],
}; 