// 二次缓动函数,用于实现平滑的滚动效果
// t: 当前时间, b: 起始值, c: 变化量, d: 持续时间
Math.easeInOutQuad = function(t, b, c, d) {
    t /= d / 2
    if (t < 1) {
      return c / 2 * t * t + b
    }
    t--
    return -c / 2 * (t * (t - 2) - 1) + b
  }
  
  // 获取requestAnimationFrame方法,用于实现动画效果
  // 优先使用标准API,降级使用前缀版本,最后使用setTimeout模拟
  var requestAnimFrame = (function() {
    return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || function(callback) { window.setTimeout(callback, 1000 / 60) }
  })()
  
  /**
   * 移动所有可滚动元素到指定位置
   * 因为很难检测具体哪个元素是可滚动的,所以直接移动所有元素
   * @param {number} amount 滚动距离
   */
  function move(amount) {
    document.documentElement.scrollTop = amount
    document.body.parentNode.scrollTop = amount
    document.body.scrollTop = amount
  }
  
  /**
   * 获取当前滚动位置
   * @returns {number} 当前滚动距离
   */
  function position() {
    return document.documentElement.scrollTop || document.body.parentNode.scrollTop || document.body.scrollTop
  }
  
  /**
   * 平滑滚动到指定位置
   * @param {number} to 目标滚动位置
   * @param {number} duration 滚动持续时间,默认500ms
   * @param {Function} callback 滚动完成后的回调函数
   */
  export function scroll(to, duration, callback) {
    const start = position()
    const change = to - start
    const increment = 20 // 每20ms更新一次
    let currentTime = 0
    duration = (typeof (duration) === 'undefined') ? 500 : duration
    var animateScroll = function() {
      // 递增时间
      currentTime += increment
      // 使用二次缓动函数计算当前位置
      var val = Math.easeInOutQuad(currentTime, start, change, duration)
      // 移动到计算出的位置
      move(val)
      // 如果动画未完成,继续执行
      if (currentTime < duration) {
        requestAnimFrame(animateScroll)
      } else {
        if (callback && typeof (callback) === 'function') {
          // 动画完成,执行回调
          callback()
        }
      }
    }
    animateScroll()
  }