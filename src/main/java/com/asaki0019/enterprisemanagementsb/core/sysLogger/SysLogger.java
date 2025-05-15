package com.asaki0019.enterprisemanagementsb.core.sysLogger;

/**
 * 系统日志记录接口
 * 提供基本的日志记录功能
 */
public interface SysLogger {
    void info(String controller, String action, String message);

    void info(String controller, String action, String message, boolean saved);

    void warn(String controller, String action, String message);

    void debug(String controller, String action, String message);

    void error(String controller, String action, String message, Throwable e);

}