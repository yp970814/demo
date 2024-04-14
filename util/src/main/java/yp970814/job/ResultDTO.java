package yp970814.job;

import lombok.Data;
import yp970814.enums.ResultEnum;

import java.io.Serializable;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:50
 */
@Data
public class ResultDTO<T> implements Serializable {

    private static final long serialVersionUID = 130L;

    /**
     * 状态
     */
    private String code;
    /**
     * 消息
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;

    /**
     * 系统默认成功返回
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResultDTO<T> resultSuccess(T data) {
        ResultDTO<T> dto = new ResultDTO<T>();
        dto.setData(data);
        dto.setCode(ResultEnum.DEFAULT_RESULT_SUCCESS.getCode());
        dto.setMessage(ResultEnum.DEFAULT_RESULT_SUCCESS.getMessage());
        return dto;
    }

    /**
     * 系统默认失败返回
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResultDTO<T> resultDefaultFail(T data) {
        ResultDTO<T> dto = new ResultDTO<T>();
        dto.setData(data);
        dto.setCode(ResultEnum.DEFAULT_RESULT_FAIL.getCode());
        dto.setMessage(ResultEnum.DEFAULT_RESULT_FAIL.getMessage());
        return dto;
    }

    /**
     * 自定义失败返回
     *
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResultDTO<T> resultFail(String code, String message, T data) {
        ResultDTO<T> dto = new ResultDTO<>();
        dto.setData(data);
        dto.setCode(code);
        dto.setMessage(message);
        return dto;
    }

}
