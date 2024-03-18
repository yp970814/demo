package yp970814.advice;

import static yp970814.advice.Util.errp;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-11 23:36
 */
public class BZException extends RuntimeException {

    private int code;
    private String msg;

    public BZException(){
        super();
    }

    public BZException(int code, Throwable e) {
        super(e);
        this.code =code;
        String errMsg = errp.getProperty(String.valueOf(code));
        if (errMsg == null || "".equalsIgnoreCase(errMsg)) {
            this.msg = "";
        } else {
            this.msg = errMsg;
        }
    }

    public BZException(int code, String msg, Throwable e) {
        super(msg, e);
        this.code = code;
        this.msg = msg;
    }

    public BZException(int code, String... fmtstr) {
        this.code =code;
        String errMsg = errp.getProperty(String.valueOf(code));
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
        this.msg = errMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
