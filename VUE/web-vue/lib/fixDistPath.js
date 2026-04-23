/**
 * 针对非ESModule方式在浏览器打开，用于修复dist包path
 * vite.config.js 先配置 @vitejs/plugin-legacy 和 base:'./'
 * 参考：https://www.jianshu.com/p/d44d14bc5344
 * 配置完成后 路径正常后 再 node fixDistPath.js
 */

import fs from 'fs';
const distPath = './dist/index.html';//打包路径的index.html
let htmlText = fs.readFileSync(distPath, 'utf8');
let resultText = '';
let htmlArr = htmlText.match(/.*\n/g) || [];
htmlArr.forEach(str => {
    str = str.replace(/\s?nomodule\s?/g,' ');
    str = str.replace(/\s?crossorigin\s?/g,' ');
    str = str.replace(/data-src/g,'src');
    if(!/type="module"/i.test(str)) resultText += str;
});
fs.writeFileSync(distPath,resultText,'utf8');
console.log('success');