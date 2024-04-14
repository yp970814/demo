package yp970814.interceptor;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 14:42
 */
public class IpAddressInInterceptor extends AbstractPhaseInterceptor<Message> {

    public IpAddressInInterceptor() {
        super(Phase.RECEIVE);
    }

    public void handleMessage(Message message) throws Fault {
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        // 通过一个IpAddressConfig对象，从XML文件中读取预先设置的允许和拒绝的IP地址，这些值也可以来自数据库

        List<String> allowedList=new ArrayList<String>();
        List<String> deniedList=new ArrayList<String>();

        // IpAddressConfig config = IpAddressConfig.getInstance(); // 获取config实例
        //  List<String> allowedList = config.getAllowedList(); // 允许访问的IP地址
        //  List<String> deniedList = config.getDeniedList(); // 拒绝访问的IP地址
        String ipAddress = request.getRemoteAddr(); // 取客户端IP地址
        // 先处理拒绝访问的地址
        for (String deniedIpAddress : deniedList) {
            if (deniedIpAddress.equals(ipAddress)) {
                throw new Fault(new IllegalAccessException("IP address " + ipAddress + " is denied"));
            }
        }
        // 如果允许访问的集合非空，继续处理，否则认为全部IP地址均合法
        if (allowedList.size() > 0) {
            boolean contains = false;
            for (String allowedIpAddress : allowedList) {
                if (allowedIpAddress.equals(ipAddress)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                throw new Fault(new IllegalAccessException("IP address " + ipAddress + " is not allowed"));
            }
        }
    }

}
