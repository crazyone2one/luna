package cn.master.luna.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * @author Created by 11's papa on 2025/7/2
 */
public class JacksonUtils {
    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .build();
    private static final TypeFactory typeFactory = objectMapper.getTypeFactory();

    public static final int DEFAULT_MAX_STRING_LEN = Integer.MAX_VALUE;

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        // 支持json字符中带注释符
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        // 自动检测所有类的全部属性
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // 如果一个对象中没有任何的属性，那么在序列化的时候就会报错
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // 设置JSON处理字符长度限制
        objectMapper.getFactory()
                .setStreamReadConstraints(StreamReadConstraints.builder().maxStringLength(DEFAULT_MAX_STRING_LEN).build());
        // 处理时间格式
        objectMapper.registerModule(new JavaTimeModule());

    }
    public static String toJSONString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
