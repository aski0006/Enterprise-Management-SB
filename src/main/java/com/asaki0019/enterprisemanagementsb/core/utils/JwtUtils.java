package com.asaki0019.enterprisemanagementsb.core.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JWT工具类，用于处理JSON Web Token的生成和解析
 */
public class JwtUtils {
    /**
     * 使用HMAC-SHA256算法的密钥
     */
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Token的过期时间，设置为10天
     */
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    /**
     * 生成包含用户信息和权限的JWT token
     *
     * @param username 用户名
     * @param userId 用户ID
     * @param permissions 用户权限集合
     * @return 生成的JWT字符串
     */
    public static String generateToken(String username, Long userId, Set<String> permissions) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)       // 用户ID存入claim
                .claim("permissions", permissions) // 权限集合
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 验证JWT token的有效性
     *
     * @param token 待验证的token
     * @return 如果token有效返回true，否则返回false
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从token中提取用户ID
     *
     * @param token JWT token
     * @return 用户ID
     */
    public static Long extractUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /**
     * 从token中提取用户权限集合
     *
     * @param token JWT token
     * @return 用户权限集合
     */
    public static Set<String> extractPermissions(String token) {
        return new HashSet<>(parseToken(token).get("permissions", List.class));
    }

    /**
     * 解析JWT token
     *
     * @param token JWT token
     * @return Claims对象，包含token中的所有声明
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}


