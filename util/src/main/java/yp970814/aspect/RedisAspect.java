package yp970814.aspect;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import yp970814.annotation.RedisCache;
import yp970814.redis.RedisClusterDao;

/**
 * AOP redis缓存拦截器
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 13:34
 */
public class RedisAspect {

    // private static final Logger logger =
    // LoggerFactory.getLogger(RedisAspect.class);

    private final RedisClusterDao redisDao = new RedisClusterDao();

    public static Map<String, Object> transBean2Map(Object obj) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (obj == null) {
            return map;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
        }
        return map;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public Object handleRedisCache(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null && method.getAnnotation(RedisCache.class) != null) {
            RedisCache redisCache = method.getAnnotation(RedisCache.class);
            Object[] args = point.getArgs();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            for (Object arg : args) {
                if (arg != null) {
                    paramMap.putAll(this.transBean2Map(arg));
                }
            }
            String redisKey = StringUtils.isNotBlank(redisCache.redisKey()) ? redisCache.redisKey() : "";
            if (StringUtils.isNotBlank(redisKey)) {
                // 如果有动态参数
                String regex = "\\((.*?)\\)";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(redisKey);
                while (m.find()) {
                    String key = m.group().substring(1, m.group().length() - 1);
                    if (this.isNumeric(key)) {
                        redisKey = redisKey.replace(m.group(), args[Integer.valueOf(key)].toString());
                    } else {
                        redisKey = redisKey.replace(m.group(),
                                paramMap.get(m.group().substring(1, m.group().length() - 1)).toString());
                    }
                }
                if (!redisCache.clearCache()) {
                    // 先判断缓存中是否存在
                    Class returnType = methodSignature.getReturnType();
                    Object cache = null;
                    // 先从缓存里取值
                    if (returnType.getName().endsWith("Response")) {
                        String val = redisDao.get(redisKey);
                        if (val != null) {
                            cache = Response.ok(val).build();
                        }
                    } else if (returnType.getName().endsWith("List")) {
                        cache = redisDao.getObjectList(redisKey);
                    } else {
                        cache = redisDao.get(redisKey, returnType);
                    }
                    if (cache != null) {
                        return cache;
                    }
                    Object returnObj = point.proceed();
                    // 将结果写入缓存
                    if (returnType.getName().endsWith("Response")) {
                        redisDao.set(redisKey, ((Response) returnObj).getEntity());
                    } else {
                        redisDao.set(redisKey, returnObj);
                    }
                    return returnObj;
                } else {
                    Object returnObj = point.proceed();
                    // 清除缓存
                    redisDao.del(redisKey);
                    return returnObj;
                }
            }
        }
        return point.proceed();
    }

    public static void main(String[] args) {
        String x = "sdfsafd(hello)(xxx)";
        String regex = "\\((.*?)\\)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(x);
        while (m.find()) {
            System.out.println(m.group());
            x = x.replace(m.group(), "123");
            System.out.println(x);
        }
    }
}
