package yp970814.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:20
 */
@Slf4j
@Component
public class KafkaUtils {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送kafka消息
     * @param obj
     * @param kafkaTopic
     */
    public void send(String obj, String kafkaTopic) {
        log.info("准备发送消息为：{}", obj);
        //发送消息
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(kafkaTopic, obj);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //发送失败的处理
                log.info(kafkaTopic + " - 生产者 发送消息失败：" + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                //成功的处理
                log.info(kafkaTopic + " - 生产者 发送消息成功：" + stringObjectSendResult.toString());
            }
        });
    }

}
