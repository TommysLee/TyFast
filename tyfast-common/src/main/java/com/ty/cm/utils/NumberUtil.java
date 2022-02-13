package com.ty.cm.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
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
     * @param str
     *            输入字符串
     * @return 整数
     */
    public static int convertToInt(String str) {
        return Integer.parseInt(str.replaceAll(",", ""));
    }

    /**
     * 转换成Long
     *
     * @param str
     * @return
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
     * @param str
     *            输入字符串
     * @return 大数字
     */
    public static BigDecimal convertToBigDecimal(String str) {
        return new BigDecimal(str.replaceAll(",", ""));
    }

    /**
     * 若对象为Null，则返回默认值
     *
     * @param t
     * @param defaultVal
     * @return <T> T
     */
    public static <T extends Number> T defaultIfNull(T t, T defaultVal) {
        return null != t ? t : defaultVal;
    }

    /**
     * 保留2位小数
     *
     * @param value 输入浮点数
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
     * @param number
     *            输入浮点数
     * @param format
     *            格式
     * @return 格式化后的字符串
     */
    public static String formatNumer(Double number, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(number);
    }

    /**
     * 若数字小于0，则返回默认值
     *
     * @param val
     * @param defaultVal
     * @return int
     */
    public static int defaultIfLteZero(int val, int defaultVal) {
        return val > 0 ? val : defaultVal;
    }

    /**
     * 数字位数不足自动补零
     *
     * @param number
     *            ----> 数字
     * @param digit
     *            ----> 位数
     * @return
     */
    public static String fillZero(Integer number, int digit) {

        return fillZero(new Long(number), digit);
    }

    /**
     * 数字位数不足自动补零
     *
     * @param numberStr
     *            ----> 数字字符串
     * @param digit
     *            ----> 位数
     * @return
     */
    public static String fillZero(String numberStr, int digit) {

        return fillZero(new Long(numberStr), digit);
    }

    /**
     * 数字位数不足自动补零
     *
     * @param number
     *            ----> 数字
     * @param digit
     *            ----> 位数
     * @return
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
     * @param includeNumberString
     *            ----> 含数字的字符串
     * @return double
     */
    public static Double pickNum(Object includeNumberString, boolean isFirst) {

        Double val = null;
        if (null != includeNumberString && includeNumberString instanceof String) {
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
     * @param min ----> 随机数范围：最小值
     * @param max ----> 随机数范围：最大值
     * @return int
     */
    public static int random(int min, int max) {

        return RandomUtils.nextInt(min, max + 1);
    }

    /**
     * 获取随机数
     *
     * @param min  ----> 随机数范围：最小值
     * @param max  ----> 随机数范围：最大值
     * @param size ----> 随机数个数
     * @return int
     */
    public static List<Integer> random(int min, int max, int size) {

        final List<Integer> nums = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
            nums.add(random(min, max));
        }
        return nums;
    }

    /**
     * 将数字字符串数组转换为整型数组
     *
     * @param numArray
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
}
