package cn.master.luna.util;

import cn.master.luna.util.uid.DefaultUidGenerator;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author Created by 11's papa on 2025/7/8
 */
public class IDGenerator {
    private static final DefaultUidGenerator DEFAULT_UID_GENERATOR;

    static {
        DEFAULT_UID_GENERATOR = CommonBeanFactory.getBean(DefaultUidGenerator.class);
    }

    public static Long nextNum() {
        return DEFAULT_UID_GENERATOR.getUID();
    }

    public static String nextStr() {
        return String.valueOf(DEFAULT_UID_GENERATOR.getUID());
    }

    /***
     * 生成大于等于 256 位（长度 length 及以上）随机字符串的方法
     * @param length 字符串长度
     * @return String
     */
    public static String randomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);

        // 使用 Base64 编码确保字符串可打印
        String randomString = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // 截取到目标长度（Base64 可能略长）
        return randomString.substring(0, length);
    }
}
