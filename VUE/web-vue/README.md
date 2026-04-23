##  css

bordercolor: #24272c
页面/区块背景色: #171a1e;
按钮背景色: #2c3138;
次级字体颜色： #707a8a


开发相关文档：
https://www.tailwindcss.cn/docs/guides/vue-3-vite
https://vant-contrib.gitee.io/vant/#/en-US


figma地址：https://www.figma.com/file/26rav6WFHIqzjfEndc5EJI/%E5%A4%96%E6%B1%87PC%E7%AB%AF?node-id=288-489&t=ukNHvTE5DGlVvzP3-0
竞品地址： https://pepperstone.com/


## Branch Management
exchange 为交易所分支
main 为最新代码分支
composite 为综合盘-demo站点分支（可废弃，代码太老了）
blackBull/HTR/E-Robinhood... 等为新盘分支

分支说明：
main拥有交易所和和综合盘的所有代码，根据router控制默认路由
exchange里面会有最新的功能，默认是开发分支，功能测试完成之后，会合并到main
blackBull/HTR/E-Robinhood... 等新盘分支，一旦交付不轻易变动
注：
如果有新盘开发请从main分支切新的分支进行开发。
如果有bug修改，先提交到main，再同步到exchange
## 开发规范
模板开发：
1. 样式开发+翻译
src/components/compositeHome/header.vue  头部文件
src/views/compositeHomePage/home.vue  首页
2. .env文件 title和email替换
3. 全文搜索DEMO Demo demo.co文案替换
4. 替换public/image下的文件，以及favicon.ico

## 模版和分支

 BTEX-交易所模板7
  HTR-综合盘模版4
  CMEO-综合盘模版6
  COM,DCL,MOX-综合盘模版7
  snowBall-综合盘模版8
  Ayor,Chasoe -综合盘模版9
  E-Robinhood-综合盘模版12
  XTB-综合盘模版15
  WealthfrontBRokerage-综合盘模版16
weBull
 ## Project setup
```
yarn install
```

### Compiles and hot-reloads for development
```
yarn serve
```

### Compiles and minifies for production
```
yarn build
```

### Lints and fixes files
```
yarn lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
