package com.asaki0019.enterprisemanagementsb.entities.performance;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 绩效评估实体类
 */
@Entity
@Table(name = "performance_evaluation")
@Data
public class PerformanceEvaluation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 评估编码
     */
    @Column(length = 20, unique = true)
    private String evaluationCode;
    
    /**
     * 员工
     */
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    /**
     * 年份
     */
    @Column(length = 4, nullable = false)
    private String year;
    
    /**
     * 季度
     */
    @Column(length = 2, nullable = false)
    private Integer quarter;
    
    /**
     * 评估周期
     */
    @Column(length = 50, nullable = false)
    private String evaluationPeriod;
    
    /**
     * 评分
     */
    @Column(nullable = false)
    private Double score;
    
    /**
     * 等级
     */
    @Column(length = 2, nullable = false)
    private String level;
    
    /**
     * 状态
     * 草稿、待确认、已确认、已归档
     */
    @Column(length = 20, nullable = false)
    private String status;
    
    /**
     * 评估人
     */
    @Column(length = 50, nullable = false)
    private String evaluator;
    
    /**
     * 评估日期
     */
    @Column(nullable = false)
    private LocalDate evaluationDate;
    
    /**
     * 确认日期
     */
    @Column
    private LocalDate confirmDate;
    
    /**
     * 管理者评语
     */
    @Column(length = 500)
    private String managerComments;
    
    /**
     * 员工评语
     */
    @Column(length = 500)
    private String employeeComments;
    
    /**
     * 改进计划
     */
    @Column(length = 500)
    private String improvementPlan;
    
    /**
     * 下一阶段目标
     * 存储为JSON格式
     */
    @Column(length = 1000)
    private String nextGoals;
    
    /**
     * 评估详情
     * 存储为JSON格式，包含各个评估项目及其得分
     */
    @Column(columnDefinition = "TEXT")
    private String evaluationDetails;
} 