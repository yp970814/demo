package yp970814.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁实现
 * 互斥性：同一时刻只能有一个线程持有锁并执行临界操作，防止数据竞争和冲突
 * 超时释放：在一定时间内未能成功获取锁时，自动释放锁，避免不必要的线程等待和资源浪费
 * 可重入性：同一个线程如果已经持有锁，则再次请求锁时应该能够成功获取，实现可重入性
 * 高性能和高可用：加锁和解锁的开销要尽可能小，同时要保证锁的可靠性，防止分布式锁失效
 * 阻塞或者非阻塞：在获取锁时使用阻塞操作或轮询来等待，或者通过非阻塞操作立即返回获取锁的结果
 * @Author yuanping970814@163.com
 * @Date 2023-09-18 13:32
 */
public class RedisLockImpl implements RedisLock{
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private ThreadLocal<String> threadLocal = new ThreadLocal<String>();
    private ThreadLocal<Integer> threadLocalInteger = new ThreadLocal<Integer>();

    @Override
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        boolean isLocked = false;
        if (null == threadLocal.get()) {
            String uuid = UUID.randomUUID().toString();
            threadLocal.set(uuid);
            isLocked =  stringRedisTemplate.opsForValue().setIfAbsent(key, uuid, timeout, unit);
            // 如果获取锁失败，则自旋获取锁，直到成功
            if (!isLocked) {
                for (;;) {
                    isLocked = stringRedisTemplate.opsForValue().setIfAbsent(key, uuid, timeout, unit);
                    if (isLocked) {
                        break;
                    }
                }
            }
            // 启动新线程来执行定时任务，更新锁过期时间
            new Thread(new UpdateLockTimeoutTask(uuid, stringRedisTemplate, key)).start();
        } else {
            isLocked = true;
        }
        // 加锁成功后将计数器加1
        if (isLocked) {
            Integer count = threadLocalInteger.get() == null ? 0 : threadLocalInteger.get();
            threadLocalInteger.set(count++);
        }
        return isLocked;
    }

    @Override
    public void releaseLock(String key) {
        // 当前线程中绑定的uuid与Redis中的uuid相同时，再执行删除锁的操作
        String uuid = stringRedisTemplate.opsForValue().get(key);
        if (threadLocal.get().equals(uuid)) {
            Integer count = threadLocalInteger.get();
            // 计数器减为0时释放锁
            if (null == count || --count <= 0){
                stringRedisTemplate.delete(key);
                // 获取更新锁超时时间的线程并中断
                long threadId = Long.parseLong(stringRedisTemplate.opsForValue().get(uuid));
                Thread updateLockTimeoutThread = ThreadUtils.getThreadByThreadId(threadId);
                if (updateLockTimeoutThread != null) {
                    // 中断更新锁超时时间的线程
                    updateLockTimeoutThread.interrupt();
                    stringRedisTemplate.delete(uuid);
                }
            }
        }
    }
}
