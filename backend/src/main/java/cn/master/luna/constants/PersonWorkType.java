package cn.master.luna.constants;

import lombok.Getter;

/**
 * @author Created by 11's papa on 2025/7/28
 */
@Getter
public enum PersonWorkType {
    P10("10", "其他"),
    P11("11", "电气安装工"),
    P12("12", "机械安装工"),
    P13("13", "矿井维修工"),
    P14("14", "电工"),
    P15("15", "电焊工"),
    P16("16", "主要通风机操作工"),
    P17("17", "井下爆破工"),
    P18("18", "火工品管理工"),
    ;
    private final String code;
    private final String desc;

    PersonWorkType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
