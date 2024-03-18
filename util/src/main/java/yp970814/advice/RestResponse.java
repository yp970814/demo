package yp970814.advice;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-11 23:59
 */
@Setter
public class RestResponse {
    private String status;
    private String message;
    private Object data;
}
