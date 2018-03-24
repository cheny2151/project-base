package com.cheney.utils.jwt;

import com.cheney.utils.SpringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import io.jsonwebtoken.impl.DefaultJwtParser;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.core.env.Environment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JsonWebToken工具类
 */
public class JwtUtils {

    static {
        Environment environment = SpringUtils.getBean("environment", Environment.class);
        IN_DATE = environment.getProperty("jwt.indate", int.class);
        SHA256_KEY = environment.getProperty("jwt.sha256.key");
    }

    private static final JwtBuilder JWT_BUILDER = new DefaultJwtBuilder();

    private static final JwtParser JWT_PARSER = new DefaultJwtParser();

    /**
     * 用户名key
     */
    private static final String USER_NAME_KEY = "sub";

    /**
     * 签证过期时间key
     */
    private static final String CREATE_DATE_KEY = "iat";

    /**
     * 签证有效时间(day)
     */
    private static int IN_DATE;

    /**
     * 签证key
     */
    private static String SHA256_KEY;


    /**
     * 生成token
     */
    public static String generateToken(JwtPrincipal user) {
        Date signDate = new Date();
        return JWT_BUILDER
                .setExpiration(generateExpiration(signDate))
                .setSubject(user.getUsername())
                .setIssuedAt(signDate)
                .signWith(SignatureAlgorithm.HS256, SHA256_KEY)
                .compact();
    }

    /**
     * 自定义信息
     */
    private static Map<String, Object> generateClaims(JwtPrincipal user, Date signDate) {
        HashMap<String, Object> claim = new HashMap<>();
        return claim;
    }

    private static Date generateExpiration(Date signDate) {
        return DateUtils.addDays(signDate, IN_DATE);
    }

    /**
     * token提取Claims
     */
    private static Claims parseToken(String token) {
        if (token == null) throw new NullPointerException();
        try {
            return JWT_PARSER
                    .setSigningKey(SHA256_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 提取username
     */
    public static String parseToUsername(String token) {
        Claims claims;
        return (claims = parseToken(token)) == null ? null : claims.getSubject();
    }

    /**
     * 1,token签证未过期
     * 2,用户最后一次修改密码时间为null || token签证创建时间在修改密码时间之后
     */
    public static boolean validate(String token, JwtPrincipal auth) {
        Claims claims = parseToken(token);
        return claims != null
                && claims.getExpiration().after(new Date())
                && (auth.getLastPasswordReset() == null || claims.getIssuedAt().after(auth.getLastPasswordReset()));
    }

}
