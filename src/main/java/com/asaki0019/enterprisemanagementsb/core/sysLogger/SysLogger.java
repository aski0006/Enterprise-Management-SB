package com.asaki0019.enterprisemanagementsb.core.sysLogger;

/**
 * 系统日志记录接口
 * 提供基本的日志记录功能
 */
public interface SysLogger {
    /**
     * 记录信息级别的日志
     * @param message 日志信息
     */
    void info(String message);

    void info(String message, boolean isRecordApiLog);

    void debug(String message, Throwable e);

    /**
     * 记录警告级别的日志
     * @param message 警告信息
     */
    void warn(String message);

    /**
     * 记录错误级别的日志
     * @param message 错误信息
     * @param e 异常对象
     */
    void error(String message, Throwable e);

    void error(String message);
    /**
     * 记录调试级别的日志
     * @param message 调试信息
     */
    void debug(String message);
}