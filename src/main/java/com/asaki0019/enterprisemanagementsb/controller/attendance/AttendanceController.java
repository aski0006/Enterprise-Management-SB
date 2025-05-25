package com.asaki0019.enterprisemanagementsb.controller.attendance;

import com.asaki0019.enterprisemanagementsb.entities.attendance.AttendanceRecord;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.service.attendance.AttendanceService;
import com.asaki0019.enterprisemanagementsb.service.attendance.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance/employee")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/clockin")
    public ResponseEntity<?> clockIn(@RequestParam Integer employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        AttendanceRecord record = attendanceService.clockInOut(employeeId, true);
        return ResponseEntity.ok(Map.of("success", true, "data", record));
    }

    @PostMapping("/clockout")
    public ResponseEntity<?> clockOut(@RequestParam Integer employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        AttendanceRecord record = attendanceService.clockInOut(employeeId, false);
        return ResponseEntity.ok(Map.of("success", true, "data", record));
    }

    @GetMapping("/records")
    public ResponseEntity<?> getEmployeeRecords(
            @RequestParam Integer employeeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        List<AttendanceRecord> records = attendanceService.getEmployeeAttendanceRecords(employee, startDate, endDate);
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("records", records)));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getEmployeeStats(
            @RequestParam Integer employeeId,
            @RequestParam String month) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        Map<String, Object> stats = attendanceService.getEmployeeAttendanceStats(employee, month);
        return ResponseEntity.ok(Map.of("success", true, "data", stats));
    }
} 