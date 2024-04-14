package yp970814.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 11:48
 */
@Getter
@Setter
@ToString
public class ElsAuthVO {

    private String elsAccount;

    private  String elsSubAccount;

    private String token;

    private String refreshToken;

    private String statusCode;

    private String message;
    /**
     *token 到期时间
     */
    private Long tokenExpires;
    /**
     * refreshtoken 到期时间
     */
    private Long refreshTokenExpires;
    /**
     * 供应商编号
     */
    private String supplierCode;

}
