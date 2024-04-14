package yp970814.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.auth0.jwt.algorithms.Algorithm;
import yp970814.enums.RespCodeEnum;
import yp970814.exception.SystemRuntimeException;
import yp970814.interceptor.SystemConstant;
//import com.alibaba.druid.filter.config.ConfigTools;
import java.util.Date;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 11:50
 */
@Slf4j
@Component
public class ElsJwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.issuer}")
    private String jwtIssuer;
    @Value("${jwt.expires}")
    private String jwtExpires;
    @Value("${jwt.refresh.expires}")
    private String jwtRefreshExpires;

    /*private static final long TOKEN_EXPIRES_TIME = ElsSysPropertisUtil.getLong(SystemConstant.JWT_EXPIRES)*1000L;

	private static final long REFRESH_TOKEN_EXPIRES_TIME = TOKEN_EXPIRES_TIME*2;*/

    private static final long TOLERANCE_TIME = 30*1000L;

    public ElsAuthVO initAuth(String elsAccount, String elsSubAccount) throws Exception{
        try {
            String key = getStringAndDecrypt(jwtSecret);
            Algorithm ALGORITHM= Algorithm.HMAC256(key);
            //账号
            StringBuilder account = new StringBuilder(elsAccount);
            account.append("_").append(elsSubAccount);
            //token超时时间
            long tokenExpires = System.currentTimeMillis()+Long.valueOf(jwtExpires);
            long refreshTokenExpires = System.currentTimeMillis()+Long.valueOf(jwtRefreshExpires);
            //token
            String token = JWT.create()
                    .withIssuer(jwtIssuer)
                    //.withKeyId(IdWorker.getId()+"")
                    .withExpiresAt(new Date(tokenExpires))
                    .withClaim(SystemConstant.JWT_ACCOUNT,account.toString())
//                    .withClaim("name",infoVO.getName())
                    .sign(ALGORITHM);
            //refreshToken
            String refreshToken = JWT.create()
                    .withIssuer(jwtIssuer)
                    //.withKeyId(IdWorker.getId()+"")
                    .withExpiresAt(new Date(refreshTokenExpires))
                    .withClaim(SystemConstant.JWT_ACCOUNT,account.toString())
                    .sign(ALGORITHM);
            ElsAuthVO authVO = new ElsAuthVO();
            authVO.setElsAccount(elsAccount);
            authVO.setElsSubAccount(elsSubAccount);
            authVO.setToken(token);
            authVO.setRefreshToken(refreshToken);
            authVO.setTokenExpires(tokenExpires-TOLERANCE_TIME);
            authVO.setRefreshTokenExpires(refreshTokenExpires-TOLERANCE_TIME);
//            authVO.setStatusCode(RespCodeEnum.S0000.getValue());
            return authVO;
        } catch (Exception e) {
            log.error("ElsJwtUtil.generateToken",e);
            throw e;
        }
    }

    public ElsAuthVO validToken(String token){
        try {
            String key = getStringAndDecrypt(jwtSecret);
            Algorithm ALGORITHM= Algorithm.HMAC256(key);
            JWTVerifier JWT_VERIFIER= JWT.require(ALGORITHM).withIssuer(jwtIssuer).build();
            DecodedJWT decodedJWT = JWT_VERIFIER.verify(token);
            Claim claimAccount = decodedJWT.getClaim(SystemConstant.JWT_ACCOUNT);
            if(claimAccount == null) {
                log.warn("认证失败！未获取到对应账号信息！");
                throw new SystemRuntimeException("认证失败！未获取到对应账号信息！", RespCodeEnum.A0301);
            }
            String claimAct = claimAccount.asString();
            String[] actArr = claimAct.split("_");
            ElsAuthVO elsAuthVO = new ElsAuthVO();
            elsAuthVO.setElsAccount(actArr[0]);
            elsAuthVO.setElsSubAccount(actArr[1]);
            return elsAuthVO;
        } catch (Exception e) {
            log.error("ElsJwtUtil.validToken",e);
            throw new SystemRuntimeException("认证失败！未获取到对应账号信息！",RespCodeEnum.A0301);
        }
    }

    public static void main(String[] args) throws Exception {
        String key = getStringAndDecrypt("abcdefg");
        String elsAccount = "0000";
        String elsSubAccount = "0000";
        String jwtExpires = "86400";
        String jwtRefreshExpires = "172800";
        String jwtIssuer = "xxx";
        Algorithm ALGORITHM= Algorithm.HMAC256(key);
        //账号
        StringBuilder account = new StringBuilder(elsAccount);
        account.append("_").append(elsSubAccount);
        //token超时时间
        long tokenExpires = System.currentTimeMillis()+Long.valueOf(jwtExpires);
        long refreshTokenExpires = System.currentTimeMillis()+Long.valueOf(jwtRefreshExpires);
        //token
        String token = JWT.create()
                .withIssuer(jwtIssuer)
                //.withKeyId(IdWorker.getId()+"")
                .withExpiresAt(new Date(tokenExpires))
                .withClaim(SystemConstant.JWT_ACCOUNT,account.toString())
                //.withClaim("name",infoVO.getName())
                .sign(ALGORITHM);
        log.info(token);
        //refreshToken
        String refreshToken = JWT.create()
                .withIssuer(jwtIssuer)
                //.withKeyId(IdWorker.getId()+"")
                .withExpiresAt(new Date(refreshTokenExpires))
                .withClaim(SystemConstant.JWT_ACCOUNT,account.toString())
                .sign(ALGORITHM);
        log.info(refreshToken);
        token = "abcdefg";
        //验证
        JWTVerifier JWT_VERIFIER= JWT.require(ALGORITHM).withIssuer(jwtIssuer).build();
        DecodedJWT decodedJWT = JWT_VERIFIER.verify(token);
        Claim claimAccount = decodedJWT.getClaim(SystemConstant.JWT_ACCOUNT);
        if(claimAccount == null) {
            log.warn("认证失败！未获取到对应账号信息！");
            throw new SystemRuntimeException("认证失败！未获取到对应账号信息！",RespCodeEnum.A0301);
        }
        String claimAct = claimAccount.asString();
        String[] actArr = claimAct.split("_");
        ElsAuthVO elsAuthVO = new ElsAuthVO();
        elsAuthVO.setElsAccount(actArr[0]);
        elsAuthVO.setElsSubAccount(actArr[1]);
        log.info("解析：{}",elsAuthVO);
    }

    public static String getStringAndDecrypt(String result) {
        try {
            log.info("jwtSecret:{}",result);
//            return ConfigTools.decrypt(result);
            return result;
        } catch (Exception e) {
            log.error("ElsSysPropertisUtil.getStringAndDecrypt failed:",e);
            return result;
        }
    }

}
