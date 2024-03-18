package yp970814.threadLocal;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-22 15:52
 */
public class ThreadLocalUtil {
    public static final TransmittableThreadLocal<Map<String, Object>> THREAD_LOCAL = new TransmittableThreadLocal<>();
    //设置线程需要保存的值
    public static void setValue (String key, Object value) {
        Map<String, Object> shareObj = THREAD_LOCAL.get();
        if (shareObj == null) {
            shareObj = new HashMap();
        }
        shareObj.put(key, value);
        THREAD_LOCAL.set(shareObj);
    }

    //获取线程中保存的值
    public static Object getValue(String key) {
        return getValue(key, null);
    }

    //获取线程中保存的值
    public static Object getValue(String key, Object defaultValue) {
        Map<String, Object> shareObj = THREAD_LOCAL.get();
        if (shareObj == null) {
            return defaultValue;
        }
        return shareObj.getOrDefault(key, defaultValue);
    }

    //移除线程中保存的值
    public static void remove(String key) {
        Map<String, Object> shareObj = THREAD_LOCAL.get();
        if (shareObj == null) {
            return;
        }
        shareObj.remove(key);
        if (shareObj.isEmpty()) {
            THREAD_LOCAL.remove();
        } else {
            THREAD_LOCAL.set(shareObj);
        }
    }

    //移除线程中保存的值
    public static void removeAll() {
        THREAD_LOCAL.remove();
    }
}
