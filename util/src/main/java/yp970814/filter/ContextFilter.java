package yp970814.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 14:49
 */
@WebFilter("/rest/*")
public class ContextFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(ContextFilter.class);

    public static final ThreadLocal<HttpServletRequest> context = new ThreadLocal<HttpServletRequest>();

    public static final ThreadLocal<HttpServletResponse> responseContext = new ThreadLocal<HttpServletResponse>();

    public static final ThreadLocal<String> sessionId = new ThreadLocal<String>();

    public ContextFilter() {

    }

    public void destroy() {
        context.remove();
        responseContext.remove();
        //sessionId.remove();
    }


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.logger.info("========放入request==========");
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Allow-Methods", "*");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers", "*");

        context.set(req);
        responseContext.set(res);
        chain.doFilter(request, response);
    }

    public void init(FilterConfig fConfig) throws ServletException {

    }

}
