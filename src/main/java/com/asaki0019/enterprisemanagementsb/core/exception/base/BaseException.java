package com.asaki0019.enterprisemanagementsb.core.exception.base;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import lombok.Getter;

/**
 * 基础异常类
 * 所有自定义异常的父类
 */
@Getter
public class BaseException extends RuntimeException{
    /**
     * 错误码
     */
    private final ErrorCode errorCode;

    /**
     * 错误参数
     */
    private final Object[] args;

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param args 错误参数
     */
    public BaseException(ErrorCode errorCode, Object... args) {
        super(errorCode.getFormattedMessage(args));
        this.errorCode = errorCode;
        this.args = args;
    }
}