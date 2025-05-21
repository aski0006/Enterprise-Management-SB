package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.salary.Payslip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工资单仓库
 */
@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    
    /**
     * 根据员工和月份查询工资单
     */
    Payslip findByEmployeeAndMonth(Employee employee, String month);
    
    /**
     * 根据员工查询工资单，并按月份降序排序
     */
    List<Payslip> findByEmployeeOrderByMonthDesc(Employee employee);
    
    /**
     * 根据月份查询工资单，并按员工排序
     */
    @Query("SELECT p FROM Payslip p WHERE p.month = :month ORDER BY p.employee.id")
    Page<Payslip> findByMonthOrderByEmployee(@Param("month") String month, Pageable pageable);
    
    /**
     * 根据状态查询工资单，并按月份排序
     */
    @Query("SELECT p FROM Payslip p WHERE p.status = :status ORDER BY p.month")
    Page<Payslip> findByStatusOrderByMonth(@Param("status") String status, Pageable pageable);
    
    /**
     * 根据部门和月份查询工资单
     */
    @Query("SELECT p FROM Payslip p WHERE p.employee.department.name = :department AND p.month = :month")
    Page<Payslip> findByDepartmentAndMonth(@Param("department") String department, @Param("month") String month, Pageable pageable);
    
    /**
     * 根据月份和状态查询工资单
     */
    @Query("SELECT p FROM Payslip p WHERE p.month = :month AND p.status = :status")
    List<Payslip> findByMonthAndStatus(@Param("month") String month, @Param("status") String status);
    
    /**
     * 根据部门、月份和状态查询工资单
     */
    @Query("SELECT p FROM Payslip p WHERE p.employee.department.name = :department AND p.month = :month AND p.status = :status")
    List<Payslip> findByDepartmentAndMonthAndStatus(@Param("department") String department, @Param("month") String month, @Param("status") String status);
} 