package yp970814.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 13:19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface RedisCache {

    String redisKey() default "";

    /** 是否清除缓存 */
    boolean clearCache() default false;
}
