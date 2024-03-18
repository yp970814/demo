package yp970814.lock.redis.jedis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-10-17 15:53
 */
@Slf4j
@Component
public class ApplicationInitConfig implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    RedisContext redisContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            initRedisManager();
        } catch (Exception e) {
            log.error(this.getErrorMessage(e));
        }
    }

    private static String getErrorMessage(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException var9) {
                    var9.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }

    private void initRedisManager() {
        RedisManager.init(redisContext);
    }

}
