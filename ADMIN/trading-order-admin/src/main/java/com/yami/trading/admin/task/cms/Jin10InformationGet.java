package com.yami.trading.admin.task.cms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yami.trading.bean.cms.Infomation;
import com.yami.trading.common.http.HttpHelper;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.cms.InfomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class Jin10InformationGet {
    @Resource
    private InfomationService infomationService;

    @Scheduled(cron = "0 */15 * * * ?")
    public void crawl() {
        try {
            LocalDateTime now = LocalDateTime.now();
            String timeStr = now.format(DateTimeFormatter.ofPattern("YYYY-MM-dd+HH:mm:ss"));
            log.info(timeStr + " 金十数据采集开始");
            long t = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            String url = "https://flash-api.jin10.com/get_flash_list?channel=-8200&vip=1&max_time=%s&t=%s";
            url = String.format(url, timeStr, t);
            Map<String, String> header = new HashMap<>();
            header.put("authority", "flash-api.jin10.com");
            header.put("Accept", "application/json, text/plain, */*");
            header.put("x-app-id", "bVBF4FyRTn5NJF5n");
            header.put("x-version", "1.0.0");

            String json = HttpHelper.sendGetHttp(url, null, null, header);
            JSONObject jsonObject = JSONObject.parseObject(json);
            JSONArray data = jsonObject.getJSONArray("data");
            List<Infomation> infomations = new ArrayList<>();
            for (Object datum : data) {
                jsonObject = (JSONObject) datum;
                String time = (String) jsonObject.get("time");
                String id = (String) jsonObject.get("id");
                Integer important = (Integer) jsonObject.get("important");
                if (important == null || important == 1) {
                    continue;
                }
                JSONObject data1 = jsonObject.getJSONObject("data");
                String content = (String) data1.get("content");
                if (StringUtils.isNotEmpty(content)) {
                    Infomation infomation = new Infomation();
                    infomation.setUuid(id);
                    content = content.replace("金十数据", "");
//                    String title = null;
//                    if (content.contains("【")) {
//                        title = content.substring(content.indexOf("【") + 1, content.indexOf("】"));
//                        content = content.substring(content.indexOf("】") + 1);
//                    }
                    if (content.contains("金十图示")) {
                        continue;
                    }
                    infomation.setLang("zh-CN");
                    infomation.setCreatedAt(time);
                    infomation.setDescription(content);
                    infomation.setType("1");
//                    infomation.setTitle(title);
                    Infomation byId = infomationService.getById(id);
                    if (byId == null) {
                        infomationService.save(infomation);
                    }
//                    infomations.add(infomation);
                }
            }
//            infomationService.saveOrUpdateBatch(infomations);
            log.info("金十数据采集结束");
        } catch (Exception e) {
            log.error("金十资讯采集异常", e);
        }
    }

//    public static void main(String[] args) throws Exception {
//        Jin10InformationGet get = new Jin10InformationGet();
//        get.crawl();
//    }
}
