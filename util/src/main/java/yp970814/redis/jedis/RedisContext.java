package yp970814.redis.jedis;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-10-01 14:57
 */
public class RedisContext {

    private JedisPool jedisPool = null;

    private int expired = 3600;

    @Value("${redis.maxActive}")
    private String maxActive;

    @Value("${redis.maxIdle}")
    private String maxIdle;

    @Value("${redis.timeout}")
    private String timeout;

    @Value("${redis.key.expired:}")
    private String confExpired;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private String port;

    @Value("${redis.database:0}")
    private String database;

    @Bean(name = "redisContext")
    public RedisContext redisContext() {
        try {
            //获取配置中心中的配置
            Config appConfig = ConfigService.getAppConfig();
            JedisPoolConfig config = new JedisPoolConfig();
            // TODO: 16/11/10 默认值
            config.setMaxTotal(Integer.parseInt(maxActive));
            config.setMaxIdle(Integer.parseInt(maxIdle));
            config.setMaxWaitMillis(Long.parseLong(timeout));
            config.setTestOnBorrow(true);
            if (StringUtils.isNotBlank(confExpired)) {
                expired = Integer.parseInt(confExpired);
            }
            if (StringUtils.isBlank(password)) {
                password = null;
            }
            jedisPool = new JedisPool(config, host,
                    Integer.parseInt(port),
                    Integer.parseInt(timeout),
                    password,
                    Integer.parseInt(database)
            );
            return this;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int getExpired() {
        return this.expired;
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public void returnJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * @param dBindex 选择指定DB
     * @return
     */
    public Jedis getJedis(int dBindex) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(dBindex);
        return jedis;
    }

}
