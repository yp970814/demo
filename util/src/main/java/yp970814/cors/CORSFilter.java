package yp970814.cors;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * 跨域处理
 * @Author yuanping970814@163.com
 * @Date 2023-09-12 0:19
 */
@Slf4j
@Component
public class CORSFilter implements Filter {

    private static final String CORS_HEADER_OPTION_METHOD = "OPTIONS";

    @Value("${mes.server.origin}")
    private String mesServerOrigin;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        String origin = request.getHeader("origin");

        log.info("CORSFilter requestAddress-origin--------------->"+origin);
    /*    if (StringUtils.isBlank(origin)) {
            chain.doFilter(req, res);
            return;
        }*/
        if (StringUtils.isNotBlank(origin) && StringUtils.isNotBlank(mesServerOrigin) && !Arrays.asList(mesServerOrigin.split(",")).contains(origin)) {
            return;
        }

        String hm = ((HttpServletRequest)req).getMethod();
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-Forwarded-For, X-Auth-Token, x-auth-token, x-iac-token, spanid, x-org-id, sdp-app-session, cmes_info, traceId");
        if (CORS_HEADER_OPTION_METHOD.equals(hm)) {
            return ;
        }
        chain.doFilter(req, res);
    }
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}

}
