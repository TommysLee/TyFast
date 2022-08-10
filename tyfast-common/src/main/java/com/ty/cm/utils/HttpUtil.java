package com.ty.cm.utils;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Http工具类
 *
 * @Author Tommy
 * @Date 2022/8/9
 */
@Slf4j
public class HttpUtil {

    public static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * GET请求
     *
     * @param url URL
     * @return String 返回响应内容
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * GET请求
     *
     * @param url URL
     * @param queryParams 查询字符串参数
     * @return String 返回响应内容
     */
    public static String get(String url, Map<String, String> queryParams) {
        return get(url, queryParams, headers());
    }

    /**
     * GET请求
     *
     * @param url URL
     * @param queryParams   查询字符串参数
     * @param headers       Http请求头对象
     * @return String 返回响应内容
     */
    public static String get(String url, Map<String, String> queryParams, Headers headers) {
        String responseBody = null;
        if (StringUtils.isNotBlank(url)) {
            // 将查询参数字符串添加到URL
            url = buildUrl(url, queryParams);

            // 构造请求对象
            Request request = new Request.Builder().url(url).headers(headers).get().build();

            // 执行请求并接收响应内容
            responseBody = execute(request);
        }
        return responseBody;
    }

    /**
     * GET请求，返回JSON对象
     *
     * @param url URL
     * @param queryParams   查询字符串参数
     * @param headers       Http请求头对象
     * @param clazz         JSON对象的Class
     * @return 返回JSON对象
     */
    public static <T> T getForJson(String url, Map<String, String> queryParams, Headers headers, Class<T> clazz) {
        String responseBody = get(url, queryParams, headers);
        return deserialization(responseBody, clazz);
    }

    /**
     * POST请求
     *
     * @param url URL
     * @return String 返回响应内容
     */
    public static String post(String url) {
        return post(url, null);
    }

    /**
     * POST请求
     *
     * @param url URL
     * @param params 请求参数
     * @return String 返回响应内容
     */
    public static String post(String url, Map<String, String> params) {
        return post(url, params, headers());
    }

    /**
     * POST请求
     *
     * @param url URL
     * @param params  请求参数
     * @param headers Http请求头对象
     * @return String 返回响应内容
     */
    public static String post(String url, Map<String, String> params, Headers headers) {
        String responseBody = null;
        if (StringUtils.isNotBlank(url)) {
            // 构造参数对象
            FormBody.Builder formBuilder = new FormBody.Builder();
            if (null != params) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    formBuilder.add(entry.getKey(), entry.getValue());
                }
            }
            RequestBody formBody = formBuilder.build();

            // 构造请求对象
            Request request = new Request.Builder().url(url).headers(headers).post(formBody).build();

            // 执行请求并接收响应内容
            responseBody = execute(request);
        }
        return responseBody;
    }

    /**
     * POST请求，返回JSON对象
     *
     * @param url URL
     * @param params    请求参数
     * @param headers   Http请求头对象
     * @param clazz     JSON对象的Class
     * @return 返回JSON对象
     */
    public static <T> T postForJson(String url, Map<String, String> params, Headers headers, Class<T> clazz) {
        String responseBody = post(url, params, headers);
        return deserialization(responseBody, clazz);
    }

    /**
     * 执行Request请求
     *
     * @param request
     * @return String 返回响应内容
     */
    public static String execute(Request request) {
        String responseBody = null;
        if (null != request) {
            try (Response response = client.newCall(request).execute()) {
                if (response.code() < 500) {
                    responseBody = response.body().string();

                    if (response.code() == 401) {
                        log.warn("请求需认证后访问：" + request.url() + ", Reason: " + responseBody);
                    }
                } else {
                    log.warn("OKHttp3 " + request.method() + " 请求失败：" + response);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return responseBody;
    }

    /**
     * 使用参数构造URL，多用于GET请求
     *
     * @param url URL
     * @param queryParams 查询字符串参数
     * @return String
     */
    public static String buildUrl(String url, Map<String, String> queryParams) {
        if (StringUtils.isNotBlank(url) && null != queryParams && queryParams.size() > 0) {
            HttpUrl httpUrl = HttpUrl.parse(url);
            HttpUrl.Builder builder = new HttpUrl.Builder().scheme(httpUrl.scheme()).host(httpUrl.host()).addPathSegments(String.join("/", httpUrl.pathSegments())).query(httpUrl.query());
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            url = builder.build().toString();
        }
        return url;
    }

    /**
     * 标准的HTTP请求头
     *
     * @return Headers
     */
    public static Headers headers() {
        return headers(null);
    }

    /**
     * 含Basic认证的HTTP请求头
     *
     * @param userName 用户名
     * @param password 密码
     * @return Headers
     */
    public static Headers headersWithBasic(String userName, String password) {
        Map<String, String> appendHeaders = Maps.newHashMap();
        if (null != userName && null != password) {
            String authorization = userName + ":" + password;
            authorization = "Basic " + Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8));
            appendHeaders.put("Authorization", authorization);
        }
        return headers(appendHeaders);
    }

    /**
     * HTTP 请求头
     *
     * @param appendHeaders 追加的请求头
     * @return Headers
     */
    public static Headers headers(Map<String, String> appendHeaders) {
        // 常规的Http请求头
        Map<String, String> headersMap = Maps.newHashMap();
        headersMap.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        headersMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.53 Safari/537.36 Edg/103.0.1264.37");

        // 追加请求头
        if (null != appendHeaders && appendHeaders.size() > 0) {
            headersMap.putAll(appendHeaders);
        }
        return Headers.of(headersMap);
    }

    /**
     * JSON反序列化
     *
     * @param json  JSON字符串
     * @param clazz JSON对象的Class
     * @return 返回JSON对象
     */
    public static <T> T deserialization(String json, Class<T> clazz) {
        if (StringUtils.isNotBlank(json)) {
            T bean = DataUtil.fromJSON(json, clazz);
            return bean;
        }
        return null;
    }
}
