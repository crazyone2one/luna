package cn.master.luna.constants;

import lombok.Getter;

/**
 * @author Created by 11's papa on 2025/7/28
 */
@Getter
public enum PersonJobType {
    P10("10", "其他"),
    P11("11", "董事长"),
    P12("12", "党委书记"),
    P13("13", "矿长"),
    P14("14", "总经理"),
    P15("15", "副矿长"),
    P16("16", "副总经理"),
    P17("17", "生产副矿长/生产副总"),
    P18("18", "安全副矿长/副总经理"),
    ;
    private final String code;
    private final String desc;

    PersonJobType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
