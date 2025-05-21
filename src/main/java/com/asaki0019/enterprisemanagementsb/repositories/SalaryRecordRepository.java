package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryRecord;
import com.asaki0019.enterprisemanagementsb.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 薪资记录仓库接口
 */
@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {
    
    /**
     * 根据员工查询薪资记录
     * @param employee 员工
     * @return 薪资记录列表
     */
    List<SalaryRecord> findByEmployeeOrderByPayPeriodDesc(Employee employee);
    
    /**
     * 根据员工和工资周期查询薪资记录
     * @param employee 员工
     * @param payPeriod 工资周期
     * @return 薪资记录
     */
    SalaryRecord findByEmployeeAndPayPeriod(Employee employee, String payPeriod);
    
    /**
     * 根据支付状态查询薪资记录
     * @param paymentStatus 支付状态
     * @param pageable 分页参数
     * @return 薪资记录分页数据
     */
    Page<SalaryRecord> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);
    
    /**
     * 根据部门和工资周期查询薪资记录
     * @param departmentId 部门ID
     * @param payPeriod 工资周期
     * @param pageable 分页参数
     * @return 薪资记录分页数据
     */
    @Query("SELECT sr FROM SalaryRecord sr JOIN sr.employee e JOIN e.department d " +
            "WHERE d.departmentId = :departmentId AND sr.payPeriod = :payPeriod")
    Page<SalaryRecord> findByDepartmentAndPayPeriod(
            @Param("departmentId") Integer departmentId, 
            @Param("payPeriod") String payPeriod, 
            Pageable pageable);
    
    /**
     * 查询指定工资周期的所有薪资记录
     * @param payPeriod 工资周期
     * @param pageable 分页参数
     * @return 薪资记录分页数据
     */
    Page<SalaryRecord> findByPayPeriod(String payPeriod, Pageable pageable);
} 