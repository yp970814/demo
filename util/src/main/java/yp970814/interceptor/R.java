package yp970814.interceptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import yp970814.enums.RespCodeEnum;

import java.io.Serializable;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:17
 */
@Getter
@Setter
@NoArgsConstructor
@ApiModel
@ToString
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1838685052549340628L;
    /**
     * 返回码
     */
    @ApiModelProperty(value = "处理结果编码", required = true)
    private RespCodeEnum code;

    /**
     * 返回描述
     */
    @ApiModelProperty(value = "处理结果描述信息")
    private String msg;

    /**
     * 返回对象
     */
    @ApiModelProperty(value = "处理结果数据信息")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T obj;

    public R(RespCodeEnum code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(RespCodeEnum code, String msg, T obj) {
        this.code = code;
        this.msg = msg;
        this.obj = obj;
    }

    /**
     * 没有返回数据
     *
     * @param code
     * @param msg
     * @return
     */
    public static <T> R<T>  buildNoObj(RespCodeEnum code, String msg) {
        return new R<>(code, msg);
    }


    /**
     * 没有返回数据
     *
     * @param code
     * @return
     */
    public static <T> R<T> buildNoObj(RespCodeEnum code) {
        return new R<>(code, code.getDisplayName());
    }


    /**
     * 初始化返回参数
     *
     * @return
     */
    public static <T> R<T> build(RespCodeEnum code, String msg, T t) {
        return new R<>(code, msg, t);
    }


    /**
     * 初始化成功返回的参数
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> R<T> buildSuccess( T obj) {
        return new R<>(RespCodeEnum.S0000, RespCodeEnum.S0000.getDisplayName(), obj);
    }


    /**
     * 检查是否成功
     *
     * @return
     */
    public boolean checkSuccess() {
        return RespCodeEnum.S0000.equals(this.code);
    }
}
