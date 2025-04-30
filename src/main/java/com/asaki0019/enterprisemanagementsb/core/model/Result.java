package com.asaki0019.enterprisemanagementsb.core.model;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.MDC;

/**
 * 统一响应结果类
 * @param <T> 响应数据的泛型类型
 */
@Data
@Accessors(chain = true)
public class Result<T> {
    /**
     * 响应状态码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 追踪ID
     */
    private String TraceID;

    /**
     * 创建成功响应结果
     * @param data 响应数据
     * @param <T> 响应数据类型
     * @return 成功的响应结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = "200";
        result.message = "success";
        result.data = data;
        result.setTraceID(MDC.get("TraceID"));
        return result;
    }

    /**
     * 创建失败响应结果
     * @param errorCode 错误码枚举
     * @param args 错误消息格式化参数
     * @return 失败的响应结果
     */
    public static Result<?> failure(ErrorCode errorCode, Object... args) {
        return new Result<>()
                .setCode(errorCode.getCode())
                .setMessage(errorCode.getFormattedMessage(args))
                .setTraceID(MDC.get("TraceID"));
    }
}