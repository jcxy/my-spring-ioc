package com.liuxing.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author CVToolMan
 * @create 2024/3/24 9:32
 */
@Retention(RetentionPolicy.RUNTIME)//表示运行时生效
@Target(ElementType.TYPE)//表示只能标注在类上面
public @interface ComponentScan {
    String value() default "";
}
