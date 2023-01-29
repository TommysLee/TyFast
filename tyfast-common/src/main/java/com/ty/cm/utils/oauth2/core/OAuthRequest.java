package com.ty.cm.utils.oauth2.core;

import com.google.common.collect.Maps;
import com.ty.cm.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于表示OAuth2 HttpRequest
 *
 * @Author Tommy
 * @Date 2022/12/22
 */
@Slf4j
public class OAuthRequest {

    public final String url;
    public final Verb verb;
    private final Map<String, String> oauthParameters = new HashMap<>(); // OAuth参数
    private final Map<String, String> querystringParams = new LinkedHashMap<>(); // 查询参数
    private final Map<String, String> headers = new HashMap<>(); // Header参数

    private Map<String, String> specs = new HashMap<>(); // OAuth2.0 参数规范

    /**
     * 构造函数
     *
     * @param url
     * @param verb
     */
    public OAuthRequest(String url, Verb verb) {
        this.url = url;
        this.verb = verb;
    }

    /**
     * 构造函数
     *
     * @param url
     * @param verb
     * @param specs
     */
    public OAuthRequest(String url, Verb verb, Map<String, String> specs) {
        this.url = url;
        this.verb = verb;
        this.specs = null != specs ? specs : this.specs;
    }

    /**
     * Adds an OAuth parameter.
     *
     * @param key
     *              the parameter name
     * @param value
     *              the parameter value
     */
    public void addOAuthParameter(String key, String value) {
        oauthParameters.put(key, value);
    }

    /**
     * Add a QueryString parameter
     *
     * @param key
     *              the parameter name
     * @param value
     *              the parameter value
     */
    public void addQuerystringParameter(String key, String value) {
        querystringParams.put(key, value);
    }

    /**
     * Add an HTTP Header to the Request
     *
     * @param key
     *              the header name
     * @param value
     *              the header value
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * 获取Http QueryString parameter
     *
     * @return Map<String, String>
     */
    public Map<String, String> getQuerystringParams() {
        return querystringParams;
    }

    /**
     * 获取Http OAuthParameters parameter
     *
     * @return Map<String, String>
     */
    public Map<String, String> getOAuthParameters() {
        return oauthParameters;
    }
    /**
     * 获取Http Headers
     *
     * @return
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 获取OAuth参数和查询参数的合集
     *
     * @return Map<String, String>
     */
    public Map<String, String> getParams() {
        Map<String, String> params = Maps.newLinkedHashMap();
        params.putAll(this.oauthParameters);
        params.putAll(this.querystringParams);
        return params;
    }

    /**
     * 获取完整的URL
     *
     * @return the complete url.
     */
    public String getCompleteUrl() {
        return HttpUtil.buildUrl(this.url, this.oauthParameters);
    }
}
