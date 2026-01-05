package cn.yznu.vms.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成和解析 Token
 */
@Slf4j
public class JwtUtils {

    /**
     * 默认密钥 (建议通过配置文件注入)
     */
    private static final String DEFAULT_SECRET = "VmsSpringCloudSecretKeyForJwtTokenGeneration2024";

    /**
     * 默认过期时间: 24小时 (毫秒)
     */
    private static final long DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000L;

    /**
     * Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Header 名称
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * 生成 Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param roles    角色列表
     * @return JWT Token
     */
    public static String generateToken(Long userId, String username, List<String> roles) {
        return generateToken(userId, username, roles, DEFAULT_SECRET, DEFAULT_EXPIRATION);
    }

    /**
     * 生成 Token (自定义密钥和过期时间)
     *
     * @param userId     用户ID
     * @param username   用户名
     * @param roles      角色列表
     * @param secret     密钥
     * @param expiration 过期时间(毫秒)
     * @return JWT Token
     */
    public static String generateToken(Long userId, String username, List<String> roles, String secret, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 解析 Token
     *
     * @param token  JWT Token
     * @param secret 密钥
     * @return Claims (包含用户信息)
     */
    public static Claims parseToken(String token, String secret) {
        // 移除 Bearer 前缀
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }

        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 解析 Token (使用默认密钥)
     */
    public static Claims parseToken(String token) {
        return parseToken(token, DEFAULT_SECRET);
    }

    /**
     * 从 Token 中获取用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * 从 Token 中获取用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从 Token 中获取角色列表
     */
    @SuppressWarnings("unchecked")
    public static List<String> getRoles(String token) {
        Claims claims = parseToken(token);
        return (List<String>) claims.get("roles");
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token  JWT Token
     * @param secret 密钥
     * @return true=有效, false=无效或已过期
     */
    public static boolean validateToken(String token, String secret) {
        try {
            parseToken(token, secret);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("Token 无效: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证 Token (使用默认密钥)
     */
    public static boolean validateToken(String token) {
        return validateToken(token, DEFAULT_SECRET);
    }

    /**
     * 判断 Token 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
