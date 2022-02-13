package com.ty.cm.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期时间工具类
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
public final class DateUtils {

    public static final String DATESTYLE_EX_YEAR_MONTH = "yyyy-MM-dd";

    public static final String DATESTYLE_DETAIL_HH_MM_SS = "yyyy/MM/dd HH:mm:ss";

    /** 日期正则 **/
    public static final String DATE_REGEX = "(\\d{4}[-/]?\\d{1,2}[-/]?\\d{1,2})(\\s(\\d{1,2}\\:\\d{1,2}(\\:\\d{1,2})?))?";

    private DateUtils() {

    }

    /**
     * 获取日期格式化对象
     *
     * @param pattern
     * @return SimpleDateFormat
     */
    private static SimpleDateFormat getDateParser(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 毫秒数转化为时间字符串
     *
     * @param time
     *            毫秒数
     * @param pattern
     *            格式
     * @return 时间字符串
     */
    public static String longToDate(Long time, String pattern) {
        if (time != null)
            return getDateParser(pattern).format(new Date(time));
        else
            return "";
    }

    /**
     * 毫秒数转化为时间字符串
     *
     * @param time
     *            毫秒数
     * @return 时间字符串
     */
    public static String longToDate(Long time) {
        return getDateParser("yyyy-MM-dd").format(new Date(time));
    }

    /**
     * 毫秒数转化为时间字符串
     *
     * @param time
     *            毫秒数
     * @return 时间字符串
     */
    public static String longToDate2(Long time) {
        return getDateParser("MM-dd HH:mm").format(new Date(time));
    }

    /**
     * 毫秒数转化为时间字符串
     *
     * @param time
     *            毫秒数
     * @return 时间字符串
     */
    public static String longToDateAll(Long time) {
        return getDateParser("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }

    /**
     * 毫秒数转化为时间字符串
     *
     * @param time
     *            毫秒数
     * @return 时间字符串
     */
    public static String longToDateAllNew(Long time) {
        return getDateParser("yyyyMMddHHmmss").format(new Date(time));
    }

    /**
     * 获取现在时间
     *
     * @return 现在时间
     */
    public static String now() {
        return getDateParser("yyyy-MM-dd HH:mm:ss").format(
                new Date(System.currentTimeMillis()));
    }

    /***
     * String型日期转为long型
     *
     * @param source
     *            时间字符串
     * @return 毫秒数
     */
    public static long dateToLong(String source) {
        if (StringUtils.isNotBlank(source)) {
            try {
                return getDateParser("yyyy/MM/dd").parse(source).getTime();
            } catch (ParseException e) {
                try {
                    return getDateParser("yyyy-MM-dd").parse(source).getTime();
                } catch (ParseException e1) {
                    return -1;
                }
            }
        }
        return -1;
    }

    /***
     * String型日期转为long型
     *
     * @param source
     *            时间字符串
     * @return 毫秒数
     */
    public static long dateToLong(String source, String pattern) {
        if (StringUtils.isNotBlank(source)) {
            try {
                return getDateParser(pattern).parse(source).getTime();
            } catch (ParseException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * 时间字符串增加一天
     *
     * @param source
     *            时间字符串
     * @return 增加一天后的毫秒数
     */
    public static long dateAddOneDayAndToLong(String source) {
        try {
            Date date = getDateParser("yyyy/MM/dd").parse(source);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            cd.add(Calendar.DAY_OF_MONTH, 1);
            return cd.getTime().getTime();
        } catch (ParseException e) {
            try {
                Date date = getDateParser("yyyy-MM-dd").parse(source);
                Calendar cd = Calendar.getInstance();
                cd.setTime(date);
                cd.add(Calendar.DAY_OF_MONTH, 1);
                return cd.getTime().getTime();
            } catch (ParseException e1) {
                return -1;
            }
        }
    }

    /**
     * 时间字符串增加一天
     *
     * @param source
     *            时间字符串
     * @return 增加一天后的毫秒数
     */
    public static long nextDate(String source) {
        try {
            return getDateParser("yyyy/MM/dd").parse(source).getTime() + 24
                    * 60 * 60 * 1000;
        } catch (ParseException e) {
            try {
                return getDateParser("yyyy-MM-dd").parse(source).getTime() + 24
                        * 60 * 60 * 1000;
            } catch (ParseException e1) {
                return -1;
            }
        }

    }

    /**
     * 毫秒数减去一天后的时间字符串
     *
     * @param time
     *            毫秒数
     * @return 毫秒数减去一天后的时间字符串
     */
    public static String longToFrontDate(Long time) {
        return getDateParser("yyyy-MM-dd").format(
                new Date(time - 24 * 60 * 60 * 1000));
    }

    /***
     * String型日期转为long型
     *
     * @param source
     *            String型日期
     * @return long 日期
     * @throws ParseException
     */
    public static long dateAllToLong(String source) {
        try {
            return getDateParser("yyyy/MM/dd HH:mm:ss").parse(source).getTime();
        } catch (ParseException e) {
            try {
                return getDateParser("yyyy-MM-dd HH:mm:ss").parse(source)
                        .getTime();
            } catch (ParseException e1) {
                return -1;
            }
        }

    }

    /**
     * 获取年月日
     *
     * @return 年月日
     */
    public static long genYMD() {
        return Long.valueOf(getDateParser("yyyyMMdd").format(new Date()));
    }

    /**
     * 获取日期字符
     *
     * @param date
     *            日期
     * @param format
     *            格式 如:yyy-MM-dd
     * @return 日期字符
     */
    public static String getDate(Date date, String format) {
        return getDateParser(format).format(date);
    }

    /**
     * 获取日期对象
     *
     * @param dataString
     *            时间字符串
     * @param format
     *            格式
     * @return 日期对象
     * @throws ParseException
     *             ParseException
     */
    public static Date toDate(String dataString, String format)
            throws ParseException {
        return getDateParser(format).parse(dataString);
    }

    /**
     * 获取系统时间
     *
     * @return 当前日期
     */
    public static String time() {
        return DateUtils.getDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static String getCurrentDate() {
        return DateUtils.getDate(new Date(), "yy/MM/dd");
    }

    /**
     * 获得当前日期
     *
     * @return 当前日期
     */
    public static long getNowDate() {
        return DateUtils.dateToLong(getDateParser("yyyy-MM-dd").format(
                new Date()));
    }

    /**
     * 获得指定日期的毫秒数
     *
     * @param curDate
     *            指定日期
     * @return 指定日期的毫秒数
     */
    public static long getNowDate(Date curDate) {
        return DateUtils
                .dateToLong(getDateParser("yyyy-MM-dd").format(curDate));
    }

    /**
     * 获取当前时间加上任意天数后的日期
     *
     * @param dayNum
     *            天数
     * @return 当前时间加上任意天数后的日期
     */
    public static String getNewDateByAdd(int dayNum) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, dayNum);
        return (getDateParser("yyyy-MM-dd")).format(cal.getTime());
    }

    /**
     * 获取当前时间加上任意天数后的日期
     *
     * @param dayNum
     *            天数
     * @param format
     *            格式
     * @return 当前时间加上任意天数后的日期
     */
    public static String getNewDateByAdd(int dayNum, String format) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, dayNum);
        return (getDateParser(format)).format(cal.getTime());
    }

    /**
     * 获取指定时间加上任意时间后的日期
     *
     * @param date
     *            指定时间
     * @param num
     *            指定增加格式的增加量
     * @param timeType
     *            指定增加格式
     * @return 指定时间加上任意时间后的日期
     */
    public static Date getDateByAdd(Date date, int num, int timeType) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(timeType, num);
        return cal.getTime();
    }

    /**
     * 获取指定时间加上任意小时后的日期
     *
     * @param hour
     *            小时
     * @return 指定时间加上任意小时后的日期
     */
    public static String getNewDateByAddHour(int hour) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);
        return (getDateParser("yyyy-MM-dd HH:mm:ss")).format(cal.getTime());
    }

