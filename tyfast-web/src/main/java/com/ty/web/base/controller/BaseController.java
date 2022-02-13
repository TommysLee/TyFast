package com.ty.web.base.controller;

import com.ty.cm.constant.MIME;
import com.ty.cm.constant.Ty;
import com.ty.cm.utils.DateUtils;
import com.ty.web.spring.CustomDateFormatter;
import com.ty.web.utils.WebIpUtil;
import com.ty.web.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * 基础Controller
 *
 * @Author Tommy
 * @Date 2022/2/3
 */
@Slf4j
public abstract class BaseController {

    /**
     * 日期属性编辑器
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addCustomFormatter(new CustomDateFormatter()); // 日期字符串解析器
    }

    /**
     * 对给定字符进行 URL 解码
     *
     * @param value 字符串
     * @return String
     */
    protected String decode(String value) {
        String result = WebUtil.decodeU8(value);
        result = WebUtil.decodeU8(result);
        return result;
    }

    /**
     * 对给定字符进行 URL 编码
     *
     * @param value 字符串
     * @return String
     */
    protected String encode(String value) {
        String result = WebUtil.encodeU8(value);
        result = WebUtil.encodeU8(result);
        return result;
    }

    /**
     * 设置不缓存
     */
    protected void setNullCache(HttpServletResponse response) {
        WebUtil.disableHttpCache(response);
    }

    /**
     * 获取Shiro Session
     */
    public static Session getSession() {
        return WebUtil.getSession();
    }

    /**
     * 获取Shiro Session属性
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttribute(String key) {
        return (T) getSession().getAttribute(key);
    }

    /**
     * 获取当前登录的用户
     */
    public <T> T getCurrentUser() {
        return WebUtil.getCurrentAccount();
    }

    /**
     * 获取当前登录的用户ID
     */
    public String getCurrentUserId() {
        return WebUtil.getCurrentUserId();
    }

    /**
     * 从请求中获取用户IP
     */
    public String getClientIP(HttpServletRequest request) {
        return WebIpUtil.getClientIP(request);
    }

    /**
     * 获取发起请求的页面URL
     */
    protected String getRefererURL(HttpServletRequest request) throws Exception {

        final String url = request.getHeader("referer");
        return url;
    }

    /**
     * 文件下载（基于Java NIO）
     */
    @SuppressWarnings("resource")
    protected void download(HttpServletRequest request, HttpServletResponse response, String fileName, String mime, File file) throws Exception {

        if (file.exists() && file.isFile()) {
            if (StringUtils.isNotBlank(fileName)) {
                final String agent = WebUtil.getUserAgent(request);
                if (StringUtils.isNotBlank(agent) && agent.toLowerCase().indexOf("firefox") != -1) {
                    fileName = new String(fileName.getBytes(), "ISO-8859-1");
                } else {
                    fileName = WebUtil.encodeU8(fileName);
                }
            }

            // 下载参数设置
            response.reset();
            response.setContentType(StringUtils.isNotBlank(mime) ? mime : MIME.STREAM); // MIME
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName); // 下载文件名称
            response.addHeader("Content-Length", "" + file.length()); // 下载文件大小

            OutputStream outStream = null;
            FileChannel readChannel = null;
            WritableByteChannel writeChannel = null;
            try {
                outStream = response.getOutputStream(); // 输出流
                writeChannel = Channels.newChannel(outStream); // 文件通道写
                readChannel = new FileInputStream(file).getChannel(); // 文件通道读

                ByteBuffer buffer = ByteBuffer.allocate(Ty.DEFAULT_BUFFER_SIZE);
                while (-1 != readChannel.read(buffer)) {
                    buffer.flip();
                    try {
                        writeChannel.write(buffer);
                    } catch (Exception ie) {
                        log.warn("用户在下载文件时，可能主动终止了下载！！！");
                        break;
                    }
                    buffer.clear();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                if (null != readChannel)
                    readChannel.close();
                if (null != writeChannel)
                    writeChannel.close();

                if (null != outStream)
                    outStream.close();
            }
        } else {
            WebUtil.writeText(response, "文件 \"" + fileName + "\" 丢失或不是文件！");
        }
    }

    /**
     * 文件下载（基于Java NIO）
     */
    protected void download(HttpServletRequest request, HttpServletResponse response, String fileName, String mime, String path) throws Exception {
        File file = new File(path);
        this.download(request, response, fileName, mime, file);
    }

    /**
     * 获取服务器时间
     */
    public String time() throws Exception {
        return DateUtils.time();
    }
}
