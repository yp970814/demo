package yp970814.kafka;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-23 18:11
 */
@EnableKafka
@Configuration
@ConditionalOnProperty(name = "kafka.config", havingValue = "1", matchIfMissing = true)
public class KafkaConsumerConfig {

    @Value("${kafka.send.order.over.topic}")
    public String sendOrderOverDetailTopic;

    @Value("${kafka.bootstrap.servers}")
    private String servers;
    @Value("${kafka.avro.schema.url}")
    private String avroSchemaUrl;
    @Value("${kafka.group.id}")
    private String groupId;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); //是否自动提交offset
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "3000"); //提交offset延时(接收到消息后多久提交offset)
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "120000");
        properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "180000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroDeserializer.class);
        properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, avroSchemaUrl); //AVRO数据结构注册中心地址
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); //默认的消费组ID
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); //当kafka中没有初始offset或offset超出范围时将自动重置offset
        return new DefaultKafkaConsumerFactory<String, String>(properties);
    }
}
