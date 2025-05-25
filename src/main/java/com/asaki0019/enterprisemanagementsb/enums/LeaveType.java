package com.asaki0019.enterprisemanagementsb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LeaveType {
    ANNUAL_LEAVE("年假"),
    SICK_LEAVE("病假"),
    PERSONAL_LEAVE("事假"),
    MARRIAGE_LEAVE("婚假"),
    MATERNITY_LEAVE("产假"),
    BEREAVEMENT_LEAVE("丧假"),
    COMPENSATORY_LEAVE("调休");

    private final String description;
}
