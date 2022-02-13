package com.ty.cm.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Date;

/**
 * 路径工具类
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
public class PathUtil {

    /**
     * 该方法用于生成以时间为规则的路径
     *
     * @param path
     *            ----> 路径
     * @return String
     */
    public static String generatePath(String path) {

        String dir = path;
        dir += DateUtils.getDate(new Date(), "yyyyMMdd") + "/";
        return dir;
    }

    /**
     * 获取相对于ClassPath的完整路径
     *
     * @return String
     */
    public static String getFullCPath(String relativePath) {

        String path = null;
        if (StringUtils.isNotBlank(relativePath)) {
            final URL url = PathUtil.class.getClassLoader().getResource(relativePath);
            path = null != url ? url.getFile() : null;
        }
        return path;
    }
}
