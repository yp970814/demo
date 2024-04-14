package yp970814.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yp970814.event.publish.BizEventPublisher;

import javax.annotation.Resource;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 15:50
 */
@Service
@Transactional
@Slf4j
public class BizEventService {

    @Resource
    BizEventPublisher bizEventPublisher;

    public void main() {
//        this.updateSendMaterialHead(head, orderType, invOrderRequestDTO);
//        发布event事件
//        bizEventPublisher.publishSendOrderPost(new BizSendOrderEvent(this, head.getId()));
    }


}
