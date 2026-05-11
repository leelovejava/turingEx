package com.yami.trading.admin.task.cms;

import com.yami.trading.bean.cms.Infomation;
import com.yami.trading.service.cms.InfomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * 英文财经 RSS 资讯采集（Reuters / Investing.com）
 */
@Component
@Slf4j
public class RssInformationGet {

    private static final List<String> RSS_URLS = Arrays.asList(
            "https://www.investing.com/rss/news.rss"
    );

    @Resource
    private InfomationService infomationService;

    @Scheduled(cron = "0 */15 * * * ?")
    public void crawl() {
        for (String rssUrl : RSS_URLS) {
            try {
                URL url = new URL(rssUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                try (InputStream is = conn.getInputStream()) {
                    // 指定 UTF-8 编码避免乱码
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                            .parse(new org.xml.sax.InputSource(new java.io.InputStreamReader(is, "UTF-8")));
                    NodeList items = doc.getElementsByTagName("item");

                    for (int i = 0; i < items.getLength(); i++) {
                        org.w3c.dom.Element item = (org.w3c.dom.Element) items.item(i);
                        String title = getText(item, "title");
                        String pubDate = getText(item, "pubDate");
                        String link = getText(item, "link");

                        if (title == null || link == null) continue;
                        // 过滤 Form 13F 等无价值的表格申报新闻
                        if (title.startsWith("Form 13F") || title.startsWith("Form 4")) continue;

                        // 用 link 末尾的唯一 ID 去重
                        String uuid = String.valueOf(Math.abs(link.hashCode()));
                        if (infomationService.getById(uuid) != null) continue;

                        Infomation info = new Infomation();
                        info.setUuid(uuid);
                        info.setLang("en");
                        info.setDescription(title);
                        info.setOriginUrl(link);
                        info.setCreatedAt(pubDate);
                        info.setType("1");
                        infomationService.save(info);
                    }
                }
            } catch (Exception e) {
                log.warn("RSS采集失败, url={}, msg={}", rssUrl, e.getMessage());
            }
        }
    }

    private String getText(org.w3c.dom.Element el, String tag) {
        NodeList nodes = el.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return null;
        return nodes.item(0).getTextContent();
    }
}
