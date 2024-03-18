package yp970814.aspect;

import yp970814.annotation.ZyAuth;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import yp970814.threadLocal.ThreadLocalUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-22 16:10
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger LOG = LoggerFactory.getLogger(OperationLogAspect.class);

    /**
     * 切面所有controller层定义了url的方法
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* controller..*.*(..)) " +
            "&& (@annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "  || @annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "  || @annotation(org.springframework.web.bind.annotation.RequestMapping))")
    public Object handleControllerMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        Object response = null;
        long startTime = System.currentTimeMillis();
        try {
            response = proceedingJoinPoint.proceed();
        } catch (Exception e) {
            saveOperationLog(proceedingJoinPoint, e, startTime, System.currentTimeMillis());
            throw e;
        }
        saveOperationLog(proceedingJoinPoint, response, startTime, System.currentTimeMillis());
        return response;
    }

    /**
     * 保存操作日志
     * @param proceedingJoinPoint
     * @param result
     * @param startTime
     * @param endTime
     */
    private void saveOperationLog(ProceedingJoinPoint proceedingJoinPoint, Object result, Long startTime, Long endTime) {
        try {
            String requestTag = (String) ThreadLocalUtil.getValue("RequestTag");
            //获取线程共享对象中的用户信息
            String userAccount = "";
            String userName = "";
            Long dealerid = 0L;
            JSONObject userInfo = (JSONObject) ThreadLocalUtil.getValue("UserInfo");
            if (userInfo != null) {
                userAccount = userInfo.getString("useracc");
                userName = userInfo.getString("username");
                dealerid = userInfo.getLong("dealerid");
            }
            //解析请求数据
            String requestTime = DateUtil.format(DateUtil.calendar(startTime).getTime(), "yyyy-MM-dd HH:mm:ss.S");
            String responseTime = DateUtil.format(DateUtil.calendar(endTime).getTime(), "yyyy-MM-dd HH:mm:ss.S");
            Long invokeTime = endTime - startTime;

            //通过signature解析操作权限名称
            String module = "";
            String operation = "";
            Signature signature = proceedingJoinPoint.getSignature();
            if (signature instanceof MethodSignature) {
                ZyAuth zyAuthModule = proceedingJoinPoint.getThis().getClass().getAnnotation(ZyAuth.class);
                if (zyAuthModule != null) {
                    module = zyAuthModule.value();
                }
                ZyAuth zyAuthMethod = ((MethodSignature) signature).getMethod().getAnnotation(ZyAuth.class);
                if (zyAuthMethod != null) {
                    operation += zyAuthMethod.value();
                }
            }
            String clientIp = "";
            String url = "";
            HttpServletRequest request = (HttpServletRequest) ThreadLocalUtil.getValue("Request");
            if (request != null) {
                clientIp = getClientIpAddr(request);
                url = request.getRequestURI();
            }

            JSONArray params = new JSONArray();
            Object[] args = proceedingJoinPoint.getArgs();
            for (Object obj : args) {
                if (obj instanceof HttpServletRequest) {
                    Enumeration<String> paramNames = request.getParameterNames();
                    if (paramNames.hasMoreElements()) {
                        JSONObject param = new JSONObject();
                        while (paramNames.hasMoreElements()) {
                            String paramName = paramNames.nextElement();
                            String paramValue = request.getParameter(paramName);
                            param.put(paramName, paramValue);
                        }
                        params.add(param);
                    } else {
                        continue;
                    }
                } else if (obj instanceof HttpServletResponse) {
                    continue;
                } else {
                    params.add(JSONObject.toJSON(obj));
                }
            }
            //构造将要保存的日志数据
            JSONObject logData = new JSONObject();
            logData.put("requesttag", requestTag);
            logData.put("useraccount", userAccount);
            logData.put("dealerid", dealerid);
            logData.put("username", userName);
            logData.put("clientip", clientIp);
            logData.put("module", module);
            logData.put("operation", operation);
            logData.put("url", url);
            logData.put("signature", signature.toString());
            logData.put("request", params.toString());
            if (result instanceof Throwable) {
                //保存异常信息
                logData.put("response", "");
                logData.put("exception", ((Throwable) result).getMessage() + "\n" + ExceptionUtil.stacktraceToString((Throwable)result, -1));
            } else {
                //保存正常的响应结果
                logData.put("response", (result == null ? "" : result.toString()));
                logData.put("exception", "");
            }
            logData.put("requesttime", requestTime);
            logData.put("responsetime", responseTime);
            logData.put("invoketime", invokeTime);
            logData.put("status", (result instanceof Exception ? "2" : "1"));

            //异步将操作日志存入数据库
            ThreadUtil.execAsync(new Runnable() {
                @Override
                public void run() {
                    OperationLog log = SpringContextComponent.getBean(OperationLog.class);
                    log.insert(logData);
                }
            });

        } catch (Exception e) {
            LOG.debug("OperationLogAspect.handleControllerMethod:\n", e);
        }
    }

    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getHeader(" Proxy-Client-IP ");
        }
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getHeader(" WL-Proxy-Client-IP ");
        }
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && !"".equalsIgnoreCase(ip)) {
            if (ip.indexOf(",") > 0) {
                return ip.substring(0, ip.indexOf(",")).trim().trim();
            }
        }
        return ip;
    }

}
