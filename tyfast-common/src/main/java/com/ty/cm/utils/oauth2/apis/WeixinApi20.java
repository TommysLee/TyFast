package com.ty.cm.utils.oauth2.apis;

import com.ty.cm.utils.oauth2.core.DefaultApi20;

/**
 * 微信网站应用OAuth2.0登录API
 *
 * @Author Tommy
 * @Date 2023/4/13
 */
public class WeixinApi20 extends DefaultApi20 {

    public WeixinApi20(final String clientID, final String clientSecret, String redirectURI) {
        super(clientID, clientSecret);
        this.redirectURI = redirectURI;
    }

    /**
     * 获取授权Endpoint
     *
     * @return String
     */
    @Override
    public String getAuthorizationEndpoint() {
        return "https://open.weixin.qq.com/connect/qrconnect?scope=snsapi_login";
    }

    /**
     * 获取Access Token Endpoint
     *
     * @return String - Access Token Endpoint
     */
    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.weixin.qq.com/sns/oauth2/access_token";
    }

    /**
     * 注销会话Endpoint
     *
     * @return String
     */
    @Override
    public String getLogoutEndpoint() {
        return null;
    }

    /**
     * Endpoint Tail
     *
     * @return String
     */
    @Override
    public String tail() {
        return "#wechat_redirect";
    }
}
