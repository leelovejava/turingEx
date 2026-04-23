import i18n from "@/i18n";
export const dataTime = (data, isTrue) => {
  var date = new Date(data);
  let Y = date.getFullYear() + "-";
  let M =
    (date.getMonth() + 1 < 10
      ? "0" + (date.getMonth() + 1)
      : date.getMonth() + 1) + "-";
  let D = date.getDate() + " ";
  let h = date.getHours() + ":";
  let m = date.getMinutes() + ":";
  let s = date.getSeconds();
  let str = Y + M + D;
  if (isTrue) {
    str = Y + M + D + h + m + s;
  } else {
    str = Y + M + D;
  }
  return str;
};

export const fixData = (val) => {
  // 保留两位小数
  const value = val / 1;
  if (isNaN(value)) {
    return "--";
  }
  if (value / 10000 < 1) {
    return value.toFixed(3);
  } else {
    if (i18n.locale === "CN") {
      if ((value / 10000).toString().split(".")[0].length <= 4) {
        return (value / 10000).toFixed(3) + " " + i18n.global.t("万");
      } else {
        return (value / 100000000).toFixed(3) + " " + i18n.global.t("亿");
      }
    } else {
      if ((value / 1000).toString().split(".")[0].length <= 4) {
        return (value / 1000).toFixed(2) + " " + "K";
      } else {
        return (value / 1000000).toFixed(2) + " " + "M";
      }
    }
  }
};
