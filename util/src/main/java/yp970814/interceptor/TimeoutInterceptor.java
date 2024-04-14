package yp970814.interceptor;

import javax.servlet.http.HttpServletRequest;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import yp970814.filter.ContextFilter;

import java.util.Date;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 14:48
 */
public class TimeoutInterceptor extends AbstractPhaseInterceptor<Message> {

    private long expireTime = 1 * 60 * 1000L;

    public TimeoutInterceptor() {

        super(Phase.PRE_INVOKE);
    }

    public TimeoutInterceptor(String phase) {
        super(phase);

    }

    @Override
    public void handleMessage(Message arg0) throws Fault {

        HttpServletRequest request = ContextFilter.context.get();
        long userExpireTime = 0;
        if (request.getSession().getAttribute("expireTime") != null) {
            userExpireTime = (long)request.getSession().getAttribute("expireTime");
        }
        Date curr = new Date();
        if (userExpireTime != 0 && userExpireTime < curr.getTime()) {
            request.getSession().invalidate();
        }
        String url = request.getRequestURL().toString();
        if (url.indexOf("AuditService/getTodoList") > 0) {
            return;
        }
        if (url.indexOf("MsgService/findModuleMsg") > 0) {
            return;
        }
        request.getSession().setAttribute("expireTime", curr.getTime() + expireTime);
    }

}
