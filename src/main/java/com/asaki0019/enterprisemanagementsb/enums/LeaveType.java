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
    PERSONAL_LEAVE("私事假"),
    COMPENSATORY_LEAVE("调休假"),
    MARRIAGE_LEAVE("婚假"),
    ;

    private final String value;
}
