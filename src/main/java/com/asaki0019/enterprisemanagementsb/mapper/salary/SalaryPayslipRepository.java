package com.asaki0019.enterprisemanagementsb.mapper.salary;

import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryPayslip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryPayslipRepository extends JpaRepository<SalaryPayslip, String> {
    
    Page<SalaryPayslip> findByMonthAndDepartmentAndStatus(String month, String department, String status, Pageable pageable);
    
    Page<SalaryPayslip> findByMonthAndDepartment(String month, String department, Pageable pageable);
    
    Page<SalaryPayslip> findByMonthAndStatus(String month, String status, Pageable pageable);
    
    Page<SalaryPayslip> findByMonth(String month, Pageable pageable);
    
    List<SalaryPayslip> findByEmployeeIdAndMonth(String employeeId, String month);
    
    long countByMonthAndDepartmentAndStatus(String month, String department, String status);
} 