package com.ty.cm.constant.enums;

/**
 * 缓存Key
 *
 * @Author Tommy
 * @Date 2022/11/2
 */
public enum CacheKey {

    /** Redis 数据字典列表Key **/
    DICT_LIST("dict_list");

    // 内部属性Key
    private final String key;

    CacheKey(String key) {
        this.key = key;
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
}
