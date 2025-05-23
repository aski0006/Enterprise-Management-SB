package com.asaki0019.enterprisemanagementsb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LeaveType {
    ANNUAL_LEAVE("年次休暇"),
    SICK_LEAVE("病欠"),
    SPECIAL_LEAVE("特別休暇"),
    MOTHER_LEAVE("事假"),
    OVERTIME("加班"),
    ;

    private final String value;
}
