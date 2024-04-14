package yp970814.enums;

/**
 * 返回枚举
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:51
 */
public enum ResultEnum {
    /**
     * 成功
     */
    DEFAULT_RESULT_SUCCESS("200", "OK"),
    /**
     * 失败
     */
    DEFAULT_RESULT_FAIL("-100", "操作失败,请重试或联系管理员"),
    /**
     * 业务失败
     */
    BUSINESS_RESULT_FAIL("-101", "fail"),

    BUSINESS_RESULT_BYX_FAIL("-102", "fail"),

    /**
     * response fail
     */
    ERROR("-200","错误信息！"),
    LOGIN_AUDIT_FAIL("401","登录验证失败，请重新登录！"),
    NO_PERMISSION_OPT("403","没有该项操作权限！请联系管理员！"),
    DATA_FORMAT_ERROR("407","发出的数据格式不正确,请确认"),
    MISSING_PARAMETERS("408","参数缺失"),
    ACTIVITI_FAIL("409","流程操作失败:"),
    OPERATION_FAIL("500","操作失败,请重试或联系管理员"),
    /**
     * 你没有登录或会话超时
     */
    NO_LOGIN("-101","你没有登录或会话超时，请重新登陆"),
    INTEFACE_ERROR("E","错误信息！"),
    INTEFACE_SUCCESS("S","response OK");



    private final String code;
    private final String message;

    ResultEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
