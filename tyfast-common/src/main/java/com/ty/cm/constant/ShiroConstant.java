package com.ty.cm.constant;

/**
 * Shiro常量
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
public interface ShiroConstant {

    /** 默认"验证码"参数名称 */
    public static final String DEFAULT_CAPTCHA_PARAM = "captcha";

    /** 默认"验证码ID" Key */
    public static final String DEFAULT_CAPTCHA_ID_PARAM = "captchaId";

    /** "验证码" Key **/
    public static final String CACHE_CAPTCHA = "shiro_cache_captcha";

    /** "用户权限集" Key **/
    public static final String CACHE_USER_PERMISSION = "shiro_cache_user_permission";

    /** 基于"URL"的校验策略 **/
    public static final String STRATEGY_URL = "url";

    /** 策略：登录成功即授权，等价于 Shiro原生的 authc **/
    public static final String STRATEGY_AUTHC = "authc";

    /** "菜单ID集合" Key **/
    public static final String USER_MENU_IDS = "user_menu_ids";

    /** "权限集URL" Key **/
    public static final String USER_PERMISSION = "user_permission";

    /** Shiro Session 属性名称 **/
    public static final String ATTRIBUTES = "attributes";

    /** Shiro RealmName 属性名称 **/
    public static final String REALM_NAME = "realmName";

    /** Token ID **/
    public static final String TOKEN_ID = "tysid";

    /** 用户Key前缀 **/
    public static final String USER_PREFIX = "tyuser";

    /** 会话超时时间：6个小时(单位：秒) **/
    public static final int SESSION_TIMEOUT = 6 * 60 * 60;

    /** 匿名会话超时时间：30分钟(单位：秒) **/
    public static final int SESSION_TIMEOUT_ANON = 1800;

    /** 冻结标识 **/
    public static final Integer ACCOUNT_FROZEN = 1;
}
