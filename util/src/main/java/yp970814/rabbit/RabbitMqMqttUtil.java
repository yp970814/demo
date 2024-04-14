package yp970814.rabbit;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import yp970814.exception.BusinessException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;


/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:57
 */
@Component
public class RabbitMqMqttUtil {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqMqttUtil.class);

    private static String host;
    private static String username;
    private static String password;
    private static int port;
    private static String environmentconfig;
    private static Address[] address;

    @Value("${rabbitmq_mqtt_host}")
    public void setHost(String str) {
        RabbitMqMqttUtil.host = str;
    }

    @Value("${rabbitmq_mqtt_userName}")
    public void setUsername(String str) {
        RabbitMqMqttUtil.username = str;
    }

    @Value("${rabbitmq_mqtt_password}")
    public void setPassword(String str) {
        RabbitMqMqttUtil.password = str;
    }

    @Value("${rabbitmq_mqtt_port}")
    public void setPort(int str) {
        RabbitMqMqttUtil.port = str;
    }

    @Value("${environ_ment_config}")
    public void setEnvironmentconfig(String str) {
        RabbitMqMqttUtil.environmentconfig = str;
    }

    public static void sendMqtt(String elsAccount,String message) throws IOException, TimeoutException {
        if (StringUtils.isEmpty(host)){
            throw new BusinessException("MQ服务器地址配置信息错误！");
        }
        String[] hostss = host.split(",");
        address = new Address[hostss.length];
        int index = 0;
        for(String host : hostss){
            address[index] = new Address(host, port);
            index ++;
        }
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        boolean empty = CollectionUtils.isEmpty(Arrays.asList(address));
        if (empty){
            logger.error("address值为: null");
        }else {
            logger.info("address值为: " + Arrays.toString(address));
        }
        try (Connection conn = factory.newConnection(address);
             Channel channel = conn.createChannel()) {
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    .deliveryMode(2) // 传送方式
                    .contentEncoding("UTF-8") // 编码方式
                    .expiration("10000") // 过期时间
                    .build();
            String topic = elsAccount + "_" + environmentconfig;
            channel.basicPublish("amq.topic", topic, properties, message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
