package com.ty.cm.utils;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ty.cm.constant.Ty.UNKNOWN;

/**
 * 浏览器UA解析工具类
 *
 * @Author Tommy
 * @Date 2022/10/24
 */
public class UserAgentUtil {

    /** OS正则 **/
    protected static Pattern OS_PATTERN = Pattern.compile("iPad|iPhone|Mac OS X|HarmonyOS|Android|Ubuntu|CentOS|Linux|Unix|Windows", Pattern.CASE_INSENSITIVE);

    /** OS别名 **/
    protected static Map<String, String> ALIAS_MAP = new HashMap<String, String>() {
        private static final long serialVersionUID = 3905289916062389111L;
        {
            // Macintosh系列
            put("ipad", "iPadOS");
            put("iphone", "iOS");
            put("mac os x", "Mac OS X");

            // 类Unix与类Linux
            put("harmonyos", "HarmonyOS");
            put("android", "Android");
            put("ubuntu", "Ubuntu");
            put("centos", "CentOS");
            put("linux", "Linux");
            put("unix", "Unix");

            // Windows
            put("windows", "Windows");
        }
    };

    /** OS优先级 **/
    protected static Map<String, Integer> PRIORITY_MAP = new HashMap<String, Integer>() {
        private static final long serialVersionUID = -8771535813003535031L;
        {
            put("ipad", 1);
            put("iphone", 1);
            put("mac os x", 96);
            put("harmonyos", 1);
            put("android", 2);
            put("ubuntu", 3);
            put("centos", 3);
            put("linux", 97);
            put("unix", 98);
            put("windows", 99);
        }
    };

    /**
     * 通过浏览器UA获取客户端操作系统名称
     *
     * @param ua 浏览器UserAgent
     * @return String
     */
    public static String getOSName(String ua) {
        String os = UNKNOWN;
        if (StringUtils.isNotBlank(ua)) {
            TreeMap<Integer, String> resultMap = Maps.newTreeMap();
            Matcher matcher = OS_PATTERN.matcher(ua);
            while (matcher.find()) {
                String val = matcher.group().trim().toLowerCase();
                resultMap.put(PRIORITY_MAP.get(val), val);
            }
            os = resultMap.size() > 0? ALIAS_MAP.get(resultMap.firstEntry().getValue()) : os;
        }
        return os;
    }
}
