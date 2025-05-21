package com.asaki0019.enterprisemanagementsb.entities.salary;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 薪资调整规则实体类
 */
@Entity
@Table(name = "salary_adjust_rule")
@Data
public class SalaryAdjustRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 规则编码
     */
    @Column(length = 20, unique = true)
    private String ruleCode;
    
    /**
     * 规则名称
     */
    @Column(length = 50, nullable = false)
    private String ruleName;
    
    /**
     * 规则类型
     * 例如：绩效调薪、晋升调薪、年度调薪等
     */
    @Column(length = 20, nullable = false)
    private String ruleType;
    
    /**
     * 适用对象
     * 例如：全员、管理层、技术人员等
     */
    @Column(length = 50, nullable = false)
    private String applyTo;
    
    /**
     * 调整比例
     * 可以存储固定比例，也可以存储JSON，表示不同级别的调整比例
     */
    @Column(length = 500, nullable = false)
    private String adjustRatio;
    
    /**
     * the type of adjustRatio
     * fixed: 固定比例
     * range: 范围比例
     */
    @Column(length = 10, nullable = false)
    private String ratioType;
    
    /**
     * 生效日期
     */
    @Column(nullable = false)
    private LocalDate effectiveDate;
    
    /**
     * 描述
     */
    @Column(length = 200)
    private String description;
} 