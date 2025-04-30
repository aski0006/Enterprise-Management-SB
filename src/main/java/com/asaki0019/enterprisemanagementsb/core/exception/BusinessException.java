package com.asaki0019.enterprisemanagementsb.core.exception;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
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
}