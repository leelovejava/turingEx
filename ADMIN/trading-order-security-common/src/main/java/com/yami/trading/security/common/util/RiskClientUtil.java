package com.yami.trading.security.common.util;

import cn.hutool.core.util.StrUtil;
import com.yami.trading.bean.model.RiskClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RiskClientUtil {

    private static Map<String, RiskClient> riskClientMap = new ConcurrentHashMap<>();

    private static AntPathMatcher ipMatcher = new AntPathMatcher();

    public static void initCache(Map<String, RiskClient> newRiskClientMap) {
        riskClientMap = newRiskClientMap;
    }

    public static Map<String, RiskClient> getRiskClientMap() {
        return Collections.unmodifiableMap(riskClientMap);
    }

    public static void saveRiskConfig(RiskClient config) {
        if (config == null) {
            return;
        }

        String cacheKey = config.getType() + ":" + config.getClientType() + ":" + config.getClientKey();
        riskClientMap.put(cacheKey, config);
    }

    public static void disableRiskConfig(String type, String clientType, String clientKey) {
        String cacheKey = type + ":" + clientType + ":" + clientKey;
        riskClientMap.remove(cacheKey);
    }

    /**
     * 基于指定用户ID值，提取特定风控类型的配置信息；
     * userId 参数必填，如果未设置 type 参数，则提取相关所有配置信息
     *
     * @param userCode
     * @param type
     * @return
     */
    public static List<RiskClient> getRiskInfoByUserCode(String userCode, String type) {
        if (StrUtil.isBlank(userCode)) {
            throw new RuntimeException("userCode参数必填");
        }

        List<RiskClient> riskList = new ArrayList<>();
        if (riskClientMap.isEmpty()) {
            return riskList;
        }

        if (StrUtil.isBlank(type)) {
            String fieldKey1 = "white:userCode:" + userCode.trim();
            String fieldKey2 = "black:userCode:" + userCode.trim();
            String fieldKey3 = "badnetwork:userCode:" + userCode.trim();

            RiskClient config1 = riskClientMap.get(fieldKey1);
            if (config1 != null) {
                riskList.add(config1);
            }

            RiskClient config2 = riskClientMap.get(fieldKey2);
            if (config2 != null) {
                riskList.add(config2);
            }

            RiskClient config3 = riskClientMap.get(fieldKey3);
            if (config3 != null) {
                riskList.add(config3);
            }
        } else {
            String fieldKey1 = type + ":userCode:" + userCode.trim();
            RiskClient config1 = riskClientMap.get(fieldKey1);
            if (config1 != null) {
                riskList.add(config1);
            }
        }

        long currentTimeMillis = Instant.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        List<RiskClient> validRiskList = new ArrayList<>();
        for (RiskClient oneRisk : riskList) {
            if (oneRisk.getStatus() != 1) {
                continue;
            }
            if (oneRisk.getBeginTimeTs().longValue() == 0 && oneRisk.getEndTimeTs().longValue() == 0) {
                // 时间不做限制
                validRiskList.add(oneRisk);
                continue;
            }
            if (oneRisk.getBeginTimeTs() > currentTimeMillis) {
                // 还没开始
                continue;
            }
            if (oneRisk.getEndTimeTs() > 0 && oneRisk.getEndTimeTs() < currentTimeMillis) {
                // 已经结束
                continue;
            }

            // 当前时间在起止时间中间
            validRiskList.add(oneRisk);
        }

        return validRiskList;
    }

    /**
     * 基于指定 IP 值，提取特定风控类型的配置信息；
     * ip 参数必填，如果未设置 type 参数，则提取相关所有配置信息
     *
     * @param clientIp
     * @param type
     * @return
     */
    public static List<RiskClient> getRiskInfoByIp(String clientIp, String type) {
        if (StrUtil.isBlank(clientIp)) {
            return new ArrayList<>();
            //throw new RuntimeException("clientIp参数必填");
        }

        List<RiskClient> riskList = new ArrayList<>();
        if (riskClientMap.isEmpty()) {
            return riskList;
        }

        List<String> keyList = new ArrayList<>();
        if (clientIp.contains("*")) {
            Set<String> cacheKeys = new HashSet<>(riskClientMap.keySet());
            Set<String> optionIps = new HashSet<>();
            for (String oneCacheKey : cacheKeys) {
                if (!oneCacheKey.contains(":ip:")) {
                    continue;
                }

                int idx = oneCacheKey.indexOf(":ip:");
                String ipPatternValue = oneCacheKey.substring(idx + ":ip:".length());
                if (ipMatcher.match(ipPatternValue, clientIp)) {
                    optionIps.add(ipPatternValue);
                    RiskClient curRiskClient = riskClientMap.get(oneCacheKey);
                    if (curRiskClient != null) {
                        riskList.add(curRiskClient);
                    }
                }
            }
        } else {
            if (StrUtil.isBlank(type)) {
                String fieldKey1 = "white:ip:" + clientIp.trim();
                String fieldKey2 = "black:ip:" + clientIp.trim();
                String fieldKey3 = "badnetwork:ip:" + clientIp.trim();

                RiskClient config1 = riskClientMap.get(fieldKey1);
                if (config1 != null) {
                    riskList.add(config1);
                }

                RiskClient config2 = riskClientMap.get(fieldKey2);
                if (config2 != null) {
                    riskList.add(config2);
                }

                RiskClient config3 = riskClientMap.get(fieldKey3);
                if (config3 != null) {
                    riskList.add(config3);
                }
            } else {
                String fieldKey1 = type + ":ip:" + clientIp.trim();
                RiskClient config1 = riskClientMap.get(fieldKey1);
                if (config1 != null) {
                    riskList.add(config1);
                }
            }
        }

        Date now = new Date(); // 注意时区问题
        List<RiskClient> validRiskList = new ArrayList<>();
        for (RiskClient oneRisk : riskList) {
            if (oneRisk.getStatus() != 1) {
                continue;
            }
            if (oneRisk.getBeginTimeTs().longValue() == 0 && oneRisk.getEndTimeTs().longValue() == 0) {
                // 时间不做限制
                validRiskList.add(oneRisk);
                continue;
            }
            if (oneRisk.getBeginTimeTs() > now.getTime()) {
                // 还没开始
                continue;
            }
            if (oneRisk.getEndTimeTs() > 0 && oneRisk.getEndTimeTs() < now.getTime()) {
                // 已经结束
                continue;
            }

            // 当前时间在起止时间中间
            validRiskList.add(oneRisk);
        }

        return validRiskList;
    }

}
