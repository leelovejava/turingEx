package com.yami.trading.huobi.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import com.yami.trading.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

/**
 * webSocket服务层 这里我们连接webSocket的时候，
 * <p>
 * 路径中传一个参数值type，用来区分不同页面推送不同的数据
 */
@ServerEndpoint(value = "/api/websocket/{type}/{param}")
@Slf4j
@Component
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。
     * <p>
     * 后面把它设计成线程安全的。
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    public static ConcurrentHashMap<String, WebSocketSession> realtimeMap = new ConcurrentHashMap<String, WebSocketSession>();
    public static ConcurrentHashMap<String, WebSocketSession> tradeMap = new ConcurrentHashMap<String, WebSocketSession>();
    public static ConcurrentHashMap<String, WebSocketSession> depthMap = new ConcurrentHashMap<String, WebSocketSession>();


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam(value = "type") String type,
                       @PathParam(value = "param") String param, Session session) {
        String language = "en";
        String queryString = session.getQueryString();
        if (StringUtils.isNotEmpty(queryString)) {
            String[] split = queryString.split("=");
            if (split.length == 2) {
                language = split[1];
            }
        }
        WebSocketSession webSocketSession = new WebSocketSession(session, getTimeInMillis(), type, param, language);
        // 加入set中
        if (WebSocketEnum.SOCKET_ENUM_REALTIME.getCode().equals(type)) {
            realtimeMap.put(session.getId(), webSocketSession);
        } else if (WebSocketEnum.SOCKET_ENUM_TRADE.getCode().equals(type)) {
            tradeMap.put(session.getId(), webSocketSession);
        } else if (WebSocketEnum.SOCKET_ENUM_DEPTH.getCode().equals(type)) {
            depthMap.put(session.getId(), webSocketSession);
        }

        // 在线数加1
        addOnlineCount();
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        log.info("有新连接加入！请求ID：{}，当前在线人数为{}", webSocketSession.getSetKey(), getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam(value = "type") String type,
                        @PathParam(value = "param") String param, Session session) {
        String id = session.getId();
        String setKey = id + "_" + type + "_" + param;
        System.out.println("关闭连接的setKey：" + setKey);
        if (setKey != null && !"".equals(setKey)) {
            // 从set中删除
            if (WebSocketEnum.SOCKET_ENUM_REALTIME.getCode().equals(type)) {
                realtimeMap.remove(id);
            } else if (WebSocketEnum.SOCKET_ENUM_TRADE.getCode().equals(type)) {
                tradeMap.remove(id);
            } else if (WebSocketEnum.SOCKET_ENUM_DEPTH.getCode().equals(type)) {
                depthMap.remove(id);
            }
            // 在线数减1
            subOnlineCount();
            try {
                if (session != null) {
                    session.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("有一连接关闭！请求ID：" + id + "当前在线人数为" + getOnlineCount());
            log.info("有一连接关闭！请求ID：{}，当前在线人数为{}", setKey, getOnlineCount());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String sessionId = session.getId();
        if (realtimeMap.containsKey(sessionId)) {
            realtimeMap.get(sessionId).setTimeStr(getTimeInMillis());
        } else if (tradeMap.containsKey(sessionId)) {
            tradeMap.get(sessionId).setTimeStr(getTimeInMillis());
        } else if (depthMap.containsKey(sessionId)) {
            depthMap.get(sessionId).setTimeStr(getTimeInMillis());
        }
    }

    /**
     * 发生错误时调用
     **/
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误:" + error);
        error.printStackTrace();
    }


    /**
     * 给指定的请求发送消息
     */
    public static void sendToMessageById(String key, String message, String type) {
        try {
            if (WebSocketEnum.SOCKET_ENUM_REALTIME.getCode().equals(type)) {
                if (realtimeMap.get(key) != null) {
                    WebSocketSession webSocketSession = realtimeMap.get(key);
                    String lang = webSocketSession.getLang();
                    JSONObject jsonObject = JSONObject.parseObject(message);
                    JSONArray data = jsonObject.getJSONArray("data");
                    String name = (String) data.getJSONObject(0).get("name");
                    if ("CN".equalsIgnoreCase(lang)) {
                        name = HanLP.convertToTraditionalChinese(name);
                    } else if (!"zh-CN".equalsIgnoreCase(lang)) {
                        name = (String) data.getJSONObject(0).get("enName");
                    }
                    data.getJSONObject(0).put("name", name);
                    message = JSON.toJSONString(jsonObject);
                    webSocketSession.sendMessage(message);
                } else {
                    System.out.println("realtimeMap中没有此key，不推送消息");
                }
            } else if (WebSocketEnum.SOCKET_ENUM_TRADE.getCode().equals(type)) {
                if (tradeMap.get(key) != null) {
                    tradeMap.get(key).sendMessage(message);
                } else {
                    System.out.println("tradeMap中没有此key，不推送消息");
                }
            } else if (WebSocketEnum.SOCKET_ENUM_DEPTH.getCode().equals(type)) {
                if (depthMap.get(key) != null) {
                    depthMap.get(key).sendMessage(message);
                } else {
                    System.out.println("depthMap中没有此key，不推送消息");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long getTimeInMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, c.get(Calendar.SECOND) + 60);
        return c.getTimeInMillis();
    }


    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
