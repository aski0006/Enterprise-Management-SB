package com.asaki0019.enterprisemanagementsb.request.attendance;

import com.asaki0019.enterprisemanagementsb.enums.ApprovalAction;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
@Data
public class ApprovalRequestRequest {
    @NotNull
    private Long recordId;

    @NotNull
    @JsonProperty("action")  // 更明确的字段名
    private ApprovalAction action;  // 使用新定义的枚举

    private String comments;

    }
