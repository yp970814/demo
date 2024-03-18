package yp970814.threadPool;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-10-01 7:50
 */
@Slf4j
@Configuration("YPThreadPoolConfiguration")
public class YPThreadPool {

    @Value("${threadPool.threadQueueSize:1000}")
    private Integer THREAD_QUEUE_SIZE = 1000;
    @Value("${threadPool.corePoolSize:20}")
    private Integer CORE_POOL_SIZE = 20;
    @Value("${threadPool.maxPoolSize:35}")
    private Integer MAX_POOL_SIZE = 30;
    private static ExecutorService executor = null;
    private BlockingQueue<Runnable> workQueue = null;

    public YPThreadPool() {}

    @Bean
    public YPThreadPool ypThreadPool() {
        try {
            if (this.workQueue == null) {
                this.workQueue = new ArrayBlockingQueue(this.THREAD_QUEUE_SIZE);
            }
            if (executor == null) {
                executor = TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(this.CORE_POOL_SIZE, this.MAX_POOL_SIZE, 10L, TimeUnit.MINUTES, this.workQueue));
            }
            log.info("初始化《WMS-APP后台业务处理线程池》完成！");
            return this;
        } catch (Exception var) {
            this.log.error("初始化《WMS-APP后台业务处理线程池》线程池时出现异常......", var);
            throw new RuntimeException();
        }
    }

    public static ExecutorService getExecute() {
        return executor;
    }

}
