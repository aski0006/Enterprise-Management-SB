package com.asaki0019.enterprisemanagementsb.core.sysLogger.impl;

import com.asaki0019.enterprisemanagementsb.core.authContext.AuthContext;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.asaki0019.enterprisemanagementsb.entities.log.Log;
import com.asaki0019.enterprisemanagementsb.repositories.LogRepository;
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
    private final LogRepository logRepository;

    @Autowired
    public SysLoggerImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }


    private Log createLog(String controller, String action, String message) {
        var log = new Log();
        String userId = AuthContext.getUserId();
        if(userId == null) userId = "系统访问";
        log.setUserId(userId);
        log.setController(controller);
        log.setAction(action);
        log.setMessage(message);
        log.setTimestamp(new Date());
        return log;
    }

    @Override
    public void info(String controller, String action, String message) {
        logger.info(MessageConstructor.constructPlainMessage(controller, action, message));
        logRepository.save(createLog(controller, action, message));
    }

    @Override
    public void info(String controller, String action, String message, boolean saved) {
        logger.info(MessageConstructor.constructPlainMessage(controller, action, message));
        if (saved) {
            logRepository.save(createLog(controller, action, message));
        }
    }

    @Override
    public void warn(String controller, String action, String message) {
        logger.warn(MessageConstructor.constructPlainMessage(controller, action, message));
        logRepository.save(createLog(controller, action, message));
    }

    @Override
    public void debug(String controller, String action, String message) {
        logger.debug(MessageConstructor.constructPlainMessage(controller, action, message));
    }
    @Override
    public void error(String controller, String action, String message, Throwable e) {
        logger.error(MessageConstructor.constructPlainMessage(controller, action, message), e);
        logRepository.save(createLog(controller, action, message));
    }
}