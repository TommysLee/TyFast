package com.ty.cm.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ty.cm.spring.SpringContextHolder;
import com.ty.cm.utils.crypto.MD5;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据工具类
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
@Slf4j
public class DataUtil {

    private static ObjectMapper mapper;

    static {
        try {
            mapper = SpringContextHolder.getBean(ObjectMapper.class);
            log.info("Jackson Init Based on SpringBoot");
        } catch (Exception e) {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            mapper.setTimeZone(TimeZone.getDefault());
            log.info("Jackson Init Based on Native");
        }
    }

    /**
     * 数据排序
     *
     * @param data
     *                    ----> 待排序的数据
     * @param reverse
     *                    ----> 是否逆序
     * @param numPriority
     *                    ----> 比较值含数字时，是否以数字优先比对
     * @param property
     *                    ----> 比较的属性名称
     * @return <T> List<T> 返回已排序的集合
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> sort(List<T> data, boolean reverse, final boolean numPriority, String... property) {

        if (null == data || 0 == data.size())
            return data;

        // 定义排序规则
        Comparator<T> comparator = new Comparator<T>() { // 在原有基础上，支持字符串数字排序
            @Override
            public int compare(T o1, T o2) {
                int order = ((Comparable<T>) o1).compareTo(o2);
                if (numPriority) {
                    Double d1 = NumberUtil.pickNum(o1, false);
                    Double d2 = NumberUtil.pickNum(o2, false);
                    if (null != d1 && null != d2) {
                        order = d1.compareTo(d2);
                    }
                }
                return order;
            }
        };
        comparator = ComparatorUtils.nullLowComparator(comparator); // 允许为null（通过comparator，创建新的comparator）
        if (reverse) { // 逆序
            comparator = ComparatorUtils.reversedComparator(comparator);
        }

        // 声明要排序的对象的属性，并指明所使用的排序规则，如果不指明，则用默认排序
        final List<Comparator<T>> sortFields = new ArrayList<Comparator<T>>();
        if (null != property && property.length > 0) {
            for (String p : property) {
                sortFields.add(new BeanComparator<T>(p, comparator)); // 按照property逆排序
            }
        } else if (String.class.isInstance(data.get(0))) {
            sortFields.add(comparator);
        } else {
            return data;
        }

        // 创建排序链
        ComparatorChain<T> sortChain = new ComparatorChain<T>(sortFields);

        // 开始真正的排序
        Collections.sort(data, sortChain);
        return data;
    }

    /**
     * 按指定排序规则进行数据排序
     *
     * @param data
     *                 ----> 待排序的数据
     * @param ogdata
     *                 ----> 指定的排列顺序
     * @param property
     *                 ----> 比较的属性名称
     * @return <T> List<T> 返回已排序的集合
     */
    public static <T> List<T> sort(List<T> data, final List<String> ogdata, String... property) {

        if (null == ogdata || ogdata.size() == 0) {
            return data;
        }
        // 定义排序规则
        Comparator<T> comparator = new Comparator<T>() { // 按指定的排序规则排序
            @Override
            public int compare(T o1, T o2) {
                int io1 = ogdata.indexOf(o1);
                int io2 = ogdata.indexOf(o2);
                return io1 - io2;
            }
        };
        comparator = ComparatorUtils.nullLowComparator(comparator); // 允许为null（通过comparator，创建新的comparator）

        // 声明要排序的对象的属性，并指明所使用的排序规则，如果不指明，则用默认排序
        final List<Comparator<T>> sortFields = new ArrayList<Comparator<T>>();
        if (null != property && property.length > 0) {
            for (String p : property) {
                sortFields.add(new BeanComparator<T>(p, comparator)); // 按照property排序
            }
        } else {
            return data;
        }

        // 创建排序链
        ComparatorChain<T> sortChain = new ComparatorChain<T>(sortFields);

        // 开始真正的排序
        Collections.sort(data, sortChain);
        return data;
    }

