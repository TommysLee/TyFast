package com.ty.cm.utils.document;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.ty.cm.constant.Ty.HLINE;

/**
 * JXLS扩展的自定义函数
 *
 * @Author Tommy
 * @Date 2022/12/7
 */
public class JxlsUtil {

    /**
     * 初始化整型List并赋值
     *
     * @param size 集合大小
     * @return List<Integer>
     */
    public List<Integer> intList(int size) {
        List<Integer> list = Lists.newArrayListWithCapacity(size);
        for (int i = 0; i < size;) {
            list.add(++i);
        }
        return list;
    }

    /**
     * 如果字符串为空或null则返回默认值
     *
     * @param text
     * @param defaultVal
     * @return String
     */
    public String defaultIfBlank(String text, Integer defaultVal) {
        return StringUtils.defaultIfBlank(text, null != defaultVal ? defaultVal.toString() : StringUtils.EMPTY);
    }

    /**
     * 如果对象为空则以横线替换
     *
     * @param text
     * @param defaultVal
     * @return String
     */
    public Object defaultHIfBlank(Object obj) {
        return null == obj? HLINE : obj;
    }
}


