module.exports = {
  plugins: {
    // 'postcss-pxtorem': { // 把px单位换算成rem单位
    //   rootValue: 41.4, // vant官方使用的是37.5
    //   selectorBlackList: ['vant', 'mu'], // 忽略转换正则匹配项
    //   propList: ['*']
    // }
    'postcss-pxtorem': {
      //根元素字体大小
      rootValue: 16,
      //匹配CSS中的属性，* 代表启用所有属性
      propList: ['*'],
      //转换成rem后保留的小数点位数
      unitPrecision: 5,
      //小于3px的样式不被替换成rem
      minPixelValue: 3,
      //忽略一些文件，不进行转换，比如我想忽略 依赖的UI框架
      exclude: (file) => {
        if (file.indexOf('src/components/Transform') > -1) {
          return false
        }
        if (file.indexOf('src/views/cryptos') > -1) {
          return false
        }
        return true
      }
    },
    tailwindcss: {},
    autoprefixer: {},
  },
}
