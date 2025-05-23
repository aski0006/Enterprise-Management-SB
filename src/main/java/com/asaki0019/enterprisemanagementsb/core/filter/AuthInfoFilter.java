package com.asaki0019.enterprisemanagementsb.core.filter;

import com.asaki0019.enterprisemanagementsb.core.authContext.AuthContext;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * 认证信息过滤器
 * 用于处理HTTP请求中的JWT令牌认证
 */
@Component
@Order(2)
public class AuthInfoFilter extends OncePerRequestFilter {
    /** 认证头部字段名 */
    private static final String AUTH_HEADER = "Authorization";
    /** Bearer认证方案前缀 */
    private static final String BEARER_PREFIX = "Bearer ";
    /** 系统日志记录器 */
    private final SysLogger sysLogger;

    /**
     * 构造函数
     * @param sysLogger 系统日志记录器
     */
    @Autowired

    public AuthInfoFilter(SysLogger sysLogger) {
        this.sysLogger = sysLogger;
    }

    /**
     * 执行过滤器逻辑
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param chain 过滤器链
     * @throws IOException IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        try {
            String authHeader = request.getHeader(AUTH_HEADER);
            if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.substring(BEARER_PREFIX.length());
                sysLogger.info("AuthInfoFilter.doFilterInternal", "doFilterInternal: token = ", token);
                if (JwtUtils.validateToken(token)) {
                    Claims claims = JwtUtils.parseToken(token);

                    AuthContext.setUserId(claims.get("userId", String.class));
                    AuthContext.setPermissions(
                            new HashSet<>(claims.get("permissions", List.class))
                    );
                }
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        } finally {
            AuthContext.clear(); // 确保在请求处理完毕后清理
        }
    }
   }

