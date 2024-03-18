package yp970814.lock.redis;

import java.util.concurrent.TimeUnit;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-18 13:30
 */
public interface RedisLock {
    // 枷锁
    boolean tryLock(String key, long timeout, TimeUnit unit);
    // 解锁
    void releaseLock(String key);
}
