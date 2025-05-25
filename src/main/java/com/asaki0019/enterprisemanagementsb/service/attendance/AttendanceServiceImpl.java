package com.asaki0019.enterprisemanagementsb.service.attendance;

import com.asaki0019.enterprisemanagementsb.entities.attendance.AttendanceRecord;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.AttendanceStatus;
import com.asaki0019.enterprisemanagementsb.repositories.AttendanceRecordRepository;
import com.asaki0019.enterprisemanagementsb.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Override
    @Transactional
    public AttendanceRecord clockInOut(Integer employeeId, boolean isClockIn) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        
        AttendanceRecord record;
        if (isClockIn) {
            record = new AttendanceRecord();
            record.setEmployee(employee);
            record.setClockIn(now);
            record.setStatus(determineClockInStatus(now.toLocalTime()));
        } else {
            List<AttendanceRecord> todayRecords = attendanceRecordRepository.findByEmployeeAndDateBetween(
                    employee,
                    today,
                    today
            );
            record = todayRecords.stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No clock-in record found for today"));
            record.setClockOut(now);
            record.setStatus(determineClockOutStatus(record.getClockIn().toLocalTime(), now.toLocalTime()));
        }
        
        return attendanceRecordRepository.save(record);
    }

    @Override
    public List<AttendanceRecord> getEmployeeAttendanceRecords(Employee employee, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository.findByEmployeeAndDateBetween(employee, startDate, endDate);
    }

    @Override
    public Map<String, Object> getEmployeeAttendanceStats(Employee employee, String month) {
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<AttendanceRecord> records = attendanceRecordRepository.findByEmployeeAndDateBetween(
                employee, startDate, endDate);

        int totalDays = yearMonth.lengthOfMonth();
        int workDays = records.size();
        int lateCount = 0;
        int earlyLeaveCount = 0;
        double overtimeHours = 0;

        for (AttendanceRecord record : records) {
            if (record.getStatus() == AttendanceStatus.LATE) {
                lateCount++;
            } else if (record.getStatus() == AttendanceStatus.EARLY_LEAVE) {
                earlyLeaveCount++;
            }

            // 计算加班时间
            if (record.getClockOut() != null && record.getClockOut().getHour() >= 17) {
                overtimeHours += record.getClockOut().getHour() - 17 +
                        record.getClockOut().getMinute() / 60.0;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDays", totalDays);
        stats.put("workDays", workDays);
        stats.put("absenceDays", totalDays - workDays);
        stats.put("lateCount", lateCount);
        stats.put("earlyLeaveCount", earlyLeaveCount);
        stats.put("overtimeHours", overtimeHours);

        return stats;
    }

    @Override
    public Page<AttendanceRecord> getAttendanceByStatus(Employee employee, AttendanceStatus status, Pageable pageable) {
        return attendanceRecordRepository.findByEmployeeAndStatus(employee, status, pageable);
    }

    private AttendanceStatus determineClockInStatus(LocalTime clockInTime) {
        LocalTime standardStartTime = LocalTime.of(9, 0); // 9:00 AM
        return clockInTime.isAfter(standardStartTime) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
    }

    private AttendanceStatus determineClockOutStatus(LocalTime clockInTime, LocalTime clockOutTime) {
        LocalTime standardEndTime = LocalTime.of(18, 0); // 6:00 PM
        if (clockOutTime.isBefore(standardEndTime)) {
            return AttendanceStatus.EARLY_LEAVE;
        } else if (clockOutTime.isAfter(standardEndTime.plusHours(2))) {
            return AttendanceStatus.OVERTIME;
        }
        return AttendanceStatus.PRESENT;
    }
} 