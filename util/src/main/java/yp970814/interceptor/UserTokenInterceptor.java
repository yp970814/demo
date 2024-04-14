package yp970814.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import yp970814.enums.RespCodeEnum;
import yp970814.annotation.NoAuthorization;
import yp970814.json.JsonUtils;
import yp970814.jwt.ElsAuthVO;
import yp970814.jwt.ElsJwtUtil;
import yp970814.threadLocal.UserThreadLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 11:45
 */
@Slf4j
@Component
public class UserTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private ElsJwtUtil elsJwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            //校验handler是否是HandlerMethod
            if (!(handler instanceof HandlerMethod)) {
                return true;
            }
            //判断是否包含@NoAuthorization注解，如果包含，直接放行
            if (((HandlerMethod) handler).hasMethodAnnotation(NoAuthorization.class)) {
                return true;
            }
            //从请求头中获取token
            String token = request.getHeader("token");
            if(StringUtils.isNotEmpty(token)){
                ElsAuthVO user = elsJwtUtil.validToken(token);
                if(user != null){
                    //token有效
                    /*String supplierCode = supplierMasterDataService.findErpCodeByCode(defaultElsAccount,user.getElsAccount());
                    //将User对象放入到ThreadLocal中
                    if(!StringUtils.isEmpty(supplierCode)){
                        user.setSupplierCode(supplierCode);
                        user.setElsAccount(defaultElsAccount);
                    }*/
                    UserThreadLocal.set(user);
                    return true;
                }
            }
        }catch (Exception e){
            log.warn("鉴权失败：",e);
        }
        response.setContentType("application/json");
        //必须放在response.getWriter()之前，否则不生效
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(JsonUtils.toJson(R.buildNoObj(RespCodeEnum.E0500,"鉴权异常")));
        //token无效，响应状态为401
        response.setStatus(401); //无权限
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //从ThreadLocal中移除User对象
        UserThreadLocal.remove();
    }

}
