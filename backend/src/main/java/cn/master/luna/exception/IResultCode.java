package cn.master.luna.exception;

import cn.master.luna.util.Translator;

/**
 * @author Created by 11's papa on 2025/7/2
 */
public interface IResultCode {
    /**
     * 返回状态码
     */
    int getCode();

    /**
     * 返回状态码信息
     */
    String getMessage();

    default String getTranslationMessage(String message) {
        return Translator.get(message, message);
    }
}
