package cn.master.luna.handler.annotation;

import java.lang.annotation.*;

/**
 * @author Created by 11's papa on 2025/7/2
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoResultHolder {
}
