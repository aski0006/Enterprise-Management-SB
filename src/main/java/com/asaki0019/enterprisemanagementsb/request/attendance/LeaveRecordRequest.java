package com.asaki0019.enterprisemanagementsb.request.attendance;
import com.asaki0019.enterprisemanagementsb.enums.LeaveType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRecordRequest {
    private Long leaveId;
    private Integer employeeId;
    private String employeeName;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer approverId;
    private String approverName;
}
