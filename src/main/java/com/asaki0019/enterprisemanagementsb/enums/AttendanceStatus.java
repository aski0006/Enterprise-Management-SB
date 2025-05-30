package com.asaki0019.enterprisemanagementsb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceStatus {
    LATE("迟到"),
    EARLY_LEAVE("早退"),
    OVERTIME("加班"),
    LEAVE("休暇"),
    UNKNOWN("未知");
    ;

    private final String status;


}
