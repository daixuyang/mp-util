package com.github.daixuyang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * mybatis push 注解
 * @author 代旭杨
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MpQuery {

    /**
     * 表前缀
     */
    String prefix() default "t";

    /**
     * 查询方式
     */
    String type() default "eq";

    /**
     * 字段
     * 用户实体类中间转换查询
     */
    String field() default "";


    /**
     * 字段 三木运算
     * 如果该字段值等于1 指向 A字段 ？ 指向 B 字段
     * 示例： 1 ? A : B
     */
    String fieldMk() default "";


}
