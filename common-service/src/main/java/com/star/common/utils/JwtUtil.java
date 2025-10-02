package com.star.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.star.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类 - JSON Web Token操作工具
 * 
 * JWT是一种开放标准，用于在各方之间安全地传输信息
 * JWT由三部分组成：Header.Payload.Signature
 * 
 * 主要功能：
 * 1. 生成JWT Token（用户登录时）
 * 2. 验证JWT Token（验证用户身份）
 * 3. 解析JWT Token（获取用户信息）
 * 4. 检查Token是否过期
 * 
 * 使用场景：
 * - 用户登录后生成Token
 * - API接口验证用户身份
 * - 无状态的用户认证
 * 
 * @author star
 */
@Slf4j
public class JwtUtil {

    /**
     * 生成JWT Token - 用户登录成功后调用
     * 
     * 工作流程：
     * 1. 使用HMAC256算法创建加密器
     * 2. 设置Token过期时间（7天）
     * 3. 设置JWT头部信息
     * 4. 添加标准声明（签发者、主题、受众、时间等）
     * 5. 添加自定义声明（用户ID、用户名）
     * 6. 使用密钥签名生成最终Token
     * 
     * @param userId 用户ID - 用于标识具体用户
     * @param username 用户名 - 用于显示和辅助验证
     * @return JWT Token字符串，失败返回null
     * 
     * 使用示例：
     * String token = JwtUtil.generateToken(1001L, "张三");
     * // 返回：eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     */
    public static String generateToken(Long userId, String username) {
        try {
            // 1. 创建HMAC256加密算法，使用配置的密钥
            Algorithm algorithm = Algorithm.HMAC256(CommonConstant.JWT.SECRET);
            
            // 2. 设置Token过期时间：当前时间 + 7天
            Date expireTime = new Date(System.currentTimeMillis() + CommonConstant.JWT.EXPIRE_TIME);
            
            // 3. 设置JWT头部信息（Header）
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");      // 令牌类型
            header.put("alg", "HS256");    // 签名算法
            
            // 4. 构建JWT并设置各种声明（Claims）
            return JWT.create()
                    .withHeader(header)                                    // 设置头部
                    .withIssuer("xuetu")                                  // 签发者：学途平台
                    .withSubject("user")                                  // 主题：用户相关
                    .withAudience("web")                                  // 受众：web客户端
                    .withIssuedAt(new Date())                            // 签发时间：当前时间
                    .withExpiresAt(expireTime)                           // 过期时间
                    .withClaim(CommonConstant.JWT.USER_ID_KEY, userId)    // 自定义声明：用户ID
                    .withClaim(CommonConstant.JWT.USERNAME_KEY, username) // 自定义声明：用户名
                    .sign(algorithm);                                    // 使用算法签名
        } catch (JWTCreationException e) {
            log.error("JWT生成失败", e);
            return null;
        }
    }

    /**
     * 生成JWT Token - 支持自定义Claims（用于角色权限管理）
     * 
     * 功能：支持在Token中存储自定义信息（如角色、权限等）
     * 
     * @param claims 自定义声明Map，可包含：
     *               - userId (Long): 用户ID
     *               - username (String): 用户名
     *               - roles (List<String>): 角色列表
     *               - permissions (List<String>): 权限列表
     * @param expireTime 过期时间（毫秒）
     * @return JWT Token字符串，失败返回null
     * 
     * 使用示例：
     * Map<String, Object> claims = new HashMap<>();
     * claims.put("userId", 1001L);
     * claims.put("username", "张三");
     * claims.put("roles", Arrays.asList("STUDENT", "TEACHER"));
     * claims.put("permissions", Arrays.asList("course:view", "course:create"));
     * String token = JwtUtil.generateToken(claims, 7 * 24 * 60 * 60 * 1000);
     */
    public static String generateToken(Map<String, Object> claims, long expireTime) {
        try {
            // 1. 创建HMAC256加密算法
            Algorithm algorithm = Algorithm.HMAC256(CommonConstant.JWT.SECRET);
            
            // 2. 设置Token过期时间
            Date expireDate = new Date(System.currentTimeMillis() + expireTime);
            
            // 3. 设置JWT头部信息
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            
            // 4. 构建JWT Builder
            var jwtBuilder = JWT.create()
                    .withHeader(header)
                    .withIssuer("xuetu")
                    .withSubject("user")
                    .withAudience("web")
                    .withIssuedAt(new Date())
                    .withExpiresAt(expireDate);
            
            // 5. 添加所有自定义claims
            if (claims != null && !claims.isEmpty()) {
                for (Map.Entry<String, Object> entry : claims.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    // 根据值的类型添加到JWT中
                    if (value == null) {
                        continue; // 跳过null值
                    } else if (value instanceof String) {
                        jwtBuilder.withClaim(key, (String) value);
                    } else if (value instanceof Long) {
                        jwtBuilder.withClaim(key, (Long) value);
                    } else if (value instanceof Integer) {
                        jwtBuilder.withClaim(key, (Integer) value);
                    } else if (value instanceof Boolean) {
                        jwtBuilder.withClaim(key, (Boolean) value);
                    } else if (value instanceof java.util.List) {
                        jwtBuilder.withClaim(key, (java.util.List<?>) value);
                    } else if (value instanceof java.util.Map) {
                        jwtBuilder.withClaim(key, (java.util.Map<String, ?>) value);
                    } else {
                        // 其他类型转为字符串
                        jwtBuilder.withClaim(key, value.toString());
                    }
                }
            }
            
            // 6. 签名并生成Token
            return jwtBuilder.sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("JWT生成失败", e);
            return null;
        }
    }

