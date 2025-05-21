package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryChangeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 薪资变更历史仓库
 */
public interface SalaryChangeHistoryRepository extends JpaRepository<SalaryChangeHistory, Long> {
    
    /**
     * 查询员工的薪资变更历史
     * @param employee 员工
     * @return 变更历史列表
     */
    List<SalaryChangeHistory> findByEmployeeOrderByChangeDateDesc(Employee employee);
    
    /**
     * 查询员工在指定时间段内的薪资变更历史
     * @param employee 员工
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 变更历史列表
     */
    List<SalaryChangeHistory> findByEmployeeAndChangeDateBetweenOrderByChangeDateDesc(
            Employee employee, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 分页查询薪资变更历史
     * @param pageable 分页参数
     * @return 变更历史分页数据
     */
    Page<SalaryChangeHistory> findAllByOrderByChangeDateDesc(Pageable pageable);
} 