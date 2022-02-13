package com.ty.cm.utils.uusn;

import com.ty.cm.utils.DateUtils;
import com.ty.cm.utils.IpUtils;
import com.ty.cm.utils.NumberUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * UUSN简单实现方式
 *
 * 说明：
 * 值组成：通过日期时间+递增序列号+所在服务器IP尾号
 *
 * UUSN总位数：20位
 *
 * 注意事项：
 * 在集群中，请确保每台应用服务器的IP尾号唯一
 *
 * 性能：
 * TPS：百万级
 *
 * UUSN有效期至：2099-12-31
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
@Slf4j
public class SimpleUUSN implements UUSN {

    // 锁：用于控制同一时间内只生成一个UUSN
    private static final Semaphore LOCK = new Semaphore(1);

    // 同一毫秒内多个UUSN的最新序号
    private int sequence = 0;

    // 递增序号最大值
    private int maxSequence = 999;

    // 递增数的位数
    private static int digit = 3;

    // 记录上次产生值时的时间毫秒数
    private long lastTimestamp = -1L;

    /**
     * 生成UUSN序列号
     *
     * @return String
     */
    @Override
    public String nextUUSN() {

        final StringBuilder sn = new StringBuilder();
        try {
            LOCK.acquire();

            // 获取当前时间戳，并判断服务器时间是否有波动（回拨情况）
            long timestamp = timeGen();
            if (timestamp < lastTimestamp) {
                log.warn("服务器时间存在波动，可能影响UUSN的准确性，请及时检查！当前时间戳：" + timestamp + ", 上一次时间戳：" + lastTimestamp);
            }

            // 同一毫秒内的生成多个UUSN
            // 将sequence序号递增1
            if (timestamp == lastTimestamp) {
                sequence++;

                // 当某一毫秒的时间，产生的UUSN个数 超过 maxSequence，系统进入等待，直到下一毫秒，系统继续产生UUSN
                if (sequence > maxSequence) {
                    timestamp = tilNextMillis(lastTimestamp);
                    sequence = 0;
                }
            } else {
                sequence = 0;
            }

            // 记录一下最近一次生成UUSN的时间戳
            lastTimestamp = timestamp;

            // 组装UUSN
            sn.append(getDatetime(timestamp)); // 日期时间（14位）
            sn.append(NumberUtil.fillZero(sequence, digit)); // 递增序列号（3位）
            sn.append(IpUtils.getLocalHostIPTail()); // IP尾号（3位）
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            LOCK.release();
        }
        return sn.toString();
    }

    /**
     * 获取当前时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 当某一毫秒的时间，产生的UUSN个数 超过 maxSequence，系统进入等待，直到下一毫秒
     */
    private long tilNextMillis(long lastTimestamp) {

        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取日期时间：格式=yyMMdd+当天的日毫秒数（8位）
     */
    private String getDatetime(long timestamp) {

        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 当天实时的毫秒数
        int millis = (calendar.get(Calendar.HOUR) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND)) * 1000 + calendar.get(Calendar.MILLISECOND);

        // 当天日期
        String dateString = DateUtils.dateToString(date, "yyMMdd");
        return dateString + NumberUtil.fillZero(millis, 8);
    }
}
