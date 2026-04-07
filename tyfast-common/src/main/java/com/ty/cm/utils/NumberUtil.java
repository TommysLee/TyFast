package com.ty.cm.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字工具类
 *
 * @Author Tommy
 * @Date 2022/1/11
 */
public class NumberUtil {

    /**
     * 转换成整型
     *
     * @param str 数字字符串
     * @return int
     */
    public static int convertToInt(String str) {
        return Integer.parseInt(str.replaceAll(",", ""));
    }

    /**
     * 转换成Long
     *
     * @param str 数字字符串
     * @return Long
     */
    public static Long convertToLong(String str) {
        if (NumberUtils.isCreatable(str)) {
            return Long.parseLong(str);
        }
        return null;
    }

    /**
     * 转换成BigDecimal
     *
     * @param str 数字字符串
     * @return BigDecimal
     */
    public static BigDecimal convertToBigDecimal(String str) {
        return new BigDecimal(str.replaceAll(",", ""));
    }

    /**
     * 若对象为Null，则返回默认值
     *
     * @param t Number对象
     * @param defaultVal 默认值
     * @return <T> T
     */
    public static <T extends Number> T defaultIfNull(T t, T defaultVal) {
        return null != t ? t : defaultVal;
    }

    /**
     * 保留2位小数
     *
     * @param value 浮点数
     * @return Double
     */
    public static Double toFixed(Double value) {
        if (null != value) {
            DecimalFormat format = new DecimalFormat("0.##");
            String val = format.format(value);
            value = Double.parseDouble(val);
        }
        return value;
    }

    /**
     * 格式化浮点数
     *
     * @param number 浮点数
     * @param format 格式
     * @return 格式化后的字符串
     */
    public static String formatNumer(Double number, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(number);
    }

    /**
     * 若数字小于0，则返回默认值
     *
     * @param val        int值
     * @param defaultVal 默认值
     * @return int
     */
    public static int defaultIfLteZero(int val, int defaultVal) {
        return val > 0 ? val : defaultVal;
    }

    /**
     * 数字位数不足自动补零
     *
     * @param number Integer数字
     * @param digit  需要的位数
     * @return String
     */
    public static String fillZero(Integer number, int digit) {
        return fillZero(Long.valueOf(number), digit);
    }

    /**
     * 数字位数不足自动补零
     *
     * @param numberStr 数字字符串
     * @param digit     需要的位数
     * @return String
     */
    public static String fillZero(String numberStr, int digit) {
        return fillZero(Long.valueOf(numberStr), digit);
    }

    /**
     * 数字位数不足自动补零
     *
     * @param number Long数字
     * @param digit  需要的位数
     * @return String
     */
    public static String fillZero(Long number, int digit) {
        final StringBuilder patternBuilder = new StringBuilder("#");
        for (int i = 0; i < digit; i++) {
            patternBuilder.append("0");
        }

        final DecimalFormat df = new DecimalFormat(patternBuilder.toString());
        return df.format(number);
    }

    /**
     * 提取字符串中的数字
     *
     * @param includeNumberString 数字字符串
     * @return Double
     */
    public static Double pickNum(Object includeNumberString, boolean isFirst) {
        Double val = null;
        if (includeNumberString instanceof String) {
            final Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
            final Matcher matcher = pattern.matcher((String) includeNumberString);
            while (matcher.find()) {
                if (isFirst) {
                    val = Double.valueOf(matcher.group());
                    break;
                } else {
                    Double tmp = Double.valueOf(matcher.group());
                    val = tmp == null ? null : ((val == null ? 0d : val) + tmp);
                }
            }
        }
        return val;
    }

    /**
     * 获取随机数
     *
     * @param min 随机数：最小值
     * @param max 随机数：最大值
     * @return int
     */
    public static int random(int min, int max) {
        return RandomUtils.nextInt(min, max + 1);
    }

    /**
     * 获取随机数
     *
     * @param min 随机数：最小值
     * @param max 随机数：最大值
     * @param size 需要的随机数个数
     * @return int
     */
    public static List<Integer> random(int min, int max, int size) {
        final List<Integer> nums = Lists.newArrayListWithCapacity(size);
        for (int i = 0; i < size; i++) {
            nums.add(random(min, max));
        }
        return nums;
    }

    /**
     * 将数字字符串数组转换为整型数组
     *
     * @param numArray 数字字符串数组
     * @return int[]
     */
    public static int[] toIntArray(String[] numArray) {
        int[] nums = new int[0];
        if (null != numArray) {
            nums = new int[numArray.length];
            int i = 0;
            for (String n : numArray) {
                nums[i++] = NumberUtils.toInt(n);
            }
        }
        return nums;
    }

    /**
     * 判断两个数字的值是否相等
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return boolean
     */
    public static boolean equals(final Number num1, final Number num2) {
        if (num1 == num2) {
            return true;
        }
        if (num1 == null || num2 == null) {
            return false;
        }

        if (num1 instanceof Float) {
            return num1.floatValue() == num2.floatValue();
        } else if (num1 instanceof Double) {
            return num1.doubleValue() == num2.doubleValue();
        } else if (num1 instanceof Integer) {
            return num1.intValue() == num2.intValue();
        }
        return num1.longValue() == num2.longValue();
    }

    /**
     * 判断对象是否为Number的实例
     *
     * @param obj 对象
     * @return boolean
     */
    public static boolean isNumberInstance(Object obj) {
        return obj instanceof Number;
    }

    /**
     * 16进制转10进制
     *
     * @param hex 十六进制值
     * @return Integer
     */
    public static Integer hex2Int(String hex) {
        return StringUtils.isNotBlank(hex) ? NumberUtils.createNumber("0x" + hex).intValue() : null;
    }

    /**
     * 十进制转二进制
     *
     * @param n 整数
     * @return String
     */
    public static String toBin(Integer n) {
        String bin = null;
        if (n != null) {
            bin = Integer.toBinaryString(n);
            // 二进制的长度一般为4的倍数，不足则高位补零
            int pad = 4 - bin.length() % 4;
            for (int i = 0; i < pad; i++) {
                bin = "0".concat(bin);
            }
        }
        return bin;
    }

    /**
     * 十进制转二进制，且长度不低于minLen
     *
     * @param n      整数
     * @param minLen 最低长度要求
     * @return String
     */
    public static String toBin(Integer n, int minLen) {
        String bin = toBin(n);
        if (null != bin) {
            int pad = minLen - bin.length();
            for (int i = 0; i < pad; i++) { // 长度不够，高位补零
                bin = "0".concat(bin);
            }
        }
        return bin;
    }

    /**
     * 二进制转十进制
     *
     * @param bin 二进制字符串
     * @return Integer
     */
    public static Integer toDec(String bin) {
        Integer n = null;
        if (StringUtils.isNotBlank(bin)) {
            n = Integer.parseInt(bin, 2);
        }
        return n;
    }

    /**
     * 将数字字符串转换为数字
     *
     * @param numStr 数字字符串
     * @return Number
     */
    public static Number parseNumber(String numStr) {
        return NumberUtils.isCreatable(numStr)? NumberUtils.createNumber(numStr) : null;
    }

    /**
     * 将数字字符串转换为整型
     *
     * @param numStr 数字字符串
     * @return Integer
     */
    public static Integer parseInt(String numStr) {
        Number num = parseNumber(numStr);
        return null != num? num.intValue() : null;
    }
}
