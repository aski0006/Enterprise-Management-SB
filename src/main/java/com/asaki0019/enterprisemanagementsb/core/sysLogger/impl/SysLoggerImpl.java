package com.asaki0019.enterprisemanagementsb.core.sysLogger.impl;

import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.entities.system.ApiLog;
import com.asaki0019.enterprisemanagementsb.repositories.ApiLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 系统日志实现类
 *
 * @author asaki0019
 * @version 1.0
 */
@Component
public class SysLoggerImpl implements SysLogger {
    /** 日志记录器 */
    private static final Logger logger = LoggerFactory.getLogger(SysLoggerImpl.class);
    
    private final ApiLogRepository apiLogRepository;

    @Autowired
    public SysLoggerImpl(ApiLogRepository apiLogRepository) {
        this.apiLogRepository = apiLogRepository;
    }

    /**
     * 记录信息级别的日志
     *
     * @param message 日志信息
     */
    @Override
    public void info(String message) {
        logger.info(message);
        recordApiLog(message, null, 200);
    }

    /**
     * 记录调试级别的日志
     *
     * @param message 调试信息
     * @param e 异常对象
     */
    @Override
    public void debug(String message, Throwable e) {
        logger.debug(message, e);
        recordApiLog(message, e, 200);
    }

    /**
     * 记录警告级别的日志
     *
     * @param message 警告信息
     */
    @Override
    public void warn(String message) {
        logger.warn(message);
        recordApiLog(message, null, 400);
    }

    /**
     * 记录错误级别的日志
     *
     * @param message 错误信息
     * @param e 异常对象
     */
    @Override
    public void error(String message, Throwable e) {
        logger.error(message, e);
        recordApiLog(message, e, 500);
    }

    @Override
    public void error(String message) {
        logger.error(message);
        recordApiLog(message, null, 500);
    }

    /**
     * 记录调试级别的日志
     *
     * @param message 调试信息
     */
    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    /**
     * 自动记录API日志到数据库
     *
     * @param message 日志信息
     * @param e 异常对象（可为null）
     * @param responseStatus 响应状态码
     */
    private void recordApiLog(String message, Throwable e, int responseStatus) {
        try {
            ApiLog apiLog = new ApiLog();
            apiLog.setApiName("Unknown API"); // 可以通过ThreadLocal或其他方式传递实际API名称
            apiLog.setRequestTime(new Date());
            apiLog.setResponseStatus(500);
            apiLog.setRequestParams("Unknown parameters"); // 可以通过ThreadLocal存储请求参数
            apiLog.setResponseBody(message);
            if (e != null) {
                apiLog.setErrorMessage(e.getMessage());
            }
            apiLog.setDurationMs(0); // 可以通过开始和结束时间计算持续时间
            apiLogRepository.save(apiLog);
        } catch (Exception ex) {
            logger.error("Failed to save API log", ex);
        }
    }
}