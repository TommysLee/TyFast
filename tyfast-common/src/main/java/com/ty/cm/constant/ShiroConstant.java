package com.ty.cm.constant;

/**
 * Shiro常量
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
public interface ShiroConstant {

    /** 默认"验证码"参数名称 */
    String DEFAULT_CAPTCHA_PARAM = "captcha";

    /** 默认"验证码ID" Key */
    String DEFAULT_CAPTCHA_ID_PARAM = "captchaId";

    /** "验证码" Key **/
    String CACHE_CAPTCHA = "shiro_cache_captcha";

    /** 基于"URL"的校验策略 **/
    String STRATEGY_URL = "url";

    /** Shiro策略：登录成功即授权，等价于 Shiro原生的 authc **/
    String STRATEGY_AUTHC = "authc";

    /** Shiro Session 属性名称 **/
    String ATTRIBUTES = "attributes";

    /** Shiro RealmName 属性名称 **/
    String REALM_NAME = "realmName";

    /** Token ID **/
    String TOKEN_ID = "tysid";

    /** 角色标识 **/
    String ROLE = "ROLE";

    /** 会话超时时间：8个小时(单位：秒) **/
    int SESSION_TIMEOUT = 8 * 60 * 60;

    /** 匿名会话超时时间：30分钟(单位：秒) **/
    int SESSION_TIMEOUT_ANON = 1800;

    /** 冻结标识 **/
    Integer ACCOUNT_FROZEN = 1;
}
