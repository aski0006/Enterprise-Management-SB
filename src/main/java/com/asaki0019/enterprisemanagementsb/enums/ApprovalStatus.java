package com.asaki0019.enterprisemanagementsb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprovalStatus {
    APPROVED("赞同"),
    REJECTED("拒絕"),
    PENDING("待审核");

    private String status;

    ApprovalStatus(String status) {
        this.status = status;
    }

}