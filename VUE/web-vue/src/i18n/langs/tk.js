// 土耳其
import tkLocale from "element-plus/lib/locale/lang/tk";
import login from "../resource/Turkish/login.js";
import home from "../resource/Turkish/home";
import user from "../resource/Turkish/user";
import hangqing from "../resource/Turkish/hangqing";
import jiaoyi from "../resource/Turkish/jiaoyi";
import c2c from "../resource/Turkish/c2c";
import compositeHome from "../resource/Turkish/compositeHome"; //综合盘的
const tk = {
  ...compositeHome,
  ...login,
  message: {
    home,
    user,
    hangqing,
    jiaoyi,
    c2c,
  },
  ...tkLocale,
};

export default tk;
