package com.ty.cm.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 资源文件读取工具类
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
@Slf4j
public class PropertiesUtil {

    /**
     * 缓存区
     */
    private static final Map<String, Properties> CACHE_DATA = new HashMap<>();

    /**
     * 读取资源文件
     *
     * @param fileName
     *            资源文件名
     * @return 资源文件
     */
    public static Properties loadProperties(String fileName) {

        Properties p = CACHE_DATA.get(fileName);
        if (null == p) {
            InputStream stream = null;
            p = new Properties();
            try {
                stream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
                p.load(stream);
                CACHE_DATA.put(fileName, p);
            } catch (IOException e) {
                log.error("读取" + fileName + "失败", e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        log.error("关闭" + fileName + "失败", e);
                    }
                }
            }
        } else {
            log.info(fileName + "\t从缓存中加载资源文件数据");
        }
        return p;
    }

    /**
     * 获取资源文件属性集合
     *
     * @param fileName
     *            资源文件名
     * @return 资源文件属性集合
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> loadedProperties(String fileName) {

        final Map<String, T> resultMap = new LinkedHashMap<>();
        Properties p = PropertiesUtil.loadProperties(fileName);
        for (Map.Entry<Object, Object> entry : p.entrySet()) {
            resultMap.put((String) entry.getKey(), (T) entry.getValue());
        }
        return resultMap;
    }

    /**
     * 获取资源文件的属性值
     *
     * @param fileName
     *            资源文件名
     * @param prop
     *            属性名
     * @return 属性值
     */
    public static <T> T loadedProperties(String fileName, String prop) {

        final Map<String, T> resultMap = PropertiesUtil.loadedProperties(fileName);
        return (T) resultMap.get(prop);
    }

    /**
     * 获取资源文件的属性值并反转
     *
     * @param fileName
     *            资源文件名
     * @param props
     *            属性名
     * @return 资源文件属性集合
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> loadedPropertiesReverse(String fileName, String... props) {

        final Map<String, T> reverseMap = new LinkedHashMap<>();
        if (null != props && props.length > 0) {
            final Map<String, T> resultMap = PropertiesUtil.loadedProperties(fileName);
            for (String prop : props) {
                if (resultMap.containsKey(prop)) {
                    reverseMap.put((String) resultMap.get(prop), (T) prop);
                }
            }
        }
        return reverseMap;
    }

    /**
     * 获取资源文件的属性值，并反转，且按值切分
     *
     * @param fileName
     *            资源文件名
     * @param props
     *            属性名
     * @return 资源文件属性集合
     */
    public static <T> Map<String, T> loadedPropertiesReverseSplit(String fileName, String... props) {

        final Map<String, T> reverseSplitMap = new LinkedHashMap<String, T>();
        final Map<String, T> reverseMap = loadedPropertiesReverse(fileName, props);
        for (Map.Entry<String, T> entry : reverseMap.entrySet()) {
            String[] keys = entry.getKey().split(",");
            for (String key : keys) {
                reverseSplitMap.put(key.trim(), entry.getValue());
            }
        }
        return reverseSplitMap;
    }

    /**
     * 读取资源文件
     *
     * @param fileName
     *            资源文件名
     * @return 资源文件
     */
    public static String readProperties(String fileName, String attrName) {

        final Properties pps = PropertiesUtil.loadProperties(fileName);
        final String attrValue = pps.getProperty(attrName);
        return null != attrValue ? attrValue : "";
    }

    /**
     * 读取资源文件
     *
     * @param fileName
     *            资源文件名
     * @return 资源文件
     */
    public static String readProperties(String fileName, Integer attrName) {

        final Properties pps = PropertiesUtil.loadProperties(fileName);
        final String attrValue = pps.getProperty(null != attrName ? attrName.toString() : "");
        return null != attrValue ? attrValue : "";
    }
}
