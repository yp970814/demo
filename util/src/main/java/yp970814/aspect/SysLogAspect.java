package yp970814.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-08-12 0:09
 */
@Aspect
@Component
public class SysLogAspect {

    private Logger logger = LoggerFactory.getLogger(SysLogAspect.class);

    @Pointcut("execution(* controller..*.*(..)) ")
    private void controllerLog() {}

    @Around("controllerLog()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
//        String traceId = request.getHeader("traceId");
        long start = System.currentTimeMillis();
//        OperatingUser operatingUser = CurrentContext.getCurrentOperatingUser();
        SysLogEntity operationLog = new SysLogEntity();
//        if (ObjectUtils.isNotNull(operatingUser)) {
//            operationLog.setCrtUser(operatingUser.getId());
//            operationLog.setCrtName(operatingUser.getName());
//            operationLog.setCrtTime(new Date(start));
//            operationLog.setId(UUIDUtils.get32UUID());
//            operationLog.setCrtHost(operatingUser.getHost());
//        }
        operationLog.setClassName(pjp.getTarget().getClass().getName());
        operationLog.setMethodName(pjp.getSignature().getName());
//        operationLog.setTraceId(traceId);

        Object object;
        Object[] args = pjp.getArgs();
        List<Object> listArgs = Arrays.asList(args);
        Stream<?> stream = CollectionUtils.isEmpty(listArgs) ? Stream.empty() : listArgs.stream();
        List<Object> logArgs = stream
                .filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)))
                .collect(Collectors.toList());
        //过滤后序列化无异常
        String string = JSON.toJSONString(logArgs);
        try {
            // 记录下请求内容
            logger.info("[{}],URL : {} ; HTTP_METHOD : {} ; IP : {} ; Uid:[{}], CLASS_METHOD : {} , ARGS : {} ",
//                    traceId,
                    request.getRequestURL().toString(),
                    request.getMethod(),
                    request.getRemoteAddr(),
                    operationLog.getCrtUser(),
                    pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName(),
                    string);

//            operationLog.setDetail(filterValue(string));
            object = pjp.proceed();
            operationLog.setExecuteTime((double) (System.currentTimeMillis() - start));

//            logService.log(operationLog);
        } catch (Throwable throwable) {
//            logger.error("[{}],{} has occurred exception:{}", traceId, pjp.getSignature(), throwable);
            operationLog.setExecuteTime((double) (System.currentTimeMillis() - start));
//            solveException(throwable, operationLog);
            throw throwable;
        }
        //不打印返回值
//        logger.info("[{}],{} has finished,result is\n{}", traceId, pjp.getSignature(), JSON.toJSONString(object));
        return object;
    }

}
