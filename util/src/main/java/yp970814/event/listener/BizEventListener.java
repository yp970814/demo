package yp970814.event.listener;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import yp970814.event.BizSendOrderEvent;
import yp970814.job.BizSendMaterialJob;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 15:39
 */
@Slf4j
@Component
public class BizEventListener {

    @Resource
    private BizSendMaterialJob sendMaterialJob;

//    @Resource
//    private BizSendMaterialHeadService sendMaterialHeadService;

    @Async
    @TransactionalEventListener
    public void sendOrderPostListener(BizSendOrderEvent event){
        try {
            log.info("【事件消息监听】过账成功事件监听:{}", JSON.toJSONString(event));
//            BizSendMaterialHead bizSendMaterialHead = sendMaterialHeadService.selectById(event.getHeadId());
            Map<String, Object> map = new HashMap<>();
            map.put("headId",event.getHeadId());
//            map.put("tenantId", Lists.newArrayList(bizSendMaterialHead.getTenantCode()));
            sendMaterialJob.execute(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
