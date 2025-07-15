package cn.master.luna.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Created by 11's papa on 2025/7/15
 */
public class DateUtils {
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
}
