package com.asaki0019.enterprisemanagementsb.core.exception;

import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.exception.base.BaseException;

/**
 * 业务异常类
 * <p>
 * 用于处理业务逻辑中的异常情况
 * </p>
 *
 * @author asaki0019
 * @since 1.0.0
 */
public class BusinessException extends BaseException {

    /**
     * 构造业务异常
     *
     * @param errorCode 错误码
     * @param args 错误参数
     */
    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    /**
     * 构造业务异常（使用默认业务错误码）
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(ErrorCode.BUSINESS_ERROR, message);
    }

    /**
     * 构造业务异常
     *
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BusinessException(String message, Throwable cause) {
        // 由于BaseException没有直接支持cause的构造方法，
        // 这里需要先调用父类构造方法设置错误信息
        super(ErrorCode.BUSINESS_ERROR, message);
        // 然后设置cause
        initCause(cause);
    }

    /**
     * 构造业务异常
     *
     * @param errorCode 错误码
     * @param message 错误消息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 构造业务异常
     *
     * @param errorCode 错误码
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message);
        initCause(cause);
    }
}