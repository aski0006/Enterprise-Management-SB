package com.asaki0019.enterprisemanagementsb.request.attendance;

import com.asaki0019.enterprisemanagementsb.enums.AttendanceStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeamAttendanceQueryRequest {
    private Long recordId;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private AttendanceStatus status;
    private Long employeeId;
}