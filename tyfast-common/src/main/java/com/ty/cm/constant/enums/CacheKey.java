package com.ty.cm.constant.enums;

/**
 * 缓存Key
 *
 * @Author Tommy
 * @Date 2022/11/2
 */
public enum CacheKey {

    /** Redis 数据字典列表Key **/
    DICT_LIST("DICT_LIST"),

    /** Redis OAuth2.0 State 微信前缀 **/
    WX_STATE("wx_state_"),

    /** Redis 机构列表Key **/
    ORGANIZATIONS("ORGANIZATIONS", -1);

    // 内部属性Key
    private final String key;

    // TTL(生存时间值，单位：秒)
    private final Integer ttl;

    CacheKey(String key) {
        this.key = key;
        this.ttl = null;
    }

    CacheKey(String key, Integer ttl) {
        this.key = key;
        this.ttl = ttl;
    }

    /**
     * 获取值
     *
     * @return String
     */
    public String value() {
        return this.key;
    }

    /**
     * 追加后返回值
     *
     * @param append
     * @return String
     */
    public String value(String append) {
        return this.key + append;
    }

    /**
     * 获取TTL
     *
     * @return Integer
     */
    public Integer ttl() {
        return this.ttl;
    }
}
