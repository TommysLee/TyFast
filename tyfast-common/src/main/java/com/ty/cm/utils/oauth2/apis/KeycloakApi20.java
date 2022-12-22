package com.ty.cm.utils.oauth2.apis;

import com.ty.cm.utils.oauth2.core.DefaultApi20;

/**
 * Keycloak OAuth2.0/OpenID Connect 对接API配置类
 *
 * @Author Tommy
 * @Date 2022/12/22
 */
public class KeycloakApi20 extends DefaultApi20 {

    public static final String BASE_URL = "http://192.168.9.132:18080";

    public KeycloakApi20() {
        this("ukey", "gi8TZvNCtwnrsDu1PjdzXEfl5BpNNB5W", "");
    }

    public KeycloakApi20(String redirectURI) {
        this();
        this.redirectURI = redirectURI;
    }

    public KeycloakApi20(final String clientID, final String clientSecret, String redirectURI) {
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
        return BASE_URL + "/realms/keycloak/protocol/openid-connect/auth";
    }

    /**
     * 获取Access Token Endpoint
     *
     * @return String - Access Token Endpoint
     */
    @Override
    public String getAccessTokenEndpoint() {
        return BASE_URL + "/realms/keycloak/protocol/openid-connect/token";
    }

    /**
     * 注销会话Endpoint
     *
     * @return String
     */
    @Override
    public String getLogoutEndpoint() {
        return BASE_URL + "/realms/keycloak/protocol/openid-connect/logout";
    }
}
