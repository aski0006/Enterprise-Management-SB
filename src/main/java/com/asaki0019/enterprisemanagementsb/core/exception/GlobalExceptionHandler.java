package com.asaki0019.enterprisemanagementsb.core.exception;

import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * 全局异常处理器
 * 用于统一处理系统中的各类异常，并返回标准化的响应结果
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final SysLogger logger;

    /**
     * 构造函数
     * @param sysLogger 系统日志记录器
     */
    @Autowired
    public GlobalExceptionHandler(SysLogger sysLogger) {
        logger = sysLogger;
    }

    /**
     * 处理业务异常
     * @param e 业务异常
     * @param request HTTP请求
     * @return 统一的错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        String msg = MessageFormat.format(
                "[Business Exception] {0} - {1} | URI: {2}",
                e.getErrorCode().getCode(),
                e.getErrorCode().getFormattedMessage(e.getArgs()),
                request.getRequestURI()
        );
        return Result.failure(e.getErrorCode(), e.getArgs()).setTraceID(MDC.get("TraceID"));
    }

    /**
     * 处理认证异常
     * @param ex 认证异常
     * @return 统一的错误响应
     */
    @ExceptionHandler(AuthException.class)
    public Result<?> handleAuthException(AuthException ex) {
        String msg = MessageFormat.format(
                "[Auth Exception] {0} - {1}",
                ex.getErrorCode().getCode(),
                ex.getErrorCode().getFormattedMessage(ex.getArgs())
        );
        return Result.failure(ex.getErrorCode(), ex.getArgs()).setTraceID(MDC.get("TraceID"));
    }

    /**
     * 处理参数校验异常
     * @param ex 参数校验异常
     * @return 统一的错误响应
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<?> handleValidationException(Exception ex) {
        String errorMessage = ex instanceof MethodArgumentNotValidException ?
                Objects.requireNonNull(((MethodArgumentNotValidException) ex).getBindingResult().getFieldError()).getDefaultMessage() :
                Objects.requireNonNull(((BindException) ex).getBindingResult().getFieldError()).getDefaultMessage();
        String msg = MessageFormat.format(
                "[Validation Exception] {0}",
                errorMessage
        );
        return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, errorMessage).setTraceID(MDC.get("TraceID"));
    }

    /**
     * 处理所有未被特定处理的异常
     * @param ex 异常
     * @param request HTTP请求
     * @return 统一的错误响应
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<?> handleGlobalException(Exception ex, HttpServletRequest request) {
        String msg = MessageFormat.format(
                "[Global Exception] {0} - {1} | URI: {2}",
                ex.getClass().getName(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR).setTraceID(MDC.get("TraceID"));
    }
}