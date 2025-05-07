package com.asaki0019.enterprisemanagementsb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PAID("支付"),
    CANCELED("取消"),
    FAILED("失敗"),
    PENDING("保留"),
    NOT_PAID("未支付");

    private final String status;

}
