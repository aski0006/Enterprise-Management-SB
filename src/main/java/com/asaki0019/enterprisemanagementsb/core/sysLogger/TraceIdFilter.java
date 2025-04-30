package com.asaki0019.enterprisemanagementsb.core.sysLogger;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 追踪ID过滤器
 * 为每个请求生成唯一的追踪ID，并将其添加到MDC和响应头中
 *
 * @author asaki0019
 * @version 1.0
 */
@Component
@Order(1)
public class TraceIdFilter implements Filter {
    /** MDC中存储追踪ID的键名 */
    private static final String TRACE_ID = "TraceID";
    /** HTTP响应头中追踪ID的键名 */
    private static final String TRACE_ID_HEADER = "X-Trace-ID";

    /**
     * 过滤器主要处理方法
     * 为每个请求生成8位的唯一追踪ID，并在请求处理完成后清理
     *
     * @param servletRequest 请求对象
     * @param servletResponse 响应对象
     * @param chain 过滤器链
     * @throws IOException IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0,8);
        try{
            MDC.put(TRACE_ID, traceId);
            ((HttpServletResponse) servletResponse).addHeader(TRACE_ID_HEADER, traceId);
            chain.doFilter(servletRequest, servletResponse);
        }finally {
            MDC.remove(TRACE_ID);
        }
    }
}