import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';

dayjs.extend(utc);
dayjs.extend(timezone)
dayjs.tz.setDefault("America/New_York");

export const formatUtcTimestamp = (timestamp) => {
  return new Date(timestamp).toLocaleString();  //2023/11/11 09:43:52'
}

export const formatGmtTimestamp = (timestamp) => {
  return new Date(timestamp).toGMTString(); //  Sat, 11 Nov 2023 01:43:52 GMT'
}

// 获得客户端当前时区
export function getTimeZone() {
  return Intl.DateTimeFormat().resolvedOptions().timeZone
}

// 时间戳转换为本地时间
export const formatLocalTime = (timestamp) => {
 // timestamp格式为服务端utc时间戳
  return dayjs.unix(timestamp).format('YYYY-MM-DD HH:mm:ss');
}

export const formatUsTime = (timestamp) => {
  // 创建 Date 对象，将时间戳转换为指定时区的时间
  const date = new Date(timestamp);
  const timeZoneOffset = -5; // 时区偏移量，比如西八区就是-5
  const utc = date.getTime() + (date.getTimezoneOffset() * 60000); // 将本地时间转换为UTC时间
  const convertedDate = new Date(utc + (3600000 * timeZoneOffset)); // 转换为指定时区的时间

  // 格式化时间
  const year = convertedDate.getFullYear();
  const month = convertedDate.getMonth() + 1;
  const day = convertedDate.getDate();
  const hour = convertedDate.getHours();
  const minute = convertedDate.getMinutes();
  const second = convertedDate.getSeconds();

  return (`${year}-${month}-${day} ${hour}:${minute}:${second}`);
}

// 美国东部时间,utc-5
export const formatEasternUnixTime = (timestamp) => {
  //时间戳 (秒) 用这个
  return dayjs.unix(timestamp).tz('America/New_York').format("YYYY-MM-DD HH:mm:ss");
}

// 美国东部时间,utc-5
export const formatEasternTime = (timestamp) => {
  //时间戳 (毫秒) 用这个
  return dayjs.tz(timestamp).format("YYYY-MM-DD HH:mm:ss");
}

export const formatTimeStringHour = (timestamp) => {
  return dayjs.tz(timestamp).format('HH:mm')
}