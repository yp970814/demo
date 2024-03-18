package yp970814.advice;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-11 23:08
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody
    public JSONObject handlerException(HttpServletResponse response, Exception e){
        JSONObject resultObj = new JSONObject();
        e.printStackTrace();
        if(e instanceof BZException){
            BZException bzException = (BZException)e;
            Util.setFailureResponse(resultObj, bzException.getCode(), bzException.getMsg());
            return resultObj;
        }else{
            Util.setFailureResponse(resultObj, "-1", e.getMessage());
            return resultObj;
        }
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public Object otherExceptionHandler(HttpServletResponse response, Exception ex) {
        response.setStatus(500);
        log.error(ex.getMessage());
        ex.printStackTrace();
        return Util.getServerErrResult(response);
    }

}
