package com.xuetu.gateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 网关专用JWT工具类
 * 
 * 职责：
 * 1. 验证JWT Token有效性
 * 2. 解析Token获取用户信息
 * 3. 检查Token是否过期
 * 
 * 注意：网关只负责验证，不负责生成Token
 * 
 * @author star
 */
@Slf4j
public class JwtUtil {

    /**
     * JWT密钥 - 必须与用户服务中的密钥一致
     */
    private static final String JWT_SECRET = "xuetu_education_platform_secret_key";
    
    /**
     * Token前缀
     */
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 验证JWT Token是否有效
     * 
     * 验证内容：
     * 1. 签名是否正确
     * 2. Token是否过期
     * 3. 格式是否正确
     * 
     * @param token JWT Token
     * @return 是否有效
     */
    public static boolean verifyToken(String token) {
        try {
            // 创建验证器
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("xuetu")      // 验证签发者
                    .withSubject("user")      // 验证主题
                    .withAudience("web")      // 验证受众
                    .build();
            
            // 执行验证
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.warn("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析JWT Token（不验证签名，仅解析）
     * 
     * @param token JWT Token
     * @return 解析后的JWT对象
     */
    public static DecodedJWT parseToken(String token) {
        try {
            return JWT.decode(token);
        } catch (Exception e) {
            log.error("JWT解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     * 
     * @param token JWT Token
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        DecodedJWT decodedJWT = parseToken(token);
        if (decodedJWT != null) {
            return decodedJWT.getClaim("userId").asLong();
        }
        return null;
    }

    /**
     * 从Token中获取用户名
     * 
     * @param token JWT Token  
     * @return 用户名
     */
    public static String getUsername(String token) {
        DecodedJWT decodedJWT = parseToken(token);
        if (decodedJWT != null) {
            return decodedJWT.getClaim("username").asString();
        }
        return null;
    }

    /**
     * 检查Token是否过期
     * 
     * @param token JWT Token
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        DecodedJWT decodedJWT = parseToken(token);
        if (decodedJWT != null) {
            Date expireTime = decodedJWT.getExpiresAt();
            return expireTime != null && expireTime.before(new Date());
        }
        return true;
    }

    /**
     * 从请求头中提取Token
     * 
     * @param authHeader Authorization请求头的值
     * @return 提取的Token，如果格式不正确返回null
     */
    public static String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 检查Token格式是否正确
     * 
     * @param token Token字符串
     * @return 格式是否正确
     */
    public static boolean isValidFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        // JWT格式检查：应该有两个点分隔三部分
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
}