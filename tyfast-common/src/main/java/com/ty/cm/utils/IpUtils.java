package com.ty.cm.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取IP相关信息实用工具类
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
public class IpUtils {

    // IP尾号
    private static String TAIL = null;

    // IP地址规范正则
    private static Pattern IP_PATTERN = Pattern.compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])$");

    /**
     * 获取本机所有IP地址
     *
     * @return
     */
    public static List<String> getAllLocalHostIP() {
        final List<String> ipList = new ArrayList<>();
        try {
            final Enumeration<NetworkInterface> netInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            InetAddress ip = null;
            if (null != netInterfaces) { // 如果在此机器可以找到网络接口
                while (netInterfaces.hasMoreElements()) {
                    NetworkInterface ni = netInterfaces.nextElement();
                    Enumeration<InetAddress> addr = ni.getInetAddresses();
                    while (addr.hasMoreElements()) {
                        ip = addr.nextElement();
                        if (null != ip && ip instanceof Inet4Address) {
                            Inet4Address ipv4 = (Inet4Address) ip;
                            if (!ipv4.isLoopbackAddress()) {
                                ipList.add(ipv4.getHostAddress());
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Collections.reverse(ipList);
        return ipList;
    }

    /**
     * 获取本机IP地址
     *
     * @return
     */
    public static String getLocalHostIP() {
        String ipAddr = "127.0.0.1";
        final List<String> ipList = getAllLocalHostIP();
        for (String ip : ipList) {
            if (!ip.equalsIgnoreCase("127.0.0.1")) {
                ipAddr = ip;
                break;
            }
        }
        return ipAddr;
    }

    /**
     * 获取本机IP地址尾号
     *
     * @return String
     */
    public static String getLocalHostIPTail() {

        if (null == TAIL) {
            final Matcher matcher = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})").matcher(getLocalHostIP());
            TAIL = matcher.matches() ? NumberUtil.fillZero(matcher.group(4), 3) : "000";
        }
        return TAIL;
    }

    /**
     * 检查IP格式是否正确
     *
     * @param ip
     * @return boolean
     */
    public static boolean checkIP(String ip) {
        boolean flag = false;
        if (StringUtils.isNotBlank(ip)) {
            flag = IP_PATTERN.matcher(ip).matches();
        }
        return flag;
    }

    /**
     * 获取本机主机名称
     *
     * @return String
     */
    public static String getHostName() {

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {

        }
        return "未知";
    }

    /**
     * 获取物理网卡地址
     *
     * @param host ----> IP地址或主机名（IP不能为：127.0.0.1）
     * @return
     */
    public static String getMacAddress(String host) {
        String mac = "";
        final StringBuffer sb = new StringBuffer();

        try {
            final NetworkInterface ni = NetworkInterface
                    .getByInetAddress(InetAddress.getByName(host));
            if (null != ni) {
                byte[] macs = ni.getHardwareAddress();
                for (int i = 0; i < macs.length; i++) {
                    mac = Integer.toHexString(macs[i] & 0xFF);

                    if (mac.length() == 1) {
                        mac = '0' + mac;
                    }

                    sb.append(mac + "-");
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (sb.length() > 0) {
            mac = sb.toString();
            mac = mac.substring(0, mac.length() - 1);
        }
        return mac;
    }

    /**
     * 判断IP是否为内网IP
     *
     * @param ip
     * @return boolean
     */
    public static boolean internalIp(String ip) {

        byte[] addr = textToNumericFormatV4(ip);
        return internalIp(addr) || "127.0.0.1".equals(ip);
    }

    private static boolean internalIp(byte[] addr) {
        if (DataUtil.isNull(addr) || addr.length < 2) {
            return false;
        }
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        // 172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        // 192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }

    /**
     * 将IPv4地址转换成字节
     *
     * @param text IPv4地址
     * @return byte 字节
     */
    public static byte[] textToNumericFormatV4(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        try {
            long l;
            int i;
            switch (elements.length) {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L)) {
                        return null;
                    }
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L)) {
                        return null;
                    }
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return bytes;
    }
}
