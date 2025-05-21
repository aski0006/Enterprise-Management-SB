package com.asaki0019.enterprisemanagementsb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemType {
    BONUS("奖金"),
    SALARY("工资"),
    ALLOWANCE("津贴"),
    DEDUCTION("扣除"),
    EARNING("收入"),
    OTHER("其他");;

    private final String name;

}
