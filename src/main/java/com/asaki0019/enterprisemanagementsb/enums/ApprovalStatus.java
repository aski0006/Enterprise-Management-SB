package com.asaki0019.enterprisemanagementsb.enums;

import lombok.Getter;

@Getter
public enum ApprovalStatus {
    APPROVED("赞同"),
    REJECTED("拒绝"),
    PENDING("待审核");

    private String status;

    ApprovalStatus(String status) {
        this.status = status;
    }
}