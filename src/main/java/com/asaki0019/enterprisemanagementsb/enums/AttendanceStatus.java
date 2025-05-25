package com.asaki0019.enterprisemanagementsb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceStatus {
    PRESENT("正常出勤"),
    LATE("迟到"),
    EARLY_LEAVE("早退"),
    ABSENCE("缺勤"),
    LEAVE("请假"),
    OVERTIME("加班");

    private final String description;
}
