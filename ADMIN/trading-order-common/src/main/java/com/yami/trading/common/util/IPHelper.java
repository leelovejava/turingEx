package com.yami.trading.common.util;

import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * IP帮助工具
 */
public class IPHelper {
	
    private static final String UNKNOWN = "unknown";
    
    /**
     * IP分割符号
     */
    private static final Pattern IP_DELIMITER=Pattern.compile("[.]|[:]");
    
    /**
	 * 本地回环IP地址集合
	 */
	private static final LinkedHashSet<String> localIPSet = new LinkedHashSet<String>();
	
	static {
		localIPSet.add("127.0.0.1");
		localIPSet.add("0:0:0:0:0:0:0:1");
	}
	
	/**
	 * 是否为本地回环地址
	 * @param srcIp 比较的源地址
	 * @return 是否为回环地址
	 */
	public static final boolean isLoopback(String srcIp) {
		return localIPSet.contains(srcIp);
	}
	
	/**
     * 比较两个IP地址的IP段是否相同
     * @param srcIp 原始地址
     * @param dstIp 目标地址
     * @return IP段是否相同
     */
    public static final boolean equalIpSegment(String srcIp,String dstIp) {
    	if(null==srcIp || (srcIp=srcIp.trim()).isEmpty()) return false;
    	if(null==dstIp || (dstIp=dstIp.trim()).isEmpty()) return false;
    	return getIPSegment(srcIp).equals(getIPSegment(dstIp));
	}
	
	/**
     * 获取IP段
     * @param ip IP地址
     * @return IP地址段
     * @see #isCorrectIpRegular
     */
    public static final String getIPSegment(String ip) {
		if(null==ip || (ip=ip.trim()).isEmpty()) return null;
		String[] bArray=IP_DELIMITER.split(ip);
		int lastIndex=ip.length()-(bArray[bArray.length-1].length()+1);
		return -1==lastIndex?ip:ip.substring(0, lastIndex);
	}

    /**
     * 得到用户的真实地址,如果有多个就取第一个
     * @return
     */
    public static final String getIpAddr() {
        HttpServletRequest request = ApplicationUtil.getServletRequest();
        if (request == null) return null;
        
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        String[] ips = ip.split(",");
        return ips[0].trim();
    }


}
