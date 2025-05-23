package com.asaki0019.enterprisemanagementsb.request.attendance;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceRecordRequest {
    private Long recordId;
    private Integer employeeId;
    private String employeeName;
    private Integer departmentId;  // 新增部门ID
    private String departmentName; // 新增部门名称
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private String status;
}
