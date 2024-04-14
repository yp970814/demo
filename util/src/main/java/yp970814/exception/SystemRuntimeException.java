package yp970814.exception;

import yp970814.enums.RespCodeEnum;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 11:59
 */
public class SystemRuntimeException extends RuntimeException{

    private RespCodeEnum respType;

    public SystemRuntimeException(String message) {
        super(message);
    }

    public SystemRuntimeException(String message, Throwable cause) {
        super(message,cause);
    }

    public SystemRuntimeException(String message, RespCodeEnum respType) {
        super(message);
        this.respType = respType;
    }

    public SystemRuntimeException(RespCodeEnum respType,String message) {
        super(message);
        this.respType = respType;
    }

    public SystemRuntimeException(RespCodeEnum respType) {
        super(respType.getDisplayName());
        this.respType = respType;
    }

    public SystemRuntimeException(String message, Throwable cause, RespCodeEnum respType) {
        super(message, cause);
        this.respType = respType;
    }
    public SystemRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, RespCodeEnum respType) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.respType = respType;
    }

    public RespCodeEnum getRespType() {
        return respType;
    }

    public void setRespType(RespCodeEnum respType) {
        this.respType = respType;
    }
}
