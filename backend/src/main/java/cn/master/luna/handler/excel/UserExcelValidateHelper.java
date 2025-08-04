package cn.master.luna.handler.excel;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Created by 11's papa on 2025/8/4
 */
@Component
public class UserExcelValidateHelper {
    private static Validator validator;

    @Resource
    public void setValidator(Validator validator) {
        UserExcelValidateHelper.validator = validator;
    }

    public static <T> String validateEntity(T obj) {
        if (validator == null) {
            throw new IllegalStateException("Validator has not been initialized. Ensure the bean is properly created by Spring.");
        }
        StringBuilder result = new StringBuilder();
        Set<ConstraintViolation<T>> violations = validator.validate(obj, Default.class);

        if (violations != null && !violations.isEmpty()) {
            // 使用TreeSet进行有序和去重处理
            Set<String> errorMsgSet = new TreeSet<>();
            for (ConstraintViolation<T> violation : violations) {
                errorMsgSet.add(violation.getMessage());
            }
            errorMsgSet.forEach(item -> result.append(item).append("; "));
        }
        return result.toString();
    }
}
