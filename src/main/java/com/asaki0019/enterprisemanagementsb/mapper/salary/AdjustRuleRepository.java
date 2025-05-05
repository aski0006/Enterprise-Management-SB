package com.asaki0019.enterprisemanagementsb.mapper.salary;

import com.asaki0019.enterprisemanagementsb.entities.salary.AdjustRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdjustRuleRepository extends JpaRepository<AdjustRule, String> {
    
    Page<AdjustRule> findByRuleType(String ruleType, Pageable pageable);
    
    List<AdjustRule> findByRuleTypeAndStatus(String ruleType, String status);
    
    List<AdjustRule> findByStatus(String status);
} 