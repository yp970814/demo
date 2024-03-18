package yp970814.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-25 16:31
 */
@Slf4j
public class RabbitConfig implements RabbitTemplate.ConfirmCallback{
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("=====Broker消息确认回调======");
        if (ack) {
            log.info("消息id为: " + correlationData.getId() + "的消息，已经被ack成功");
        } else {
            log.info("消息id为: " + correlationData.getId() + "的消息，消息nack，失败原因是：" + cause);
        }
    }

    @Value("${spring.rabbitmq.listener.simple.concurrency}")
    private Integer concurrency;

    @Value("${spring.rabbitmq.listener.simple.max-concurrency}")
    private Integer maxConcurrency;

    @Value("${inv.callback.queueName:INV_CALLBACK_QUEUE}")
    public String invCallbackQueue;

    @Value("${qms.iqc.inspection.result.queueName:wms_query_qms_inspection_results}")
    public String iqcCompleteQueue;

    @Value("${biz.diassemble.queue:wms.biz.diassemble.  queue}")
    public String diassembleQueue;

    private final static String IQC_TOPIC = "qms_app.iqc.topic";
    private final static String IQC_COMPLETE_TOPIC_ROUTING_KEY = "qms_app.iqc.complete";

    @Bean(name = "rabbitAdmin")
    public RabbitAdmin rabbitAdmin(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean(name = "connectionFactory")
    public ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.addresses}") String host,
                                               @Value("${spring.rabbitmq.port}") int port,
                                               @Value("${spring.rabbitmq.username}") String username,
                                               @Value("${spring.rabbitmq.password}") String password,
                                               @Value("${spring.rabbitmq.virtual-host}") String vhost) {
        return constructorConnectionFactory(host, port, username, password, vhost);
    }

    @Bean(name="rabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);
        return rabbitTemplate;
    }

    @Bean(name="simpleRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, @Qualifier("connectionFactory")ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(concurrency);  //设置核心线程数
        factory.setMaxConcurrentConsumers(maxConcurrency); //最大线程数
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); //消费手工确认
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public Queue invCallbackQueue(@Qualifier("rabbitAdmin") RabbitAdmin rabbitAdmin) {
        log.info("create callBack Queue, queueName:{}", invCallbackQueue);
        Queue queue = new Queue(invCallbackQueue, Boolean.TRUE);
        queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    @Bean(name = "diassembleQueue")
    public Queue diassembleQueue(@Qualifier("rabbitAdmin") RabbitAdmin rabbitAdmin) {
        log.info("create wip topic, queueName:{}", diassembleQueue);
        Queue queue = new Queue(diassembleQueue, Boolean.TRUE);
        queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    private CachingConnectionFactory constructorConnectionFactory(String host, Integer port, String username, String password, String vhost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

}
