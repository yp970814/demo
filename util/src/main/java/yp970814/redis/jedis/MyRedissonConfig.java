package yp970814.redis.jedis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-10-01 14:36
 */
@Configuration
public class MyRedissonConfig {
    @Value("${redis.password}")
    private String password;

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private String port;

    @Value("${redis.database:0}")
    private int database;

    @Value("${redis.maxActive}")
    private int maxActive;

    @Value("${redis.maxIdle}")
    private int maxIdle;

    @Value("${redis.timeout}")
    private int timeout;

    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() {
        Config config = new Config();
        //Redis多节点
        // config.useClusterServers()
        //     .addNodeAddress("redis://127.0.0.1:6379", "redis://127.0.0.1:7001");
        //Redis单节点
        SingleServerConfig singleServerConfig = config.useSingleServer();
        //可以用"rediss://"来启用SSL连接
        String address = "redis://" + host + ":" + port;
        singleServerConfig.setAddress(address);
        //设置 数据库编号
        singleServerConfig.setDatabase(database);
        singleServerConfig.setPassword(password);
//        singleServerConfig.setConnectionPoolSize(maxActive);
//        singleServerConfig.setConnectTimeout(timeout);
        singleServerConfig.setTimeout(timeout);
        config.setLockWatchdogTimeout(5 * 1000L);
//        singleServerConfig.set
        //连接池大小:默认值：64
        // singleServerConfig.setConnectionPoolSize()
//        config.setMaxTotal(Integer.parseInt(maxActive));
//        config.setMaxIdle(Integer.parseInt(maxIdle));
//        config.setMaxWaitMillis(Long.parseLong(timeout));
        return Redisson.create(config);
    }

}
