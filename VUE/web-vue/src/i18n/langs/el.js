// 土耳其
import elLocale from "element-plus/lib/locale/lang/el";
import login from "../resource/Greek/login.js";
import home from "../resource/Greek/home";
import user from "../resource/Greek/user";
import hangqing from "../resource/Greek/hangqing";
import jiaoyi from "../resource/Greek/jiaoyi";
import c2c from "../resource/Greek/c2c";
import compositeHome from "../resource/Greek/compositeHome"; //综合盘的
const el = {
  ...compositeHome,
  ...login,
  message: {
    home,
    user,
    hangqing,
    jiaoyi,
    c2c,
  },
  ...elLocale,
};

export default el;
