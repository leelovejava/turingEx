package com.yami.trading.service.cms.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.cms.Infomation;
import com.yami.trading.common.http.HttpHelper;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.dao.cms.InfomationMapper;
import com.yami.trading.service.cms.InfomationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lucas
 * @since 2023-06-19 23:44:55
 */
@Service
public class InfomationServiceImpl extends ServiceImpl<InfomationMapper, Infomation> implements InfomationService {
    public void getInformation() {
        String cookie = HttpHelper.getCookie("https://xueqiu.com/");
        String url = "https://xueqiu.com/statuses/livenews/list.json?since_id=-1&max_id=-1&count=15";
        String json = HttpHelper.sendGetHttp(url, null, cookie);
        JSONArray items = JSONObject.parseObject(json).getJSONArray("items");
        List<Infomation> infomations = new ArrayList<>();
        items.forEach(d -> {
            Infomation infom = new Infomation();
            JSONObject data = (JSONObject) d;
            String dataId = data.getString("id");
            infom.setDataId(dataId);
            String description = data.getString("text");
            infom.setDescription(description);
            String createAt = data.getString("created_at");
            infom.setCreatedAt(createAt);
            infom.setType("1");
            String originUrl = data.getString("target");
            infom.setOriginUrl(originUrl);
            String source = getSource(description);
            infom.setSource(source);
            infom.setLang("zh-CN");
            String key = "infomation" + originUrl;
            if (RedisUtil.get(key) == null) {
                infomations.add(infom);
                // url存一周
                RedisUtil.set(key, 1, 60 * 60 * 24 * 7);
            }
        });
        if(infomations.size()>1){
            saveBatch(infomations);
        }
    }

    public static String getSource(String text) {

        String pattern = "（([^（）]*)）$"; // 匹配最后一个括号内的文本

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(text);

        if (matcher.find()) {
            String endText = matcher.group(1);
            return endText;
        } else {
            return "";
        }
    }
}
