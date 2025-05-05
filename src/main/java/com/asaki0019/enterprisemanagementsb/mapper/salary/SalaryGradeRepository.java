package com.asaki0019.enterprisemanagementsb.mapper.salary;

import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryGrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryGradeRepository extends JpaRepository<SalaryGrade, String> {
    
    Page<SalaryGrade> findByDepartmentOrderByGradeNameAscLevelAsc(String department, Pageable pageable);
    
    Page<SalaryGrade> findAllByOrderByGradeNameAscLevelAsc(Pageable pageable);
    
    List<SalaryGrade> findByDepartment(String department);
} 