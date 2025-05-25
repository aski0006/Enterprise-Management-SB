package com.asaki0019.enterprisemanagementsb.service.attendance;

import com.asaki0019.enterprisemanagementsb.entities.attendance.AttendanceRecord;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    /**
     * 员工打卡
     * @param employeeId 员工ID
     * @param isClockIn 是否是上班打卡
     * @return 打卡记录
     */
    AttendanceRecord clockInOut(Integer employeeId, boolean isClockIn);

    /**
     * 获取员工考勤记录
     * @param employee 员工
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 考勤记录列表
     */
    List<AttendanceRecord> getEmployeeAttendanceRecords(Employee employee, LocalDate startDate, LocalDate endDate);

    /**
     * 获取员工考勤统计
     * @param employee 员工
     * @param month 月份（格式：yyyy-MM）
     * @return 统计数据
     */
    Map<String, Object> getEmployeeAttendanceStats(Employee employee, String month);

    /**
     * 根据考勤状态获取记录
     * @param employee 员工
     * @param status 考勤状态
     * @param pageable 分页参数
     * @return 考勤记录分页数据
     */
    Page<AttendanceRecord> getAttendanceByStatus(Employee employee, AttendanceStatus status, Pageable pageable);
} 