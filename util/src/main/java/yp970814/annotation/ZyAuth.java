package yp970814.annotation;

import java.lang.annotation.*;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-03-13 10:25
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME )
public @interface ZyAuth {
    //权限名
    String value() default "";

    //限制的范围 0全部使用 1操作 2接口api调用（针对三方调用）
    //int type() default 0;

}
