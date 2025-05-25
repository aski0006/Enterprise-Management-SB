package com.asaki0019.enterprisemanagementsb.service.attendance;

import com.asaki0019.enterprisemanagementsb.entities.attendance.LeaveRecord;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.asaki0019.enterprisemanagementsb.enums.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LeaveService {
    /**
     * 申请请假
     * @param leaveRecord 请假记录
     * @return 请假记录
     */
    LeaveRecord applyLeave(LeaveRecord leaveRecord);

    /**
     * 获取员工的请假记录
     * @param employee 员工
     * @return 请假记录列表
     */
    List<LeaveRecord> getEmployeeLeaveRecords(Employee employee);

    /**
     * 根据请假类型获取请假记录
     * @param leaveType 请假类型
     * @param employee 员工
     * @return 请假记录列表
     */
    List<LeaveRecord> getLeaveRecordsByType(LeaveType leaveType, Employee employee);

    /**
     * 根据状态获取请假记录
     * @param status 审批状态
     * @param pageable 分页参数
     * @return 请假记录分页数据
     */
    Page<LeaveRecord> getLeaveRecordsByStatus(ApprovalStatus status, Pageable pageable);

    /**
     * 获取指定日期范围内的请假记录
     * @param employee 员工
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 请假记录列表
     */
    List<LeaveRecord> getLeaveRecordsByDateRange(Employee employee, LocalDate startDate, LocalDate endDate);
} 