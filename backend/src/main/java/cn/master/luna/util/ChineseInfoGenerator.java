package cn.master.luna.util;

import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @author Created by 11's papa on 2025/7/28
 */
public class ChineseInfoGenerator {
    // 1. 扩充姓氏库：单姓（按常见度分组，提高常见姓氏出现概率）
    private static final String[] COMMON_SURNAMES = {
            "王", "李", "张", "刘", "陈", "杨", "黄", "赵", "吴", "周",
            "徐", "孙", "马", "朱", "胡", "林", "郭", "何", "高", "罗"
    };
    private static final String[] UNCOMMON_SURNAMES = {
            "郑", "梁", "谢", "宋", "唐", "许", "邓", "冯", "韩", "曹",
            "曾", "彭", "萧", "蔡", "潘", "田", "董", "袁", "于", "余"
    };
    private static final String[] RARE_SURNAMES = {
            "叶", "蒋", "杜", "苏", "魏", "程", "吕", "丁", "沈", "任",
            "姚", "卢", "傅", "钟", "姜", "崔", "谭", "廖", "范", "汪"
    };

    // 2. 复姓库（增加多样性）
    private static final String[] COMPOUND_SURNAMES = {
            "欧阳", "上官", "司马", "东方", "皇甫", "尉迟", "公羊", "赫连",
            "澹台", "公冶", "宗政", "濮阳", "淳于", "单于", "太叔", "申屠"
    };

    // 3. 名字字库（区分性别倾向和单/双字）
    // 中性字（单字名通用）
    private static final String[] NEUTRAL_CHARS = {
            "明", "华", "杰", "强", "伟", "敏", "静", "博", "文", "晨",
            "宇", "轩", "涵", "睿", "哲", "嘉", "欣", "逸", "诺", "辰"
    };
    // 男性倾向字（单字名）
    private static final String[] MALE_CHARS = {
            "刚", "勇", "毅", "峰", "军", "磊", "鹏", "斌", "辉", "翔",
            "涛", "健", "豪", "龙", "虎", "超", "凯", "阳", "朗", "栋"
    };
    // 女性倾向字（单字名）
    private static final String[] FEMALE_CHARS = {
            "芳", "娜", "婷", "琳", "玲", "娟", "倩", "妍", "琪", "雅",
            "馨", "雪", "梅", "兰", "虹", "颖", "梦", "娇", "琳", "璇"
    };
    // 双字名前缀（增加组合多样性）
    private static final String[] DOUBLE_NAME_PREFIX = {
            "子", "梓", "雨", "语", "沐", "若", "诗", "思", "宇", "皓",
            "嘉", "家", "俊", "泽", "艺", "依", "怡", "奕", "逸", "景"
    };
    // 双字名后缀（与前缀组合）
    private static final String[] DOUBLE_NAME_SUFFIX = {
            "轩", "涵", "睿", "哲", "琪", "瑶", "彤", "欣", "晨", "辰",
            "洋", "阳", "宇", "航", "诺", "彤", "熙", "然", "昕", "玥"
    };

    // 身份证前6位地址码（示例：覆盖部分省市）
    private static final String[] AREA_CODES = {
            "110101", "110102", "310101", "310104", "440106", "440305",
            "510104", "510107", "120101", "120104", "320102", "320105"
    };

    // 学历列表
    private static final String[] EDUCATION = {
            "其他", "初中", "高中", "中专", "专科", "本科", "研究生", "博士"
    };

    // 职业列表
    private static final String[] OCCUPATIONS = {
            "工程师", "教师", "医生", "公务员", "程序员", "销售", "学生", "农民"
    };

    // 手机号段
    private static final String[] PHONE_PREFIXES = {
            "130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
            "150", "151", "152", "153", "155", "156", "157", "158", "159",
            "170", "171", "173", "176", "177", "178", "180", "181", "182", "183"
    };