    /**
     * 获取当前月份最大天数
     *
     * @author lidong
     * @return 当前月份最大天数
     * @2011-12-26
     */
    public static int lastDayOfMonth() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return value;
    }

    /**
     * 获取当前年份及往前5年的年份集合
     *
     * @author lidong
     * @return 当前年份及往前5年的年份集合
     * @2012-01-11
     */
    public static List<Object> getYearList() {
        List<Object> yearList = new ArrayList<Object>();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        for (int i = 0; i < 5; i++) {
            Map<String, Object> yearMap = new HashMap<String, Object>();
            yearMap.put("id", currentYear);
            yearMap.put("text", currentYear + "年");
            if (i == 0) {
                yearMap.put("selected", true);
            }
            yearList.add(yearMap);
            currentYear = currentYear - 1;
        }
        return yearList;
    }

    /**
     * 将日期字符串转换为日期对象
     *
     * @param dateText
     *            ----> 日期字符串(yyyy-MM-dd 或 yyyy/MM/dd)
     * @return Date
     */
    public static Date parseDate(String dateText) {

        Date date = null;
        if (StringUtils.isNotBlank(dateText)) {
            dateText = dateText.replace("/", "-");
            final SimpleDateFormat dateFormat = getDateParser("yyyy-MM-dd");
            try {
                date = dateFormat.parse(dateText);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 将String型日期转换成日期对象
     *
     * @param 	dateText 日期
     * @param 	format	 日期格式
     * @return
     */
    public static Date parseDate(String dateText, String format) throws ParseException {

        Date date = null;
        if (StringUtils.isNotBlank(dateText) && StringUtils.isNotBlank(format)) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyPattern(format);
            try {
                date = simpleDateFormat.parse(dateText);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 将日期对象转换为日期字符串
     *
     * @param date
     * @return String
     */
    public static String toText(Date date) {

        String result = null;
        final SimpleDateFormat dateFormat = new SimpleDateFormat();
        final String datePattern = "yyyy-MM-dd";
        final String dateTimePattern = "yyyy-MM-dd HH:mm:ss";
        final String timePattern = "HHmmss";
        if (null != date) {
            dateFormat.applyPattern(timePattern);
            if (Integer.parseInt(dateFormat.format(date)) > 0) {
                dateFormat.applyPattern(dateTimePattern);
            } else {
                dateFormat.applyPattern(datePattern);
            }
            result = dateFormat.format(date);
        }
        return result;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate
     *            较小的时间
     * @param bdate
     *            较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate)
            throws ParseException {
        SimpleDateFormat sdf = getDateParser("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 获取当月的天数
     *
     * @return int
     */
    public static int getDayOfMonth() {
        LocalDate date = LocalDate.now();
        return date.getDayOfMonth();
    }

    /**
     * 判断字符串是否为日期格式
     *
     * @param dateText
     * @return boolean
     */
    public static boolean isDate(String dateText) {
        boolean flag = false;
        if (StringUtils.isNotBlank(dateText)) {
            flag = Pattern.compile(DATE_REGEX).matcher(dateText).matches();
        }
        return flag;
    }

    /**
     * 将日期字符串转换为日期对象
     *
     * @param dateText
     * @return Date
     */
    public static Date toDate(String dateText) {
        Date date = null;
        if (isDate(dateText)) {
            dateText = StringUtils.replace(dateText, "/", "-");
            final Matcher matcher = Pattern.compile(DATE_REGEX).matcher(dateText);
            matcher.matches();

            final StringBuilder pattern = new StringBuilder(
                    StringUtils.contains(dateText, "-") ? "yyyy-MM-dd" : "yyyyMMdd");
            pattern.append(StringUtils.isNotBlank(matcher.group(3)) ? "HH:mm" : StringUtils.EMPTY);
            pattern.append(StringUtils.isNotBlank(matcher.group(4)) ? ":ss" : StringUtils.EMPTY);
            try {
                date = new SimpleDateFormat(pattern.toString()).parse(dateText);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * Date类型转为String类型
     *
     * @param date 输入的日期
     * @param style 输入的日期格式
     * @return String
     */
    public static String dateToString(Date date, String style) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dFmt = new SimpleDateFormat(style);
        return dFmt.format(date);
    }
}
