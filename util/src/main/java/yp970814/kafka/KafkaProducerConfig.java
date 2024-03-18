package yp970814.kafka;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-23 20:38
 */
@EnableKafka
@Configuration
@ConditionalOnProperty(name = "kafka.config", havingValue = "1", matchIfMissing = true)
public class KafkaProducerConfig {

    @Value("${kafka.bootstrap.servers}")
    private String servers;
    @Value("${kafka.avro.schema.url}")
    private String avroSchemaUrl;

    @Bean("kafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<String, String>(producerFactory());
        return kafkaTemplate;
    }

    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        properties.put(ProducerConfig.RETRIES_CONFIG, 0); //重试次数
        properties.put(ProducerConfig.ACKS_CONFIG, "1"); //应答级别:多少个分区副本备份完成时向生产者发送ack确认(可选0、1、all/-1)
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); //批量大小
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 0); //提交延时 当生产端积累的消息达到batch-size或接收到消息linger.ms后,生产者就会将消息提交给kafka linger.ms为0表示每接收到一条消息就提交给kafka,这时候batch-size其实就没用了
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); //生产端缓冲区大小
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class); //Kafka提供的序列化类
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);//Kafka提供的序列化类
        properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, avroSchemaUrl);
        return new DefaultKafkaProducerFactory<String, String>(properties);
    }
}
