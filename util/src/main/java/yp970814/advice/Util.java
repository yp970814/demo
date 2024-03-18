package yp970814.advice;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-11 23:42
 */
public class Util {

    public static Properties errp = new Properties();
    static {
        try {
            errp.load(new InputStreamReader(new Object() {}.getClass().getResourceAsStream("/error.properties"), "UTF-8"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void setFailureResponse(JSONObject jo, int errcode, String... fmtstr) {
        jo.put("status", "failure");
        String errMsg = errp.getProperty(String.valueOf(errcode));
        if (errMsg == null || "".equalsIgnoreCase(errMsg)) {
            if (fmtstr.length == 1) {
                errMsg = fmtstr[0];
            } else {
                errMsg = "";
                for (String msg : fmtstr) {
                    errMsg += msg + "\n";
                }
            }
        } else {
            errMsg = String.format(errMsg, fmtstr);
        }
        JSONObject jmess = new JSONObject().fluentPut("code", String.valueOf(errcode)).fluentPut("info", errMsg);
        jo.put("message", jmess);
    }

    public static void setFailureResponse(JSONObject jo, String errcode, String msg) {
        jo.put("status", "failure");
        JSONObject jmess = new JSONObject().fluentPut("code", errcode).fluentPut("info", msg);
        jo.put("message", jmess);
    }

    public static RestResponse getServerErrResult(HttpServletResponse response) {
        response.setStatus(500);
        return buildResult(ExceptionEnum.SERVER_ERROR.getName(),
                ExceptionEnum.SERVER_ERROR.getCode());
    }

    private static RestResponse buildResult(String msg, String status) {
        return buildResult(msg, status, null);
    }

    private static RestResponse buildResult(String msg, String status, Object data) {
        RestResponse restResponse = new RestResponse();
        restResponse.setMessage(msg);
        restResponse.setStatus(status);
        restResponse.setData(data);
        return restResponse;
    }

}
