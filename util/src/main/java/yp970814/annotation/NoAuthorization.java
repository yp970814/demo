package yp970814.annotation;

import java.lang.annotation.*;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 11:46
 */
@Target(ElementType.METHOD)//定义在方法上
@Retention(RetentionPolicy.RUNTIME)//注解的时间
@Documented//标识是一个注解
public @interface NoAuthorization {
}
