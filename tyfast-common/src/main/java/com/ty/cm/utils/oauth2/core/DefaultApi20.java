package com.ty.cm.utils.oauth2.core;

import com.ty.cm.utils.HttpUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.ty.cm.utils.oauth2.core.OAuthConstants.CLIENT_ID;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.NONCE;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.REDIRECT_URI;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.RESPONSE_TYPE;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.RESPONSE_TYPE_CODE;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.SCOPE;
import static com.ty.cm.utils.oauth2.core.OAuthConstants.STATE;

/**
 * OAuth Protocol, version 2.0 默认配置类
 *
 * @Author Tommy
 * @Date 2022/12/22
 */
@Data
public abstract class DefaultApi20 {

    /** 客户端ID **/
    protected String clientID;

    /** 客户端密钥 **/
    protected String clientSecret;

    /** 重定向URI **/
    protected String redirectURI;

    /**
     * 默认构造函数
     */
    public DefaultApi20() {
    }

    /**
     * 构造函数
     *
     * @param clientID      客户端ID
     * @param clientSecret  客户端密钥
     */
    public DefaultApi20(final String clientID, final String clientSecret) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    /**
     * 获取重定向URI
     *
     * @return String
     */
    public String getRedirectURI() {
        return redirectURI;
    }

    /**
     * 获取授权Endpoint
     *
     * @return String
     */
    public abstract String getAuthorizationEndpoint();

    /**
     * Access Token的请求类型
     *
     * @return Verb
     */
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    /**
     * 获取Access Token Endpoint
     *
     * @return String - Access Token Endpoint
     */
    public abstract String getAccessTokenEndpoint();

    /**
     * 注销会话Endpoint
     *
     * @return String
     */
    public abstract String getLogoutEndpoint();

    /**
     * 获取默认首页地址
     *
     * @return String
     */
    public String getHomeURL() {
        return "";
    }

    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     *
     * @param scope One or more scope values indicating which parts of the user's account you wish to access
     * @param state A random string generated by your application, which you'll verify later
     * @return String - Like a "Log In" link
     */
    public String getAuthorizationUrl(String scope, String state) {
        return this.getAuthorizationUrl(RESPONSE_TYPE_CODE, getClientID(), getRedirectURI(), scope, state, null);
    }

    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     *
     * @param responseType
     * @param clientID
     * @param redirectURI
     * @param scope
     * @param state
     * @param additionalParams any additional GET params to add to the URL
     * @return the URL where you should redirect your users
     */
    public String getAuthorizationUrl(String responseType, String clientID, String redirectURI, String scope, String state, Map<String, String> additionalParams) {

        additionalParams = null != additionalParams ? additionalParams : new HashMap<>();
        additionalParams.put(RESPONSE_TYPE, responseType);
        additionalParams.put(CLIENT_ID, clientID);

        if (StringUtils.isNotBlank(redirectURI)) {
            additionalParams.put(REDIRECT_URI, redirectURI);
        }

        if (StringUtils.isNotBlank(scope)) {
            additionalParams.put(SCOPE, scope);
        }

        if (StringUtils.isNotBlank(state)) {
            additionalParams.put(STATE,  state);
            additionalParams.put(NONCE,  state); // 兼容OIDC
        }
        return HttpUtil.buildUrl(getAuthorizationEndpoint(), additionalParams);
    }
}