package com.yaser.news.utils;


import com.yaser.news.controller.globalHandler.APIException;
import com.yaser.news.constant.ResultCode;
import com.yaser.news.dataEntity.RecUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

/**
 * @Package com.cxyzj.cxyzjback.Utils
 * @Author Yaser
 * @Date 2018/08/09 11:13
 * @Description: Token生成工具类，使用jwt标准
 */
@Slf4j
public class JWTUtils {
    private static final long TOKEN_EXPIRATE_AT = 1000 * 3600 * 24;//过期时间
    private static final String KEY = "YASER";//加密密钥
    private static final String TOKEN_PREFIX = "Bearer ";//头部前缀
    private static final String HEADER_STRING = "Authorization";//请求头部

    /**
     * @param recUser 用于生成token的用户
     * @param hour    几小时后过期
     * @return 生成的token
     */
    public static String generateToken(RecUser recUser, int hour) {
        return Jwts.builder().
                setClaims(generateClaims(recUser)).
                setExpiration(generateExpiration(hour)).
                setIssuedAt(new Date()).setIssuer("NewsRec").
                setSubject("Token").
                setAudience("User").
                setId(UUID.randomUUID().toString()).
                signWith(SignatureAlgorithm.HS512, KEY).
                compact();
    }

    /**
     * @param recUser 用于生成Token的用户
     * @return 生成的Token
     */
    public static String generateToken(RecUser recUser) {
        return generateToken(recUser, 7);//默认七天过期
    }

    /**
     * @param hour 几小时后过期
     * @return 过期的日期时间
     */
    private static Date generateExpiration(int hour) {
        return new Date(System.currentTimeMillis() + hour * TOKEN_EXPIRATE_AT);
    }

    /**
     * @param recUser 用于生成Token的用户
     * @return 存放在Token中的私有信息
     */
    private static Claims generateClaims(RecUser recUser) {
        DefaultClaims defaultClaims = new DefaultClaims();
        defaultClaims.put("uid", recUser.getUid());
        return defaultClaims;
    }

    /**
     * 解析token信息，并将解析结果保存
     *
     * @param request 要解析的request请求
     */
    public static long ParseToken(HttpServletRequest request) throws ExpiredJwtException, SignatureException {
        log.info("-------------------------------------------Start to parse Token");
        String token = request.getHeader(HEADER_STRING);//读取请求头部
        if (token == null) throw new APIException(ResultCode.TOKEN_NOT_FOUND);
        if (token.startsWith(TOKEN_PREFIX)) {
            log.info(token);
            token = token.replace(TOKEN_PREFIX, "");//去掉前缀
            Claims claims = Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).getBody();//读取token信息
            var uid = claims.get("uid");
            if (uid != null) {
                return (long) uid;
            } else {
                return 0;
            }
        } else {
            throw new APIException(ResultCode.ILLEGAL_TOKEN);
        }
    }
}
