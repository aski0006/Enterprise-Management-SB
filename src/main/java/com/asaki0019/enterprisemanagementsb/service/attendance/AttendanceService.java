package com.asaki0019.enterprisemanagementsb.service.attendance;

import com.asaki0019.enterprisemanagementsb.request.attendance.AttendanceRecordRequest;
import com.asaki0019.enterprisemanagementsb.request.attendance.TeamAttendanceQueryRequest;
import com.asaki0019.enterprisemanagementsb.enums.AttendanceStatus;

import java.util.List;
import java.util.Map;

public interface AttendanceService {
    List<AttendanceRecordRequest> getTeamAttendance(TeamAttendanceQueryRequest query);
    void markAttendanceException(Long recordId, AttendanceStatus status);
    Map<String, Object> getSimpleDepartmentStats(); // 添加新方法
}

