package cn.master.luna.util;

import cn.master.luna.util.uid.DefaultUidGenerator;

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
}
