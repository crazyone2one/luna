package cn.master.luna.handler;

import cn.master.luna.exception.IResultCode;

/**
 * @author Created by 11's papa on 2025/7/2
 */
public enum CustomHttpResultCode implements IResultCode {
    SUCCESS(100200, "操作成功"),
    FAILED(100500, "系统未知异常"),
    VALIDATE_FAILED(100400, "参数校验失败"),
    UNAUTHORIZED(100401, "用户认证失败"),
    FORBIDDEN(100403, "权限认证失败"),
    NOT_FOUND(100404, "%s不存在");
    ;
    private final int code;
    private final String message;

    CustomHttpResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
