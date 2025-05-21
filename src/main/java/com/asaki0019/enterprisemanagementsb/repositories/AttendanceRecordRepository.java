package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.attendance.AttendanceRecord;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考勤记录仓库接口
 */
@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    
    /**
     * 根据员工ID和日期范围查询考勤记录
     * @param employee 员工
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 考勤记录列表
     */
    @Query("SELECT a FROM AttendanceRecord a WHERE a.employee = :employee AND DATE(a.clockIn) BETWEEN :startDate AND :endDate ORDER BY a.clockIn DESC")
    List<AttendanceRecord> findByEmployeeAndDateBetween(
            @Param("employee") Employee employee, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    /**
     * 根据员工ID和状态查询考勤记录
     * @param employee 员工
     * @param status 考勤状态
     * @param pageable 分页参数
     * @return 考勤记录分页数据
     */
    Page<AttendanceRecord> findByEmployeeAndStatus(Employee employee, AttendanceStatus status, Pageable pageable);
    
    /**
     * 根据日期查询考勤记录
     * @param date 日期
     * @param pageable 分页参数
     * @return 考勤记录分页数据
     */
    @Query("SELECT a FROM AttendanceRecord a WHERE DATE(a.clockIn) = :date")
    Page<AttendanceRecord> findByDate(@Param("date") LocalDate date, Pageable pageable);
    
    /**
     * 根据部门查询考勤记录
     * @param departmentId 部门ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageable 分页参数
     * @return 考勤记录分页数据
     */
    @Query("SELECT a FROM AttendanceRecord a JOIN a.employee e JOIN e.department d " +
            "WHERE d.departmentId = :departmentId AND DATE(a.clockIn) BETWEEN :startDate AND :endDate")
    Page<AttendanceRecord> findByDepartmentAndDateBetween(
            @Param("departmentId") Integer departmentId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate, 
            Pageable pageable);
    
    /**
     * 统计员工在指定月份的考勤状态数量
     * @param employee 员工
     * @param status 考勤状态
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 考勤记录数量
     */
    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.employee = :employee AND a.status = :status AND a.clockIn BETWEEN :startDateTime AND :endDateTime")
    long countByEmployeeAndStatusAndPeriod(
            @Param("employee") Employee employee, 
            @Param("status") AttendanceStatus status, 
            @Param("startDateTime") LocalDateTime startDateTime, 
            @Param("endDateTime") LocalDateTime endDateTime);
} 