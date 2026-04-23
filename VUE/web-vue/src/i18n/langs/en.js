import enLocale from "element-plus/lib/locale/lang/en";
import login from "../resource/English/login";
import home from "../resource/English/home";
import user from "../resource/English/user";
import hangqing from "../resource/English/hangqing";
import jiaoyi from "../resource/English/jiaoyi";
import c2c from "../resource/English/c2c";
import compositeHome from "../resource/English/compositeHome"; //综合盘的
const en = {
  ...compositeHome,
  ...login,
  message: {
    home,
    user,
    hangqing,
    jiaoyi,
    c2c,
  },
  ...enLocale,
};

export default en;
