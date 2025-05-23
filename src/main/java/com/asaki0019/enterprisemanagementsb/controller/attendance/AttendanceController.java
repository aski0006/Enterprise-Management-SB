package com.asaki0019.enterprisemanagementsb.controller.attendance;

import com.asaki0019.enterprisemanagementsb.request.attendance.AttendanceRecordRequest;
import com.asaki0019.enterprisemanagementsb.request.attendance.TeamAttendanceQueryRequest;
import com.asaki0019.enterprisemanagementsb.enums.AttendanceStatus;
import com.asaki0019.enterprisemanagementsb.service.attendance.AttendanceServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceServiceImpl attendanceServiceImpl;

    public AttendanceController(AttendanceServiceImpl attendanceServiceImpl) {
        this.attendanceServiceImpl = attendanceServiceImpl;
    }

    @GetMapping("/team")
    public ResponseEntity<List<AttendanceRecordRequest>> getTeamAttendance(
            @RequestParam(required = false) Long recordId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime clockIn,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime clockOut,
            @RequestParam(required = false) AttendanceStatus status,
            @RequestParam(required = false) Long employeeId) {

        TeamAttendanceQueryRequest query = new TeamAttendanceQueryRequest();
        query.setRecordId(recordId);
        query.setClockIn(clockIn);
        query.setClockOut(clockOut);
        query.setStatus(status);
        query.setEmployeeId(employeeId);

        List<AttendanceRecordRequest> records = attendanceServiceImpl.getTeamAttendance(query);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/department-stats")
    public ResponseEntity<Map<String, Object>> getDepartmentStats() {
        Map<String, Object> stats = attendanceServiceImpl.getSimpleDepartmentStats();
        return ResponseEntity.ok(stats);
    }
}