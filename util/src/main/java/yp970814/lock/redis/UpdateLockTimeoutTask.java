package yp970814.lock.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-18 14:25
 */
public class UpdateLockTimeoutTask implements Runnable{
    private String uuid;
    private StringRedisTemplate stringRedisTemplate;
    private String key;
    public UpdateLockTimeoutTask(String uuid, StringRedisTemplate stringRedisTemplate, String key) {
        this.uuid = uuid;
        this.stringRedisTemplate = stringRedisTemplate;
        this.key = key;
    }
    @Override
    public void run() {
        // 以uuid为key，当前线程id为value保存到Redis中
        stringRedisTemplate.opsForValue().set(uuid, String.valueOf(Thread.currentThread().getId()));
        // 定义更新锁的过期时间
        while (true) {
            stringRedisTemplate.expire(key, 30, TimeUnit.SECONDS);
            try {
                // 每隔10秒执行一次
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
