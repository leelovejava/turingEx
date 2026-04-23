// 葡萄牙
import ptLocale from "element-plus/lib/locale/lang/pt.js.map";
import login from "../resource/Portuguese/login.js";
import home from "../resource/Portuguese/home";
import user from "../resource/Portuguese/user";
import hangqing from "../resource/Portuguese/hangqing";
import jiaoyi from "../resource/Portuguese/jiaoyi";
import c2c from "../resource/Portuguese/c2c";
import compositeHome from "../resource/Portuguese/compositeHome"; //综合盘的
const pt = {
  ...compositeHome,
  ...login,
  message: {
    home,
    user,
    hangqing,
    jiaoyi,
    c2c,
  },
  ...ptLocale,
};

export default pt;