    private static final Random random = new Random();
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 生成完整的模拟个人信息
     */
    public static PersonInfo generatePersonInfo() {
        // 生成姓名
        String name = generateFullName();

        // 生成出生日期（1950-2005年）
        LocalDate birthDate = generateBirthDate();

        // 生成身份证号（与出生日期关联）
        String idCard = generateIdCard(birthDate);

        // 计算年龄
        int age = calculateAge(birthDate);

        // 生成性别（基于身份证号）
        String gender = getGenderFromIdCard(idCard);

        // 生成电话号码
        String phone = generatePhoneNumber();

        // 生成学历（与年龄匹配）
        String education = generateEducation(age);

        // 生成职业
        String occupation = generateOccupation(age, education);

        // 生成地址（模拟）
        String address = generateAddress();

        return new PersonInfo(
                name, idCard, gender, age, birthDate.format(BIRTH_DATE_FORMATTER),
                phone, education, occupation, address
        );
    }

    /**
     * 生成姓名
     */
    private static String generateFullName() {
        // 随机选择是否使用复姓（10%概率）
        String surname = random.nextDouble() < 0.1
                ? COMPOUND_SURNAMES[random.nextInt(COMPOUND_SURNAMES.length)]
                : getSingleSurname();

        // 随机选择名字长度（单字或双字，6:4比例）
        String givenName = random.nextDouble() < 0.6
                ? generateSingleGivenName()
                : generateDoubleGivenName();

        return surname + givenName;
    }
    private static String getSingleSurname() {
        double randomValue = random.nextDouble();
        if (randomValue < 0.6) { // 60%概率选择常见姓氏
            return COMMON_SURNAMES[random.nextInt(COMMON_SURNAMES.length)];
        } else if (randomValue < 0.9) { // 30%概率选择不常见姓氏
            return UNCOMMON_SURNAMES[random.nextInt(UNCOMMON_SURNAMES.length)];
        } else { // 10%概率选择稀有姓氏
            return RARE_SURNAMES[random.nextInt(RARE_SURNAMES.length)];
        }
    }
    private static String generateSingleGivenName() {
        double genderRatio = random.nextDouble();
        if (genderRatio < 0.4) { // 40%男性倾向
            return MALE_CHARS[random.nextInt(MALE_CHARS.length)];
        } else if (genderRatio < 0.8) { // 40%女性倾向
            return FEMALE_CHARS[random.nextInt(FEMALE_CHARS.length)];
        } else { // 20%中性字
            return NEUTRAL_CHARS[random.nextInt(NEUTRAL_CHARS.length)];
        }
    }
    private static String generateDoubleGivenName() {
        String prefix = DOUBLE_NAME_PREFIX[random.nextInt(DOUBLE_NAME_PREFIX.length)];
        String suffix = DOUBLE_NAME_SUFFIX[random.nextInt(DOUBLE_NAME_SUFFIX.length)];
        return prefix + suffix;
    }

    /**
     * 生成出生日期
     */
    private static LocalDate generateBirthDate() {
        int year = 1970 + random.nextInt(30); // 1950-2005
        int month = 1 + random.nextInt(12);

        // 计算当月最大天数
        int maxDay;
        if (month == 2) {
            maxDay = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0 ? 29 : 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            maxDay = 30;
        } else {
            maxDay = 31;
        }

        int day = 1 + random.nextInt(maxDay);
        return LocalDate.of(year, month, day);
    }

