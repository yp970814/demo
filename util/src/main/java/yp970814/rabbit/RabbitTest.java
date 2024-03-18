package yp970814.rabbit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-25 15:37
 */
public class RabbitTest {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        // 创建一个exchange，true是exchange的持久化
        channel.exchangeDeclare("myExchange", "direct", true);
        // 创建一个queue，true是queue的持久化
        channel.queueDeclare("myQueue", true, false, false, null);
        // 绑定exchange和queue
        channel.queueBind("myQueue", "myExchange", "key");
        // 发送消息
        byte[] message = "hello world!".getBytes();
        // 消息持久化，basicProperties的deliveryMode设置成2
        channel.basicPublish("myExchange", "key", false,
                new AMQP.BasicProperties().builder()
                        .deliveryMode(2)
                        .contentType("text/plain")
                        .build(),
                message);
    }

}
