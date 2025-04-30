package com.asaki0019.enterprisemanagementsb.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST("400", "请求参数错误"),
    UNAUTHORIZED("401", "未授权"),
    INVALID_TOKEN("401", "无效的令牌"),
    FORBIDDEN("403", "禁止访问"),
    NOT_FOUND("404", "资源不存在"),
    METHOD_NOT_ALLOWED("405", "请求方法不支持"),
    CONFLICT("409", "请求冲突"),
    GONE("410", "资源已删除"),
    TOO_MANY_REQUESTS("429", "请求次数过多"),
    PARAM_VALIDATION_ERROR("422", "参数校验错误"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误"),
    BUSINESS_ERROR("500", "业务异常"),
    NO_PERMISSION("403", "没有权限");

    private final String code;
    private final String message;

    public String getFormattedMessage(Object... args) {
        return String.format(message, args);
    }
}