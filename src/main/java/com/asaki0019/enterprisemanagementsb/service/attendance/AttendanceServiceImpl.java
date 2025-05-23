package com.asaki0019.enterprisemanagementsb.service.attendance;

import com.asaki0019.enterprisemanagementsb.request.attendance.AttendanceRecordRequest;
import com.asaki0019.enterprisemanagementsb.request.attendance.TeamAttendanceQueryRequest;
import com.asaki0019.enterprisemanagementsb.entities.attendance.AttendanceRecord;
import com.asaki0019.enterprisemanagementsb.enums.AttendanceStatus;
import com.asaki0019.enterprisemanagementsb.repositories.AttendanceRecordRepository;
import com.asaki0019.enterprisemanagementsb.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<AttendanceRecordRequest> getTeamAttendance(TeamAttendanceQueryRequest query) {
        List<AttendanceRecord> records;
        
        if (query.getRecordId() != null) {
            records = List.of(attendanceRecordRepository.findById(query.getRecordId())
                    .orElseThrow(() -> new RuntimeException("考勤记录不存在")));
        } else if (query.getEmployeeId() != null) {
            records = attendanceRecordRepository.findByEmployee_EmployeeIdAndClockInBetween(
                    query.getEmployeeId().intValue(),
                    query.getClockIn() != null ? query.getClockIn() : LocalDateTime.MIN,
                    query.getClockOut() != null ? query.getClockOut() : LocalDateTime.MAX
            );
        } else {
            records = attendanceRecordRepository.findAll();
        }

        // 如果指定了状态，进行过滤
        if (query.getStatus() != null) {
            records = records.stream()
                    .filter(record -> record.getStatus() == query.getStatus())
                    .collect(Collectors.toList());
        }

        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void markAttendanceException(Long recordId, AttendanceStatus status) {
        AttendanceRecord record = attendanceRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("考勤记录不存在"));

        record.setStatus(status);
        attendanceRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSimpleDepartmentStats() {
        List<AttendanceRecord> records = attendanceRecordRepository.findAll();

        // 使用部门名称直接作为分组键，避免二次映射
        return records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getEmployee().getDepartment().getName(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                deptRecords -> {
                                    int total = deptRecords.size();
                                    Map<AttendanceStatus, Long> statusCounts = deptRecords.stream()
                                            .collect(Collectors.groupingBy(
                                                    AttendanceRecord::getStatus,
                                                    Collectors.counting()
                                            ));

                                    double overtimeHours = deptRecords.stream()
                                            .filter(r -> r.getClockOut() != null)
                                            .mapToDouble(r -> {
                                                double hours = Duration.between(r.getClockIn(), r.getClockOut()).toHours();
                                                return Math.max(hours - 8, 0); // 计算加班时长
                                            })
                                            .sum();

                                    Map<String, Object> stats = new HashMap<>();
                                    stats.put("presentRate", calculatePresentRate(statusCounts, total));
                                    stats.put("lateCount", statusCounts.getOrDefault(AttendanceStatus.LATE, 0L));
                                    stats.put("earlyLeaveCount", statusCounts.getOrDefault(AttendanceStatus.EARLY_LEAVE, 0L));
                                    stats.put("absenceCount", statusCounts.getOrDefault(AttendanceStatus.ABSENCE, 0L));
                                    stats.put("overtimeHours", overtimeHours);
                                    return stats;
                                }
                        )
                ));
    }

    private double calculatePresentRate(Map<AttendanceStatus, Long> statusCounts, int total) {
        if (total == 0) return 0;
        long present = statusCounts.getOrDefault(AttendanceStatus.NORMAL, 0L)
                + statusCounts.getOrDefault(AttendanceStatus.OVERTIME, 0L);
        return (present * 100.0) / total;
    }

    private AttendanceRecordRequest convertToDTO(AttendanceRecord record) {
        try {
            AttendanceRecordRequest dto = new AttendanceRecordRequest();
            dto.setRecordId(record.getRecordId());
            dto.setEmployeeId(record.getEmployee().getEmployeeId());
            dto.setEmployeeName(record.getEmployee().getFirstName() + " " + record.getEmployee().getLastName());

            // 添加空指针检查
            if (record.getEmployee().getDepartment() != null) {
                dto.setDepartmentId(record.getEmployee().getDepartment().getDepartmentId());
                dto.setDepartmentName(record.getEmployee().getDepartment().getName());
            }

            dto.setClockIn(record.getClockIn());
            dto.setClockOut(record.getClockOut());
            dto.setStatus(record.getStatus() != null ? record.getStatus().name() : "UNKNOWN");
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("转换考勤记录DTO失败", e);
        }
    }
}
