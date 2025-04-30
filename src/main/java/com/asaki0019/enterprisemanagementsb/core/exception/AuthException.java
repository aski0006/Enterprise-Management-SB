package com.asaki0019.enterprisemanagementsb.core.exception;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.exception.base.BaseException;

/**
 * 认证异常类
 *
 * <p>用于处理认证过程中出现的异常情况</p>
 *
 * @author asaki0019
 * @since 1.0.0
 */
public class AuthException extends BaseException {

    /**
     * 构造认证异常
     *
     * @param errorCode 错误码
     * @param args 错误信息参数
     */
    public AuthException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}