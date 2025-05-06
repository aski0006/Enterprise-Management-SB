package com.asaki0019.enterprisemanagementsb.core.sysLogger.impl;

import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    /**
     * 记录信息级别的日志
     *
     * @param message 日志信息
     */
    @Override
    public void info(String message) {
        logger.info(message);
    }

    /**
     * 记录警告级别的日志
     *
     * @param message 警告信息
     */
    @Override
    public void warn(String message) {
        logger.warn(message);
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
    }

    @Override
    public void error(String message) {
        logger.error(message);
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
}