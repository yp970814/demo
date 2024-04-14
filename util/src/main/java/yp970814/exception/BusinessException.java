package yp970814.exception;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:59
 */
public class BusinessException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message,Throwable throwable) {
        super(message,throwable);
    }
}
