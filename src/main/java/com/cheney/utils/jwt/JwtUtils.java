package com.cheney.utils.jwt;

import com.cheney.utils.SpringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import io.jsonwebtoken.impl.DefaultJwtParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JsonWebToken工具类
 */
@Slf4j
public class JwtUtils {

    static {
        Environment environment = SpringUtils.getBean("environment", Environment.class);
        IN_DATE = environment.getRequiredProperty("jwt.indate", int.class);
        SHA256_KEY = environment.getProperty("jwt.sha256.key");
    }

    private static final JwtBuilder JWT_BUILDER = new DefaultJwtBuilder();

    private static final JwtParser JWT_PARSER = new DefaultJwtParser();

    /**
     * 签证有效时间(day)
     */
    public static int IN_DATE;

    /**
     * 签证key
     */
    private static String SHA256_KEY;

    private String token;

    private Claims claims;

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
    public static JwtUtils parseToken(String token) {
        if (token == null) {
            throw new NullPointerException();
        }
        try {
            Claims claims = JWT_PARSER
                    .setSigningKey(SHA256_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            JwtUtils jwtUtils = new JwtUtils();
            jwtUtils.setToken(token);
            jwtUtils.setClaims(claims);
            return jwtUtils;
        } catch (Exception e) {
            log.error("解析token失败,e:", e);
            return null;
        }
    }

    /**
     * 提取username
     */
    public String username() {
        return claims == null ? null : claims.getSubject();
    }

    /**
     * token签证有效并未过期
     */
    public boolean validate() {
        return claims != null
                && !StringUtils.isEmpty(claims.getSubject())
                && claims.getExpiration() != null
                && claims.getExpiration().after(new Date());
    }

    public Claims getClaims() {
        return claims;
    }

    private void setClaims(Claims claims) {
        this.claims = claims;
    }

    private void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
