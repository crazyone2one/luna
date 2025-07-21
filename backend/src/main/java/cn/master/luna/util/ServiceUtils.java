package cn.master.luna.util;

import cn.master.luna.exception.CustomException;

import static cn.master.luna.handler.CustomHttpResultCode.NOT_FOUND;

/**
 * @author Created by 11's papa on 2025/7/18
 */
public class ServiceUtils {
    //用于排序的pos
    public static final int POS_STEP = 4096;

    /**
     * 保存资源名称，在处理 NOT_FOUND 异常时，拼接资源名称
     */
    private static final ThreadLocal<String> resourceName = new ThreadLocal<>();

    public static String getResourceName() {
        return resourceName.get();
    }

    public static void clearResourceName() {
        resourceName.remove();
    }

    public static <T> T checkResourceExist(T resource, String name) {
        if (resource == null) {
            resourceName.set(name);
            throw new CustomException(NOT_FOUND);
        }
        return resource;
    }
}
