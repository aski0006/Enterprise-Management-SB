package com.asaki0019.enterprisemanagementsb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 错误码枚举类，定义系统中使用的各种错误代码及描述。
 */
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
    NO_PERMISSION("403", "没有权限"),
    EXIST_USER("500", "用户已存在" ),
    PASSWORD_NOT_MATCH("500", "密码不匹配" ),
    NOT_EXIST_USER("500", "用户不存在" ),
    NOT_EXIST_ROLE("500", "角色不存在"); // 新增的角色不存在错误码

    private final String code;
    private final String message;

    public String getFormattedMessage(Object... args) {
        return String.format(message, args);
    }
}