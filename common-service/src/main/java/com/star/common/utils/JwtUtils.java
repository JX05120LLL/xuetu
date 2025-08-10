package com.star.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT工具类 - 使用Auth0的java-jwt
 * @author star
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret:xuetu-secret-key-for-jwt-token-generation}")
    private String secret;

    /**
     * JWT过期时间(秒) 默认7天
     */
    @Value("${jwt.expire:604800}")
    private Long expire;

    /**
     * JWT签发者
     */
    @Value("${jwt.issuer:xuetu}")
    private String issuer;

    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @return JWT Token
     */
    public String generateToken(Long userId) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Date now = new Date();
            Date expireDate = new Date(now.getTime() + expire * 1000);

            return JWT.create()
                    .withIssuer(issuer)                    // 签发者
                    .withSubject(userId.toString())        // 主题(用户ID)
                    .withIssuedAt(now)                     // 签发时间
                    .withExpiresAt(expireDate)             // 过期时间
                    .withClaim("userId", userId)           // 自定义声明
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("JWT生成失败: {}", e.getMessage(), e);
            throw new RuntimeException("JWT生成失败", e);
        }
    }

    /**
     * 生成JWT Token(带用户名)
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(Long userId, String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Date now = new Date();
            Date expireDate = new Date(now.getTime() + expire * 1000);

            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(userId.toString())
                    .withIssuedAt(now)
                    .withExpiresAt(expireDate)
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("JWT生成失败: {}", e.getMessage(), e);
            throw new RuntimeException("JWT生成失败", e);
        }
    }

    /**
     * 验证JWT Token
     * @param token JWT Token
     * @return 解码后的JWT
     */
    public DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("JWT验证失败: {}", e.getMessage(), e);
            throw new RuntimeException("JWT验证失败", e);
        }
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserId(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return jwt.getClaim("userId").asLong();
        } catch (Exception e) {
            log.error("从Token中获取用户ID失败: {}", e.getMessage(), e);
            throw new RuntimeException("从Token中获取用户ID失败", e);
        }
    }

    /**
     * 从Token中获取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsername(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return jwt.getClaim("username").asString();
        } catch (Exception e) {
            log.error("从Token中获取用户名失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 检查Token是否过期
     * @param token JWT Token
     * @return true: 过期, false: 未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (Exception e) {
            log.error("检查Token过期状态失败: {}", e.getMessage(), e);
            return true;
        }
    }

    /**
     * 刷新Token(重新生成)
     * @param token 原Token
     * @return 新Token
     */
    public String refreshToken(String token) {
        try {
            Long userId = getUserId(token);
            String username = getUsername(token);
            return generateToken(userId, username);
        } catch (Exception e) {
            log.error("刷新Token失败: {}", e.getMessage(), e);
            throw new RuntimeException("刷新Token失败", e);
        }
    }

    /**
     * 获取Token的剩余有效时间(秒)
     * @param token JWT Token
     * @return 剩余有效时间(秒)
     */
    public long getTokenExpireTime(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            Date expireDate = jwt.getExpiresAt();
            Date now = new Date();
            return (expireDate.getTime() - now.getTime()) / 1000;
        } catch (Exception e) {
            log.error("获取Token剩余时间失败: {}", e.getMessage(), e);
            return 0;
        }
    }
}