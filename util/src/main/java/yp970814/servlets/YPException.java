package yp970814.servlets;

import java.io.Serializable;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-08 11:56
 */
public class YPException extends Exception implements Serializable {


    /*错误代码，初始化为成功*/
    private int errorCode = 0;

    /**
     * 构造方法，对错误代码进行初始化
     *
     * @param errorCode 错误代码的初始值
     */
    public YPException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    //added by zhujw
    public YPException(int errorCode, String desc) {
        super(desc);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public int getError() {
        return errorCode;
    }

}
