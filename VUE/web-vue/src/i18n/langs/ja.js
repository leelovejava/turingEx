import jaLocale from "element-plus/lib/locale/lang/ja";
import login from "../resource/Japanese/login";
import home from "../resource/Japanese/home";
import user from "../resource/Japanese/user";
import hangqing from "../resource/Japanese/hangqing";
import jiaoyi from "../resource/Japanese/jiaoyi";
import c2c from "../resource/Japanese/c2c";
import compositeHome from "../resource/Japanese/compositeHome"; //综合盘的
const Japanese = {
  ...compositeHome,
  ...login,
  message: {
    home,
    user,
    hangqing,
    jiaoyi,
    c2c,
  },
  ...jaLocale,
};

export default Japanese;
