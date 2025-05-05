package com.asaki0019.enterprisemanagementsb.mapper.salary;

import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryCalculation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryCalculationRepository extends JpaRepository<SalaryCalculation, String> {
    
    Page<SalaryCalculation> findByPeriodAndDepartment(String period, String department, Pageable pageable);
    
    Page<SalaryCalculation> findByPeriod(String period, Pageable pageable);
    
    List<SalaryCalculation> findByEmployeeIdAndPeriod(String employeeId, String period);
    
    List<SalaryCalculation> findByPeriodAndStatus(String period, String status);
    
    Page<SalaryCalculation> findByPeriodAndDepartmentAndStatus(String period, String department, String status, Pageable pageable);
    
    long countByPeriodAndDepartmentAndStatus(String period, String department, String status);
} 