    /**
     * 生成符合规则的身份证号
     */
    private static String generateIdCard(LocalDate birthDate) {
        StringBuilder id = new StringBuilder();

        // 前6位：地址码
        id.append(AREA_CODES[random.nextInt(AREA_CODES.length)]);

        // 8位出生日期
        id.append(birthDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        // 3位顺序码（第17位用于区分性别）
        int sequence = random.nextInt(1000);
        // 确保性别位（倒数第二位）符合规则
        if (random.nextBoolean()) {
            // 男性：奇数
            if (sequence % 2 == 0) sequence++;
        } else {
            // 女性：偶数
            if (sequence % 2 == 1) sequence++;
            if (sequence >= 1000) sequence = 998;
        }
        id.append(String.format("%03d", sequence));

        // 最后1位校验码（使用简化算法）
        id.append(calculateCheckCode(id.toString()));

        return id.toString();
    }

    /**
     * 计算身份证校验码（简化版）
     */
    private static char calculateCheckCode(String idPrefix) {
        final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        int sum = 0;
        for (int i = 0; i < idPrefix.length(); i++) {
            sum += Character.getNumericValue(idPrefix.charAt(i)) * WEIGHTS[i];
        }
        return CHECK_CODES[sum % 11];
    }

    /**
     * 从身份证号获取性别
     */
    private static String getGenderFromIdCard(String idCard) {
        int genderCode = Character.getNumericValue(idCard.charAt(16));
        return genderCode % 2 == 1 ? "男" : "女";
    }

    /**
     * 计算年龄
     */
    private static int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * 生成电话号码
     */
    private static String generatePhoneNumber() {
        String prefix = PHONE_PREFIXES[random.nextInt(PHONE_PREFIXES.length)];
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            suffix.append(random.nextInt(10));
        }
        return prefix + suffix;
    }

    /**
     * 生成与年龄匹配的学历
     */
    private static String generateEducation(int age) {
        if (age < 12) return "学龄前";
        if (age < 15) return "小学";
        if (age < 18) return EDUCATION[random.nextInt(2)]; // 初中、高中
        if (age < 22) return EDUCATION[random.nextInt(3) + 2]; // 高中、中专、大专
        if (age < 60) return EDUCATION[random.nextInt(EDUCATION.length)];
        return EDUCATION[random.nextInt(4) + 3]; // 偏向较高学历
    }

    /**
     * 生成与年龄和学历匹配的职业
     */
    private static String generateOccupation(int age, String education) {
        if (age < 16) return "学生";
        if (age >= 60) return "退休";
        if ("博士".equals(education) || "硕士".equals(education)) {
            return OCCUPATIONS[random.nextInt(3)]; // 工程师、教师、医生
        }
        return OCCUPATIONS[random.nextInt(OCCUPATIONS.length)];
    }

    /**
     * 生成模拟地址
     */
    private static String generateAddress() {
        String[] provinces = {"北京市", "上海市", "广东省", "江苏省", "浙江省", "四川省"};
        String[] cities = {"朝阳区", "徐汇区", "天河区", "鼓楼区", "西湖区", "武侯区"};
        String[] roads = {"长安街", "南京路", "北京路", "人民路", "解放路", "科技园路"};

        String province = provinces[random.nextInt(provinces.length)];
        String city = cities[random.nextInt(cities.length)];
        String road = roads[random.nextInt(roads.length)];
        int number = 1 + random.nextInt(200);
        int unit = 1 + random.nextInt(30);
        int room = 101 + random.nextInt(900);

        return String.format("%s%s%s%d号%d单元%d室",
                province, city, road, number, unit, room);
    }

    /**
     * 个人信息实体类
     */
    public static class PersonInfo {
        @Getter
        private final String name;
        @Getter
        private final String idCard;
        @Getter
        private final String gender;
        private final int age;
        @Getter
        private final String birthDate;
        @Getter
        private final String phone;
        @Getter
        private final String education;
        private final String occupation;
        private final String address;

        public PersonInfo(String name, String idCard, String gender, int age, String birthDate,
                          String phone, String education, String occupation, String address) {
            this.name = name;
            this.idCard = idCard;
            this.gender = gender;
            this.age = age;
            this.birthDate = birthDate;
            this.phone = phone;
            this.education = education;
            this.occupation = occupation;
            this.address = address;
        }

        @Override
        public String toString() {
            return String.format("""
                姓名: %s
                身份证号: %s
                性别: %s
                年龄: %d岁
                出生日期: %s
                手机号: %s
                学历: %s
                职业: %s
                地址: %s
                """, name, idCard, gender, age, birthDate, phone, education, occupation, address);
        }
    }
}
