package com.ty.cm.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 有序读取资源文件内容的工具类
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
@Slf4j
public class LinkedPropertiesUtil {

    /**
     * 缓存区
     */
    private static final Map<String, Properties> CACHE_DATA = new HashMap<>();

    private LinkedPropertiesUtil() {

    }

    /**
     * 读取资源文件
     *
     * @param fileName 资源文件名
     * @return 资源文件
     */
    public static Properties loadProperties(String fileName) {

        Properties p = CACHE_DATA.get(fileName);
        if (null == p) {
            InputStream stream = null;
            p = new LinkedProperties();
            try {
                stream = LinkedPropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
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
     * @param fileName 资源文件名
     * @return 资源文件属性集合
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> loadedProperties(String fileName) {

        final Map<String, T> resultMap = new LinkedHashMap<>();
        Properties p = LinkedPropertiesUtil.loadProperties(fileName);
        for (Map.Entry<Object, Object> entry : p.entrySet()) {
            resultMap.put((String) entry.getKey(), (T) entry.getValue());
        }
        return resultMap;
    }

    /**
     * 获取资源文件的属性值
     *
     * @param fileName 资源文件名
     * @param prop     属性名
     * @return 属性值
     */
    public static <T> T loadedProperties(String fileName, String prop) {

        final Map<String, T> resultMap = LinkedPropertiesUtil.loadedProperties(fileName);
        return (T) resultMap.get(prop);
    }

    /**
     * 读取资源文件
     *
     * @param fileName 资源文件名
     * @return 资源文件
     */
    public static String readProperties(String fileName, String attrName) {

        final Properties pps = LinkedPropertiesUtil.loadProperties(fileName);
        final String attrValue = pps.getProperty(attrName);
        return null != attrValue ? attrValue : "";
    }
}
