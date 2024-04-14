package yp970814.json;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:11
 */
@Configuration
@Slf4j
public class JsonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        ObjectMapper objectMapper = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(dateFormat))
                .setTimeZone(TimeZone.getTimeZone("GMT+8"))
                .registerModule(new JavaTimeModule());
        //设置不序列化为空的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //反序列化未知字段不报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化未知字段不报错
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        return objectMapper;
    }
}
