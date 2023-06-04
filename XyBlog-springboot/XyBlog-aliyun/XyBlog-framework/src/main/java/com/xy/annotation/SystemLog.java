package com.xy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这个是用来自定义aop注解的
 */
@Retention(RetentionPolicy.RUNTIME)//注解生效的时间：运行时期
@Target(ElementType.METHOD)//注解的目标：方法
public @interface SystemLog {

    String businessName();// TODO 为什么注解接口的属性要加个括号呢

}
