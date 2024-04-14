package yp970814.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import yp970814.idWorker.IdWorker;
import org.bson.Document;
import yp970814.idWorker.UUIDGenerator;
import yp970814.mongo.MongoTemplate;

import java.util.Date;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:22
 */
public class KafkaConsumerListener {

    private static final Logger LOGGER= LoggerFactory.getLogger(KafkaConsumerListener.class);

    private static final String BASE_TOPIC = "base_topic_";

    @KafkaListener(topics = BASE_TOPIC + "${KAFKA_TYPE}")
    public void onMessage(String message){
        DTO eventDTO = JSON.parseObject(message, DTO.class);
        this.kafkaSetOutPutParam(eventDTO.getUuid(),eventDTO.getValue());
    }

    @Data
    private class DTO {
        String uuid;
        String value;
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    public void kafkaSetOutPutParam(String uuid, String obj) {
        ElsDataLogVO elsDataLogVO = new ElsDataLogVO();
        elsDataLogVO.setId(IdWorker.getId());
        elsDataLogVO.setLogId(uuid);
        elsDataLogVO.setLogtime(System.currentTimeMillis());
        if (StringUtils.isNotBlank(obj)) {
            try {
                elsDataLogVO.setType("output");
                elsDataLogVO.setParam(obj);
            } catch (Exception e) {
                LOGGER.error("setOutPutParam failed:", e);
            }
        }
        //插入mongodb数据库
        JSONObject jsonLogVO = (JSONObject) JSONObject.toJSON(elsDataLogVO);
        Document document = new Document().append("id", UUIDGenerator.getUuid())
                .append("logTime", elsDataLogVO.getLogtime())
                .append("date", new Date())
                .append("loginId", uuid)
                .append("value", jsonLogVO);
        mongoTemplate.insert(document, "SYSLOG_OUTPUT");
    }

}
