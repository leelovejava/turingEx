package com.yami.trading.common.util;

import cn.hutool.core.util.StrUtil;
import com.yami.trading.common.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 检测IP合法性工具类
 *
 */
public class IpUtil {
    private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);

    private static final String ANYHOST_VALUE = "0.0.0.0";
    private static final String LOCALHOST_VALUE = "127.0.0.1";
    private static volatile InetAddress LOCAL_ADDRESS = null;
    private static final int MIN_USER_PORT_NUMBER = 1024;
    private static final int MAX_USER_PORT_NUMBER = 65536;
    private static final long[][] intranet_ip_ranges = new long[][] {
            {ipToInt("10.0.0.0"), ipToInt("10.255.255.255")},
            {ipToInt("172.16.0.0"), ipToInt("172.31.255.255")},
            {ipToInt("192.168.0.0"), ipToInt("192.168.255.255")}
    };

    public static String ipRegix =
            "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

    public static Pattern ipPattern = Pattern.compile(ipRegix);
    private static String localIp = null;

    private static Pattern pattern;
    
    private static Pattern ptipv4;
    
    static {
        // ipv6
        pattern = Pattern.compile("^((([0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){1,7}:)|(([0-9A-Fa-f]{1,4}:){6}:[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){5}(:[0-9A-Fa-f]{1,4}){1,2})|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){1,3})|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){1,4})|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){1,5})|([0-9A-Fa-f]{1,4}:(:[0-9A-Fa-f]{1,4}){1,6})|(:(:[0-9A-Fa-f]{1,4}){1,7})|(([0-9A-Fa-f]{1,4}:){6}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){5}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){0,1}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){0,2}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){0,3}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|([0-9A-Fa-f]{1,4}:(:[0-9A-Fa-f]{1,4}){0,4}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(:(:[0-9A-Fa-f]{1,4}){0,5}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}))$");
        // ipv4
        ptipv4 = Pattern.compile("^(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$");
    }

    /**
     * 正则表达式 判断字符是否为IP
     *
     */
    public static boolean isCorrectIpRegular(String ipString) {
        if (StringUtils.isNullOrEmpty(ipString)) {
            return false;
        }
        
        boolean ipv6 = pattern.matcher(ipString).matches();
        boolean ipv4 = ptipv4.matcher(ipString).matches();
        
        if (!ipv6 && !ipv4) {
        	System.out.println("IP不合法: " + ipString);
            return false;
        }

        return true;
    }

    public static boolean isIp(String in) {
        return in != null && ipPattern.matcher(in).matches();
    }

    /**
     * 获取本机所有ip.
     *
     * @return map key为网卡名 value为对应ip
     */
    public static Map<String, String> getLocalIps() {
        try {
            Map<String, String> result = new HashMap<String, String>();
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                String name = ni.getName();
                String ip = "";
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress address = ips.nextElement();
                    if (address instanceof Inet4Address) {
                        ip = address.getHostAddress();
                        break;
                    }
                }
                result.put(name, ip);
            }
            return result;
        } catch (SocketException e) {
            logger.error("getLocalIP error", e);
            return Collections.emptyMap();
        }
    }

    /**
     * 获取服务器ip 判断规则 eth0 > eth1 > ... ethN > wlan > lo.
     */
    public static String getLocalIp() {
        Map<String, String> ips = getLocalIps();
        List<String> faceNames = new ArrayList<String>(ips.keySet());
        Collections.sort(faceNames);

        for (String name : faceNames) {
            if ("lo".equals(name)) {
                continue;
            }
            String ip = ips.get(name);
            if (StrUtil.isNotBlank(ip)) {
                return ip;
            }
        }
        return "127.0.0.1";
    }

    /**
     * 只获取一次ip.
     */
    public static String getSingleLocalIp() {
        if (localIp == null) {
            localIp = getLocalIp();
        }
        return localIp;
    }

    /**
     * 随机返回可用端口.
     *
     * @return 端口号
     */
    public static int randomAvailablePort() {
        int port;
        do {
            port = (int) ((MAX_USER_PORT_NUMBER - MIN_USER_PORT_NUMBER) * Math.random())
                    + MIN_USER_PORT_NUMBER;
        } while (!availablePort(port));
        return port;
    }

    /**
     * 检测该端口是否可用 <br/> 端口必须大于 0 小于 {@value #MAX_USER_PORT_NUMBER}.
     *
     * @param port 端口号
     * @return 是否可用
     */
    public static boolean availablePort(int port) {
        if (port < 0 || port > MAX_USER_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            // ignore
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    /**
     * .
     */
    public static String getRequestIp(HttpServletRequest request) {
        // just for test.
        if (request == null) {
            request = ApplicationUtil.getServletRequest();
            if (request == null) {
                return null;
            }
        }

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Matrix-RemoteIP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Original-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Fastly-Client-Ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("True-Client-Ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Forwarded");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.length() != 0) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        //logger.info("获取日志打印【{}】", ip);
        return ip != null ? ip.trim() : "127.0.0.1";
    }

    /**
     * 是否为内网ip A类 10.0.0.0-10.255.255.255 B类 172.16.0.0-172.31.255.255 C类
     * 192.168.0.0-192.168.255.255 不包括回环ip.
     *
     * @param ip 需要判断的ip地址
     * @return 是否是内网ip
     */
    public static boolean isIntranetIP(String ip) {
        if (!isIp(ip)) {
            return false;
        }
        long ipNum = ipToInt(ip);
        for (long[] range : intranet_ip_ranges) {
            if (ipNum >= range[0] && ipNum <= range[1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert IP to Int.
     *
     * @param address   ip地址
     * @param isSegment true IP segment, false full IP.
     * @return 数字
     */
    public static int ipToInt(final String address, final boolean isSegment) {
        String customAddress = address;
        String[] ips = address.split(",");
        if (ips.length > 1) {
            customAddress = ips[0];
        }
        final String[] addressBytes = customAddress.split("\\.");
        int length = addressBytes.length;
        if (length < 3) {
            return 0;
        }
        int ip = 0;
        try {
            for (int i = 0; i < 3; i++) {
                ip <<= 8;
                ip |= Integer.parseInt(addressBytes[i]);
            }
            ip <<= 8;
            if (isSegment || length == 3) {
                ip |= 0;
            } else {
                ip |= Integer.parseInt(addressBytes[3]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Warn ipToInt address is wrong: address=" + address, e);
        }

        return ip;
    }

    /**
     * 将ip转化为数字，并且保持ip的大小顺序不变 如 ipToInt("10.75.0.1") > ipToInt("10.75.0.0") 如果ip不合法则返回 0.
     *
     * @param addr ip地址
     * @return 数字
     */
    public static int ipToInt(final String addr) {
        return ipToInt(addr, false);
    }

    /**
     * ip转化为数字，并且保持ip的大小顺序不变 如果ip不合法则返回0.
     * ParseIP parses s as an IP address, returning the result.
     * The string s can be in IPv4 dotted decimal ("192.0.2.1"), IPv6
     * ("2001:db8::68"), or IPv4-mapped IPv6 ("::ffff:192.0.2.1") form.
     * If s is not a valid textual representation of an IP address,
     * ParseIP returns nil.
     * @param addr ip地址
     * @return 数字
     */
    public static long ipToLong(String addr) {
        if (addr == null) {
            return 0;
        }
        if (addr.contains(Constants.DOU_HAO)) {
            addr = addr.substring(0, addr.indexOf(Constants.DOU_HAO));
        }
        //logger.info("ipToLong获取地址参数ip 【{}】", addr);
        if (addr.length() > 20) {
            return 0;
        }

        if (addr.length() < 4) {
            throw new RuntimeException("ip地址错误 wrong ipAddr format address=" + addr);
        }

        if (addr.contains(Constants.MAO_HAO)) {
            return 0;
        }
        final String[] addressBytes = addr.split("\\.");
        int length = addressBytes.length;
        if (length < 3) {
            return 0;
        }
        long ip = 0;
        try {
            for (int i = 0; i < 3; i++) {
                ip <<= 8;
                ip |= Integer.parseInt(addressBytes[i]);
            }
            ip <<= 8;
            if (length == 3) {
                ip |= 0;
            } else {
                ip |= Integer.parseInt(addressBytes[3]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Warn ipToLong address is wrong: address=" + addr, e);
        }

        return ip;
    }

    /**
     * 将数字转化成ip.
     */
    public static String intToIp(int ip) {
        return ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }

    /**
     * 将数字转化成ip.
     */
    public static String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }

    private static InetAddress toValidAddress(InetAddress address) {
        if (address instanceof Inet6Address) {
            Inet6Address v6Address = (Inet6Address) address;
            if (isPreferIPV6Address()) {
                return normalizeV6Address(v6Address);
            }
        }
        if (isValidV4Address(address)) {
            return address;
        }
        return null;
    }

    private static boolean isPreferIPV6Address() {
        return Boolean.getBoolean("java.net.preferIPv6Addresses");
    }

    /**
     * valid Inet4Address
     *
     * @param address
     * @return
     */
    private static boolean isValidV4Address(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        boolean result = (name != null
                && ptipv4.matcher(name).matches()
                && !ANYHOST_VALUE.equals(name)
                && !LOCALHOST_VALUE.equals(name));
        return result;
    }


    /**
     * normalize the ipv6 Address, convert scope name to scope id.
     * e.g.
     * convert
     * fe80:0:0:0:894:aeec:f37d:23e1%en0
     * to
     * fe80:0:0:0:894:aeec:f37d:23e1%5
     * <p>
     * The %5 after ipv6 address is called scope id.
     * see java doc of {@link Inet6Address} for more details.
     *
     * @param address the input address
     * @return the normalized address, with scope id converted to int
     */
    private static InetAddress normalizeV6Address(Inet6Address address) {
        String addr = address.getHostAddress();
        int i = addr.lastIndexOf('%');
        if (i > 0) {
            try {
                return InetAddress.getByName(addr.substring(0, i) + '%' + address.getScopeId());
            } catch (UnknownHostException e) {
                // ignore
                logger.debug("Unknown IPV6 address: ", e);
            }
        }
        return address;
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            InetAddress addressItem = toValidAddress(localAddress);
            if (addressItem != null) {
                return addressItem;
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (null == interfaces) {
                return localAddress;
            }
            while (interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = interfaces.nextElement();
                    if (network.isLoopback() || network.isVirtual() || !network.isUp()) {
                        continue;
                    }
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        try {
                            InetAddress addressItem = toValidAddress(addresses.nextElement());
                            if (addressItem != null) {
                                try {
                                    if(addressItem.isReachable(100)){
                                        return addressItem;
                                    }
                                } catch (IOException e) {
                                    // ignore
                                }
                            }
                        } catch (Throwable e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
        return localAddress;
    }

    /**
     * Find first valid IP from local network card
     *
     * @return first valid local IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    /**
     * get ip address
     *
     * @return String
     */
    public static String getIp(){
        return getLocalAddress().getHostAddress();
    }

    /**
     * get ip:port
     *
     * @param port
     * @return String
     */
    public static String getIpPort(int port){
        String ip = getIp();
        return getIpPort(ip, port);
    }

    public static String getIpPort(String ip, int port){
        if (ip==null) {
            return null;
        }
        return ip.concat(":").concat(String.valueOf(port));
    }

    public static Object[] parseIpPort(String address){
        String[] array = address.split(":");

        String host = array[0];
        int port = Integer.parseInt(array[1]);

        return new Object[]{host, port};
    }

}
