package com.ty.cm.utils.oauth2.core;

import com.google.common.collect.Maps;
import com.ty.cm.utils.HttpUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;

import java.util.HashMap;
import java.util.Map;

import static com.ty.cm.utils.oauth2.core.OAuthConstants.ACCESS_TOKEN;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.AUTHORIZATION_CODE;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.CLIENT_ID;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.CLIENT_SECRET;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.CODE;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.GRANT_TYPE;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.REDIRECT_URI;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.REFRESH_TOKEN;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.RESPONSE_TYPE_CODE;

/**
 * OAuth Protocol, version 2.0 服务类
 *
 * @Author Tommy
 * @Date 2022/12/22
 */
@Slf4j
public class OAuth20Service {

    @Getter
    private final DefaultApi20 api;

    /**
     * 构造函数
     *
     * @param api
     */
    public OAuth20Service(DefaultApi20 api) {
        this.api = api;
    }

    /**
     * 获取Access Token
     *
     * @param code 授权码
     *
     * @return Map<String, String>
     */
    public Map<String, String> getAccessToken(String code) {
        return this.getAccessToken(code, api.getAccessTokenVerb());
    }

    /**
     * 获取Access Token
     *
     * @param code 授权码
     * @param verb 请求方式
     *
     * @return Map<String, String>
     */
    public Map<String, String> getAccessToken(String code, Verb verb) {
        final OAuthRequest request = new OAuthRequest(api.getAccessTokenEndpoint(), verb);
        request.addOAuthParameter(CODE, code);
        request.addOAuthParameter(GRANT_TYPE, AUTHORIZATION_CODE);
        request.addOAuthParameter(REDIRECT_URI, api.getRedirectURI());
        return this.sendTokenRequestSync(request);
    }

    /**
     * 以同步方式发送Token Request请求
     *
     * @param request
     * @return Map<String, String>
     */
    protected Map<String, String> sendTokenRequestSync(OAuthRequest request) {
        if (null != request) {
            request.addOAuthParameter(CLIENT_ID, api.getClientID());
            request.addOAuthParameter(CLIENT_SECRET, api.getClientSecret());
        }
        return this.execute(request);
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken Refresh令牌
     * @return Map<String, String>
     */
    public Map<String, String> refreshToken(String refreshToken) {
        final OAuthRequest request = new OAuthRequest(api.getAccessTokenEndpoint(), Verb.POST);
        request.addOAuthParameter(GRANT_TYPE, REFRESH_TOKEN);
        request.addOAuthParameter(CLIENT_ID, api.getClientID());
        request.addOAuthParameter(CLIENT_SECRET, api.getClientSecret());
        request.addOAuthParameter(REFRESH_TOKEN, refreshToken);
        return this.execute(request);
    }

    /**
     * 注销会话
     *
     * @param refreshToken Refresh令牌
     */
    public void logout(String refreshToken) {
        final OAuthRequest request = new OAuthRequest(api.getLogoutEndpoint(), Verb.POST);
        request.addOAuthParameter(CLIENT_ID, api.getClientID());
        request.addOAuthParameter(CLIENT_SECRET, api.getClientSecret());
        request.addOAuthParameter(REFRESH_TOKEN, refreshToken);
        this.execute(request);
    }

    /**
     * Sign OAuthRequest
     *
     * @param accessToken
     * @param request
     */
    public void signRequest(String accessToken, OAuthRequest request) {
        request.addOAuthParameter(ACCESS_TOKEN, accessToken);
    }

    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     *
     * @return the URL where you should redirect your users
     */
    public String getAuthorizationUrl() {
        return this.getAuthorizationUrl(null);
    }

    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     *
     * @param state
     * @return the URL where you should redirect your users
     */
    public String getAuthorizationUrl(String state) {
        return this.getAuthorizationUrl(state, new HashMap<>());
    }

    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     *
     * @param state
     * @param scope
     * @return the URL where you should redirect your users
     */
    public String getAuthorizationUrl(String state, String scope) {
        return this.getAuthorizationUrl(state, scope, null);
    }

    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     *
     * @param state
     * @param additionalParams
     *                         any additional GET params to add to the URL
     * @return the URL where you should redirect your users
     */
    public String getAuthorizationUrl(String state, Map<String, String> additionalParams) {
        return this.getAuthorizationUrl(state, null, additionalParams);
    }

    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     *
     * @param state
     * @param scope
     * @param additionalParams
     *                         any additional GET params to add to the URL
     * @return the URL where you should redirect your users
     */
    public String getAuthorizationUrl(String state, String scope, Map<String, String> additionalParams) {
        return api.getAuthorizationUrl(RESPONSE_TYPE_CODE, api.getClientID(), api.getRedirectURI(), scope, state, additionalParams);
    }

    /**
     * 执行OAuth Request请求
     *
     * @param request
     * @return Map<String, String>
     */
    public Map<String, String> execute(OAuthRequest request) {
        Map<String, String> dataMap = Maps.newHashMap();
        if (null != request) {
            Headers headers = HttpUtil.headersOf(request.getHeaders());
            try {
                switch (request.verb) {
                    case GET:
                        dataMap = HttpUtil.getForJson(request.getCompleteUrl(), request.getQuerystringParams(), headers, HashMap.class);
                        break;
                    case POST:
                        dataMap = HttpUtil.postForJson(request.url, request.getParams(), headers, HashMap.class);
                        break;
                    default:
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        return dataMap;
    }
}
