package cn.master.luna.exception;

/**
 * @author Created by 11's papa on 2025/7/18
 */
public enum SystemResultCode implements IResultCode {
    GLOBAL_USER_ROLE_PERMISSION(101001, "global_user_role_permission_error"),
    NO_ORG_USER_ROLE_PERMISSION(101007, "organization_user_role_permission_error"),
    NO_PROJECT_USER_ROLE_PERMISSION(101012, "project_user_role_permission_error"),
    ADMIN_USER_ROLE_PERMISSION(100019, "internal_admin_user_role_permission_error"),
    GLOBAL_USER_ROLE_RELATION_SYSTEM_PERMISSION(101003, "global_user_role_relation_system_permission_error"),
    GLOBAL_USER_ROLE_LIMIT(101004, "global_user_role_limit_error"),
    USER_ROLE_RELATION_EXIST(100002, "user_role_relation_exist_error"),
    GLOBAL_USER_ROLE_EXIST(101002, "global_user_role_exist_error"),
    INTERNAL_USER_ROLE_PERMISSION(100003, "internal_user_role_permission_error"),
    ;
    private final int code;
    private final String message;

    SystemResultCode(int code, String message) {
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
