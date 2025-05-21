package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryAdjustRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 薪资调整规则仓库接口
 */
@Repository
public interface SalaryAdjustRuleRepository extends JpaRepository<SalaryAdjustRule, Integer> {
    
    /**
     * 根据规则类型查询薪资调整规则
     * @param ruleType 规则类型
     * @return 薪资调整规则列表
     */
    List<SalaryAdjustRule> findByRuleType(String ruleType);
    
    /**
     * 根据规则编码查询薪资调整规则
     * @param ruleCode 规则编码
     * @return 薪资调整规则
     */
    Optional<SalaryAdjustRule> findByRuleCode(String ruleCode);
    
    /**
     * 根据规则名称查询薪资调整规则
     * @param ruleName 规则名称
     * @return 薪资调整规则
     */
    Optional<SalaryAdjustRule> findByRuleName(String ruleName);
    
    /**
     * 根据适用对象查询薪资调整规则
     * @param applyTo 适用对象
     * @return 薪资调整规则列表
     */
    List<SalaryAdjustRule> findByApplyTo(String applyTo);
} 