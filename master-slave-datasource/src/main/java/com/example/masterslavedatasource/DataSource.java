package com.example.masterslavedatasource;

import java.lang.annotation.*;

// 5 创建自定义注解 用于标记需要切换数据源的方法或类
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource {
    String value() default "master";
}