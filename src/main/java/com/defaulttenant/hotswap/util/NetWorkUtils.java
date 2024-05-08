package com.defaulttenant.hotswap.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络工具类
 *
 * @author sys
 */
public final class NetWorkUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetWorkUtils.class);

    /**
     * ipv4地址数值化
     * @param strIp
     * @return
     */
    public static long ipV4AddressToLong(String strIp) {
        if(StringUtils.isBlank(strIp)) {
            return 0;
        }

        String[]ip = strIp.split("\\.");
        if (ip.length < 4) {
            return 0;
        }

        return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) +
                (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
    }

    /**
     * 获取本机ipv4地址，排除localhost、127.0.0.1
     * 注意: 可能返回空
     * @return
     */
    public static String getHostIpV4Address(){
        Enumeration<NetworkInterface> allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            LOGGER.warn("get ip address error");
            return null;
        }

        while (allNetInterfaces.hasMoreElements()){
            NetworkInterface netInterface = allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()){
                InetAddress ip = addresses.nextElement();
                if (ip != null
                        && ip instanceof Inet4Address
                        && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                        && ip.getHostAddress().indexOf(":")==-1){
                    return ip.getHostAddress();
                }
            }
        }

        return null;
    }

    public static boolean isLocalHost(String host) {
        if (StringUtils.isBlank(host)) {
            return false;
        }

        return "127.0.0.1".equals(host) || "localhost".equals(host) || "::1".equals(host);
    }

    public static String getCurrentIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
