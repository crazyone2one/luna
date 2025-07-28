package cn.master.luna.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Created by 11's papa on 2025/7/15
 */
public class DateUtils {

    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 返回yyyyMMddHHmmss格式的时间
     *
     * @param localDateTime 需要转换的时间
     * @return java.lang.String
     */
    public static String localDateTime2String(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(formatter);
    }

    /**
     * 返回yyyy-MM-dd HH:mm:ss格式的时间
     *
     * @param localDateTime 需要转换的时间
     * @return java.lang.String
     */
    public static String localDateTime2StringStyle2(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

    /**
     * 返回yyyy-MM-dd格式的时间
     *
     * @param localDateTime 需要转换的时间
     * @return java.lang.String
     */
    public static String localDateTime2StringStyle3(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateTime.format(formatter);
    }

    /***
     * 判断当前时间是否处于每天的指定时间段内（包含开始时间，不含结束时间）
     * @param startHour 开始小时（0-23）
     * @param startMinute 开始分钟（0-59）
     * @param endHour 结束小时（0-23）
     * @param endMinute 结束分钟（0-59）
     * @return boolean 是否在时间段内
     */
    public static boolean isInDailyRange(int startHour, int startMinute, int endHour, int endMinute, LocalDateTime localDateTime) {
        // 获取当前时间的时分秒
        LocalTime nowTime = localDateTime.toLocalTime();

        // 定义每天的开始和结束时间
        LocalTime startTime = LocalTime.of(startHour, startMinute);
        LocalTime endTime = LocalTime.of(endHour, endMinute);

        // 比较当前时间是否在 [startTime, endTime) 范围内
        return nowTime.isAfter(startTime) || nowTime.equals(startTime)
                && nowTime.isBefore(endTime);
    }

    /***
     * 支持跨天的时间段判断（例如 22:00 至次日 06:00）
     * @param startHour 开始小时（0-23）
     * @param startMinute 开始分钟（0-59）
     * @param endHour 结束小时（0-23）
     * @param endMinute 结束分钟（0-59）
     * @return boolean 是否在时间段内
     */
    public static boolean isInDailyRangeCrossDay(int startHour, int startMinute, int endHour, int endMinute) {
        LocalTime nowTime = LocalDateTime.now().toLocalTime();
        LocalTime startTime = LocalTime.of(startHour, startMinute);
        LocalTime endTime = LocalTime.of(endHour, endMinute);

        // 跨天情况：开始时间 > 结束时间（如 22:00 到 06:00）
        if (startTime.isAfter(endTime)) {
            // 当前时间 >= 开始时间 或 当前时间 < 结束时间
            return nowTime.isAfter(startTime) || nowTime.equals(startTime)
                    || nowTime.isBefore(endTime);
        } else {
            // 非跨天情况，正常判断
            return nowTime.isAfter(startTime) || nowTime.equals(startTime)
                    && nowTime.isBefore(endTime);
        }
    }
}
