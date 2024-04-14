package yp970814.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 15:40
 */
public class BizSendOrderEvent extends ApplicationEvent {

    private String headId;

    public BizSendOrderEvent(Object source,String headId) {
        super(source);
        this.headId = headId;
    }

    public String getHeadId() {
        return headId;
    }

    public void setHeadId(String headId) {
        this.headId = headId;
    }
}
