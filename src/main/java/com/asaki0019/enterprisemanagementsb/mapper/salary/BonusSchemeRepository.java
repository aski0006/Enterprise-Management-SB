package com.asaki0019.enterprisemanagementsb.mapper.salary;

import com.asaki0019.enterprisemanagementsb.entities.salary.BonusScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonusSchemeRepository extends JpaRepository<BonusScheme, String> {
    
    Page<BonusScheme> findByDepartmentAndYear(String department, String year, Pageable pageable);
    
    Page<BonusScheme> findByDepartment(String department, Pageable pageable);
    
    Page<BonusScheme> findByYear(String year, Pageable pageable);
    
    List<BonusScheme> findByApprovalStatus(String approvalStatus);
} 