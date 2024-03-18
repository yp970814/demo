package yp970814;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author yuanping970814@163.com
 * @Date ${DATE} ${TIME}
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAutoDataSourceProxy
public class SeataApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataApplication.class, args);
    }

}
