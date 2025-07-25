package cn.master.luna.util;

import java.util.Random;

/**
 * @author Created by 11's papa on 2025/7/15
 */
public class StringUtils {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();

    public static String doubleTypeString(int min, int max) {
        return String.format("%.2f%n", min + ((max - min) * random.nextDouble()));
    }

    /***
     * 生成指定长度的字符串
     * @param length 字符串长度
     * @return String
     */
    public static String generateRandomString(int length) {
//        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(index));
        }

        return builder.toString();
    }

    public static <T extends Enum<T>> T getRandomEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[random.nextInt(clazz.getEnumConstants().length)];
    }
}
