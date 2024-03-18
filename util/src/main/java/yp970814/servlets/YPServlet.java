package yp970814.servlets;

import com.alibaba.fastjson.JSONObject;
import yp970814.advice.Util;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 此Servlet，用于解释MVC注解的实现原理，主要基于反射
 * @Author yuanping970814@163.com
 * @Date 2023-11-08 11:16
 */
public class YPServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Map<String, Class<?>> actionMap = new HashMap<String, Class<?>>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println(this.getClass().getName() + " is created");
        //读取配置文件，初始化接口调用配置
        Properties prop = new Properties();
        try {
            prop.load(new Object(){}.getClass().getResourceAsStream("/app_interface.properties"));
            Enumeration<?> propertyNames = prop.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String key = (String) propertyNames.nextElement();
                String value = prop.getProperty(key);
                if (null != value) {
                    try {
                        Class<?> cls = Class.forName(value);
                        actionMap.put(key, cls);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject params = new JSONObject();
        Enumeration<?> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = req.getParameter(name);
            params.put(name, value);
        }
        execute(req, resp, params.toJSONString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        StringBuffer requestBody = new StringBuffer();
        String uri = req.getRequestURI();
//        if (uri.indexOf("bpm/bill/uploadFileTwo") != -1) {
//            JSONObject json = new JSONObject();
//            json.put("sessionToken", URLDecoder.decode(request.getParameter("sessionToken").trim(), "utf-8"));
//            execute(req, resp, json.toJSONString());
//        } else if (uri.indexOf("wechat/uri/callback") != -1) {
//            Enumeration paramNames = req.getParameterNames();
//            JSONObject params = new JSONObject();
//            while(paramNames.hasMoreElements()) {
//                String name = (String) paramNames.nextElement();
//                String value = req.getParameter(name);
//                params.put(name, value);
//            }
//            execute(req, resp, params.toJSONString());
//        }else{
            BufferedReader reader = req.getReader();
            String line = null;
            while((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            //存在request拥有readLine，但是readLine是空的情况，就导致requestBody是空
            Enumeration<?> names = req.getParameterNames();
            if (names.hasMoreElements()) {
                JSONObject params = JSONObject.parseObject(requestBody.toString());
                if (null == params) {
                    params = new JSONObject();
                }
                while(names.hasMoreElements()) {
                    String name = (String) names.nextElement();
                    String value = req.getParameter(name);
                    params.put(name, value);
                }
                execute(req, resp, params.toJSONString());
            } else {
                execute(req, resp, requestBody.toString());
            }
//        }
    }

    private void execute(HttpServletRequest request, HttpServletResponse response, String params) throws IOException {
        String uri = request.getRequestURI();
//        if (uri.indexOf("/srm/") != -1) {
//            params = params.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
//            params = URLDecoder.decode(params.trim(), "utf-8");
//        }
        System.out.println("URI:" + request.getMethod() + " " + uri);
        System.out.println("Params:" + params);
        /**
         * uri不可能为null
         * 	  /bigzone_new_cy_shake/v1/api/sys/lg/login
         * 截掉 /bigzone_new_cy_shake/v1/api
         * login 为方法名
         * sys.lg 为模块名
         */
        String[] uris = uri.split("/");
        /**
         * /webname/calss/method
         * uris.length最少有4个
         * 随着配置不断增加
         */
        if (uris.length >= 4) {
            String name3 = uris[uris.length - 1];
            String name2 = uris[uris.length - 2];
            String name1 = uris[uris.length - 3];
            StringBuilder model = new StringBuilder();
            model.append(name1).append(".").append(name2);
            System.err.println("[model]" + model.toString());
            System.err.println("[methodName]" + name3);
            Class<?> cls = actionMap.get(model.toString());
            System.err.println("[cls]" + cls.getName());
            if (null != cls) {
                Method method = null;
                try {
                    method = cls.getMethod(name3, new Class<?>[]{HttpServletRequest.class, HttpServletResponse.class, String.class});
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                if (null != method) {
                    Object returnValue = null;
                    try {
                        if (Void.class == method.getReturnType()) {
                            method.invoke(cls, new Object[] {request, response, params});
                            return;
                        } else {
                            returnValue = method.invoke(cls, new Object[] {request, response, params});
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        Throwable targetException = e.getTargetException();
                        if (targetException instanceof YPException) {
                            int errCode = ((YPException)targetException).getError();
                            String errInfo = ((YPException)targetException).getMessage();
                            if (null == errInfo || "".equals(errInfo)) {
                                errInfo = getErrInfo(errCode);
                            }
                            returnValue = "{\"message\":{\"code\":\"" + errCode + "\",\"info\":\"" + errInfo + "\"},\"status\":\"failure\"}";
                        }
                    }

                    if (null != returnValue && !"".equals(returnValue)) {
                        if (returnValue instanceof String) {
                            System.out.println("Result:" + (String) returnValue);
                            output(request, response, (String) returnValue, params);
                        } else {
                            System.out.println("Result:" + returnValue.toString());
                            output(request, response, returnValue.toString(), params);
                        }
                    } else {
                        String responseContent = "{\"message\":{\"code\":\"4\",\"info\":\"接口返回值不正确\"},\"status\":\"failure\"}";
                        output(request, response, responseContent, params);
                    }
                } else {
                    String responseContent = "{\"message\":{\"code\":\"3\",\"info\":\"接口未实现\"},\"status\":\"failure\"}";
                    output(request, response, responseContent, params);
                }
            } else {
                String responseContent = "{\"message\":{\"code\":\"2\",\"info\":\"接口配置不正确\"},\"status\":\"failure\"}";
                output(request, response, responseContent, params);
            }
        } else {
            String responseContent = "{\"message\":{\"code\":\"1\",\"info\":\"接口未定义\"},\"status\":\"failure\"}";
            output(request, response, responseContent, params);
        }
    }

    private void output(HttpServletRequest request, HttpServletResponse response, String content, String params) throws IOException {
        params = params.substring(params.indexOf("{"), params.lastIndexOf("}")+1);
        JSONObject paramJson = JSONObject.parseObject(params);
        String sessionToken = paramJson.getString("sessionToken");

        if (null == sessionToken || "".equals(sessionToken)) {

        }else{
            try{
//                logout_new(params);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        response.setContentType("text/plain;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(content);
        out.flush();
        out.close();
    }

    private String getErrInfo(int errCode) {
        return Util.errp.getProperty(String.valueOf(errCode));
    }

}
