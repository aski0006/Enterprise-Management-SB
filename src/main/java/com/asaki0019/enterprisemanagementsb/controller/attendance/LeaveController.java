package com.asaki0019.enterprisemanagementsb.controller.attendance;

import com.asaki0019.enterprisemanagementsb.entities.attendance.LeaveRecord;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.service.attendance.EmployeeService;
import com.asaki0019.enterprisemanagementsb.service.attendance.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveRecord leaveRecord) {
        Employee employee = employeeService.getEmployeeById(leaveRecord.getEmployee().getEmployeeId());
        leaveRecord.setEmployee(employee);
        LeaveRecord savedRecord = leaveService.applyLeave(leaveRecord);
        return ResponseEntity.ok(Map.of("success", true, "data", savedRecord));
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getLeaveApplications(@RequestParam Integer employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        List<LeaveRecord> applications = leaveService.getEmployeeLeaveRecords(employee);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", Map.of("applications", applications)
        ));
    }

    @GetMapping("/records")
    public ResponseEntity<?> getLeaveRecords(
            @RequestParam Integer employeeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        List<LeaveRecord> records = leaveService.getLeaveRecordsByDateRange(employee, startDate, endDate);
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("records", records)));
    }
} 