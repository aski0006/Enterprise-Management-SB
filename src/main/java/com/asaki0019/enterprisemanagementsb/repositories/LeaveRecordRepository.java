package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.attendance.LeaveRecord;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.asaki0019.enterprisemanagementsb.enums.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 请假记录仓库接口
 */
@Repository
public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Long> {
    
    /**
     * 根据员工查询请假记录，按开始日期降序排列
     * @param employee 员工
     * @return 请假记录列表
     */
    List<LeaveRecord> findByEmployeeOrderByStartDateDesc(Employee employee);
    
    /**
     * 根据请假类型和员工查询请假记录
     * @param leaveType 请假类型
     * @param employee 员工
     * @return 请假记录列表
     */
    List<LeaveRecord> findByLeaveTypeAndEmployee(LeaveType leaveType, Employee employee);
    
    /**
     * 根据审批状态查询请假记录
     * @param status 审批状态
     * @param pageable 分页参数
     * @return 请假记录分页数据
     */
    Page<LeaveRecord> findByStatus(ApprovalStatus status, Pageable pageable);
    
    /**
     * 根据员工和日期范围查询请假记录
     * @param employee 员工
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 请假记录列表
     */
    @Query("SELECT l FROM LeaveRecord l WHERE l.employee = :employee AND " +
            "((l.startDate BETWEEN :startDate AND :endDate) OR " +
            "(l.endDate BETWEEN :startDate AND :endDate) OR " +
            "(l.startDate <= :startDate AND l.endDate >= :endDate))")
    List<LeaveRecord> findByEmployeeAndDateRange(
            @Param("employee") Employee employee, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    /**
     * 根据部门和日期范围查询请假记录
     * @param departmentId 部门ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageable 分页参数
     * @return 请假记录分页数据
     */
    @Query("SELECT l FROM LeaveRecord l JOIN l.employee e JOIN e.department d " +
            "WHERE d.departmentId = :departmentId AND " +
            "((l.startDate BETWEEN :startDate AND :endDate) OR " +
            "(l.endDate BETWEEN :startDate AND :endDate) OR " +
            "(l.startDate <= :startDate AND l.endDate >= :endDate))")
    Page<LeaveRecord> findByDepartmentAndDateRange(
            @Param("departmentId") Integer departmentId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    /**
     * 根据审批人查询待审批的请假记录
     * @param approver 审批人
     * @param status 审批状态
     * @return 请假记录列表
     */
    List<LeaveRecord> findByApproverAndStatus(Employee approver, ApprovalStatus status);
} 