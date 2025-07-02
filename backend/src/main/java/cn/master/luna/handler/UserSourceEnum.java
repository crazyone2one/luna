package cn.master.luna.handler;

import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;

/**
 * @author Created by 11's papa on 2025/7/2
 */
@AllArgsConstructor
public enum UserSourceEnum {
    LOCAL (1, "LOCAL"),
    ;
    private final int code;
    private final String desc;

    public int getCode() {
        return code;
    }
    @EnumValue
    public String getDesc() {
        return desc;
    }
}
