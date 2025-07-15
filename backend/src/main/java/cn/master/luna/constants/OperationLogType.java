package cn.master.luna.constants;

/**
 * @author Created by 11's papa on 2025/7/14
 */
public enum OperationLogType {
    ADD,
    DELETE,
    UPDATE,
    DEBUG,
    REVIEW,
    COPY,
    EXECUTE,
    SHARE,
    RESTORE,
    IMPORT,
    EXPORT,
    LOGIN,
    SELECT,
    RECOVER,
    LOGOUT,
    DISASSOCIATE,
    ASSOCIATE,
    QRCODE,
    ARCHIVED,
    STOP,
    RERUN;

    public boolean contains(OperationLogType keyword) {
        return this.name().contains(keyword.name());
    }
}
