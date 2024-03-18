package yp970814.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-11 23:55
 */
@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    SERVER_ERROR("50000", "服务器内部错误，请联系管理员处理!!"),
    DUPLICATE_ERROR("10001", "数据重复"),
    REQUEST_CONFIRM("10003", "请确认！"),;
    ;
    private String code;
    private String name;
}
