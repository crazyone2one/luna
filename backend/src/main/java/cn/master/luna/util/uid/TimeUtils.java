package cn.master.luna.util.uid;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Created by 11's papa on 2025/7/8
 */
public class TimeUtils extends DateUtils {
    public static final String DAY_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date parseByDayPattern(String str) {
        return parseDate(str, DAY_PATTERN);
    }

    public static Date parseDate(String str, String pattern) {
        try {
            return parseDate(str, new String[]{pattern});
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDataStr(long timeStamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DAY_PATTERN);
        return dateFormat.format(timeStamp);
    }
}
