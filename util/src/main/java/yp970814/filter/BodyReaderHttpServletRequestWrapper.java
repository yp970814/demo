package yp970814.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * ServletRequest中getReader()和getInputStream()只能调用一次的解决方法
 * @Author yuanping970814@163.com
 * @Date 2024-03-13 9:29
 */
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;
    private String bodyMessage;

    public byte[] getBody() {
        return body;
    }
    /**
     * 把处理后的参数放到body里面
     * @param body
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = readBytes(request.getReader(), "UTF-8");
        bodyMessage =  new String(body,"utf-8");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    public byte[] readBytes(BufferedReader bufferedReader, String charset) throws IOException{
        StringBuffer sb = new StringBuffer();
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            sb.append(s);
        }
        if(sb.length() == 0){
            return "".getBytes(charset);
        }
        return sb.toString().getBytes(charset);
    }

}