    /**
     * 数据排序
     *
     * @param data
     *                 ----> 待排序的集合
     * @param reverse
     *                 ----> 正序or倒序
     * @param property
     *                 ----> 排序属性
     * @return <T> List<T>
     */
    public static <T> List<T> sort(List<T> data, boolean reverse, String... property) {

        return sort(data, reverse, false, property);
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param jsonText
     *                 ----> JSON字符串
     * @return 返回Map
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> fromJSON(String jsonText) {

        return fromJSON(jsonText, Map.class);
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param jsonText
     *                 ----> JSON字符串
     * @param defaultVal
     *                 ----> 默认值
     * @return 返回Map
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> fromJSON(String jsonText, Map<String, T> defaultVal) {

        if (StringUtils.isNotBlank(jsonText)) {
            return fromJSON(jsonText, Map.class);
        }
        return defaultVal;
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param jsonText
     *                 ----> JSON字符串
     * @param defaultVal
     *                 ----> 默认值
     * @return 返回Map
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<Integer, T> fromOJSON(String jsonText, Map<Integer, T> defaultVal) {

        if (StringUtils.isNotBlank(jsonText)) {
            final Map<String, T> data = fromJSON(jsonText, LinkedHashMap.class);
            final Map<Integer, T> result = Maps.newLinkedHashMap();
            for (Map.Entry<String, T> entry : data.entrySet()) {
                result.put(Integer.parseInt(entry.getKey().trim()), entry.getValue());
            }
            return result;
        }
        return defaultVal;
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param jsonText
     *                 ----> JSON字符串
     * @param clazz
     *                 ----> Class<T>
     * @return <T> T
     */
    public static <T> T fromJSON(String jsonText, Class<T> clazz) {

        try {
            return mapper.readValue(jsonText, clazz);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将JSON数组转换为集合对象
     *
     * @param jsonArray JSON数组字符串
     * @param clazz
     * @param <T>
     * @return List<T>
     */
    public static <T> List<T> fromJSONArray(String jsonArray, Class<T> clazz) {

        try {
            CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return mapper.readValue(jsonArray, javaType);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param jsonObject
     *                   ----> 对象
     * @return String
     */
    public static String toJSON(Object jsonObject) {

        try {
            return mapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将Bean转换为Map
     *
     * @param bean
     * @return Map<String, Object>
     */
    public static Map<String, Object> toMap(Object bean) {

        Map<String, Object> beanMap = null;
        try {
            if (null != bean) {
                beanMap = mapper.convertValue(bean, HashMap.class);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return beanMap;
    }

    /**
     * 将Map转换为Bean
     *
     * @param beanMap
     * @param clazz
     * @return T
     */
    public static <T> T toBean(Map<String, Object> beanMap, Class<T> clazz) {
        T bean = null;
        try {
            if (null != beanMap && null != clazz) {
                bean = mapper.convertValue(beanMap, clazz);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return bean;
    }

    /**
     * char 转 byte[] 数组
     * @param c
     * @return
     */
    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    /**
     * 判断对象是否为Null
     *
     * @param obj
     * @return true / false
     */
    public static boolean isNull(Object obj) {
        return null == obj;
    }

    /**
     * 根据区间获取值
     * @param rateMap
     * @param total
     * @return
     * @Date 2020年12月24日 下午4:50:57
     * @Author DingWY
     *
     */
    public static Map<String, double[]> getValMapByRateMap(LinkedHashMap<String, int[]> rateMap, Double total) {
        Map<String, double[]> valMap = Maps.newLinkedHashMap();
        if (rateMap == null || total <= 0) {
            return valMap;
        }
        rateMap.entrySet().forEach(entry -> {
            double[] valArr = new double[2];
            for (int i = 0; i < 2; i++) {
                valArr[i] = entry.getValue()[i] * total / 100;
            }
            valMap.put(entry.getKey(), valArr);
        });
        return valMap;
    }

    /**
     * 数据MD5加密：将多个值按一定规则进行MD5加密
     *
     * @param datas 待加密的一组数据
     * @return String
     */
    public static String encrypt(String ... datas) {
        if (null != datas && datas.length > 0) {
            String content = StringUtils.EMPTY;
            for (String d : datas) {
                content += d;
            }
            return MD5.encrypt(content);
        }
        return null;
    }

    /**
     * 获取对象的所有Null属性
     *
     * @param source
     * @return String[]
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null || StringUtils.EMPTY.equals(srcValue))
                emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 复制非空属性到目标对象
     *
     * @param src 源对象
     * @param target 目标对象
     */
    public static void copyPropertiesIgnoreNull(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    /**
     * 返回入参的恰当的值
     *
     * @param val
     * @return Object
     */
    public static Object getFitValue(Object val) {
        if (null != val) {
            if (val instanceof Instant) {
                return val.toString();
            } else if (val instanceof Date) {
                Date dateVal = (Date) val;
                return dateVal.toInstant().toString();
            }
        }
        return val;
    }

    /**
     * 计算当前节点的所有后代节点的Level
     *
     * @param item  当前节点
     * @param itemLevel 当前节点的Level
     * @param childrenFunc  获取子节点的函数接口
     * @param setLevelConsumer  设置Level的函数接口
     * @return T 返回当前节点
     */
    public static <T> T calChildrenLevel(T item, int itemLevel, Function<T, List<T>> childrenFunc, BiConsumer<T, Integer> setLevelConsumer) {
        List<T> children = childrenFunc.apply(item);
        if (null != children && children.size() > 0) {
            children.stream().forEach(child -> {
                int level = itemLevel + 1;
                setLevelConsumer.accept(child, level);
                calChildrenLevel(child, level, childrenFunc, setLevelConsumer);
            });
        }
        return item;
    }

    /**
     * 通过数据列表构建任意树的数据
     *
     * @param dataList  列表数据
     * @param idFunc    获取PK的函数接口
     * @param parentIdFunc  获取父ID的函数接口
     * @param childrenFunc  获取子节点的函数接口
     * @param setLevelConsumer      设置Level的函数接口
     * @param setChildrenConsumer   设置子节点的函数接口
     * @param onlyRootNode  是否只返回根节点数据
     * @return List<T> 返回树结构数据集合
     */
    public static <T> List<T> wrapTreeData(List<T> dataList, Function<T, String> idFunc, Function<T, String> parentIdFunc, Function<T, List<T>> childrenFunc, BiConsumer<T, Integer> setLevelConsumer, BiConsumer<T, List<T>> setChildrenConsumer, boolean onlyRootNode) {

        Set<String> childIds = Sets.newHashSet();
        List<T> treeData = dataList.stream().map(currentItem -> { // 查找节点的所有子节点
            List<T> children = Lists.newArrayList();
            dataList.stream().forEach(item -> {
                if (idFunc.apply(currentItem).equals(parentIdFunc.apply(item))) {
                    children.add(item);
                    childIds.add(idFunc.apply(item));
                }
            });
            if (children.size() > 0) {
                setChildrenConsumer.accept(currentItem, children);
            }
            return currentItem;
        }).collect(Collectors.collectingAndThen(Collectors.toList(), resultList -> {
            return resultList.stream().filter(item -> { // 过滤所有子节点（因为子节点已被添加到 父节点的 children 属性）
                boolean flag = !childIds.contains(idFunc.apply(item));
                if (flag) {
                    setLevelConsumer.accept(item, 1); // 根节点 level=1
                    calChildrenLevel(item, 1, childrenFunc, setLevelConsumer); // 从每一个根节点开始，递归计算各子节点的层级
                }
                return flag;
            }).collect(Collectors.toList());
        }));

        if (onlyRootNode) { // 只返回根节点
            dataList.forEach(item -> setChildrenConsumer.accept(item, null));
        }
        return treeData;
    }
}
