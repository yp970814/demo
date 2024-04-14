package yp970814.event.publish;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import yp970814.event.BizSendOrderEvent;

import javax.annotation.Resource;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 15:48
 */
@Slf4j
@Component
public class BizEventPublisher {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishSendOrderPost(BizSendOrderEvent event){
        log.info("【事件发布】发布过账成功事件：{}", JSON.toJSONString(event));
        applicationEventPublisher.publishEvent(event);
    }

}