    /**
     * 验证JWT Token的有效性 - 每次API请求时调用
     * 
     * 验证内容：
     * 1. 签名是否正确（防止Token被篡改）
     * 2. Token是否过期
     * 3. 签发者、主题、受众是否匹配
     * 4. Token格式是否正确
     * 
     * @param token JWT Token字符串
     * @return true=Token有效，false=Token无效
     */
    public static boolean verifyToken(String token) {
        try {
            // 1. 创建验证算法（必须与生成时使用的算法一致）
            Algorithm algorithm = Algorithm.HMAC256(CommonConstant.JWT.SECRET);
            
            // 2. 构建JWT验证器，设置验证规则
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("xuetu")    // 验证签发者必须是"xuetu"
                    .withSubject("user")    // 验证主题必须是"user"
                    .withAudience("web")    // 验证受众必须是"web"
                    .build();               // 构建验证器
            
            // 3. 执行验证，如果验证失败会抛出异常
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析JWT Token
     * @param token JWT Token
     * @return 解析后的JWT
     */
    public static DecodedJWT parseToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(CommonConstant.JWT.SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("xuetu")
                    .withSubject("user")
                    .withAudience("web")
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("JWT解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        DecodedJWT decodedJWT = parseToken(token);
        if (decodedJWT != null) {
            return decodedJWT.getClaim(CommonConstant.JWT.USER_ID_KEY).asLong();
        }
        return null;
    }

    /**
     * 从Token中获取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsername(String token) {
        DecodedJWT decodedJWT = parseToken(token);
        if (decodedJWT != null) {
            return decodedJWT.getClaim(CommonConstant.JWT.USERNAME_KEY).asString();
        }
        return null;
    }

    /**
     * 检查Token是否过期
     * @param token JWT Token
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        DecodedJWT decodedJWT = parseToken(token);
        if (decodedJWT != null) {
            Date expireTime = decodedJWT.getExpiresAt();
            return expireTime.before(new Date());
        }
        return true;
    }

    /**
     * 从请求头中获取Token
     * @param authHeader Authorization请求头
     * @return JWT Token
     */
    public static String getTokenFromHeader(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(CommonConstant.JWT.TOKEN_PREFIX)) {
            return authHeader.substring(CommonConstant.JWT.TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 获取Token的剩余过期时间（秒）
     * @param token JWT Token
     * @return 剩余过期时间
     */
    public static Long getTokenExpireTime(String token) {
        DecodedJWT decodedJWT = parseToken(token);
        if (decodedJWT != null) {
            Date expireTime = decodedJWT.getExpiresAt();
            long currentTime = System.currentTimeMillis();
            long expireMillis = expireTime.getTime();
            if (expireMillis > currentTime) {
                return (expireMillis - currentTime) / 1000;
            }
        }
        return 0L;
    }

    /**
     * 刷新Token（生成新的Token）
     * @param token 原Token
     * @return 新Token
     */
    public static String refreshToken(String token) {
        Long userId = getUserId(token);
        String username = getUsername(token);
        if (userId != null && StringUtils.hasText(username)) {
            return generateToken(userId, username);
        }
        return null;
    }
}