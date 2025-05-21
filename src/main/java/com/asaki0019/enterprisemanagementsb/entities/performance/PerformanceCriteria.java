package com.asaki0019.enterprisemanagementsb.entities.performance;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 绩效评估标准实体类
 */
@Entity
@Table(name = "performance_criteria")
@Data
public class PerformanceCriteria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 标准名称
     * 例如：工作表现、目标达成、团队协作等
     */
    @Column(length = 50, nullable = false)
    private String name;
    
    /**
     * 权重
     */
    @Column(nullable = false)
    private Double weight;
    
    /**
     * 适用部门
     * 如果为null或空，表示适用于所有部门
     */
    @Column(length = 50)
    private String department;
    
    /**
     * 适用职位
     * 如果为null或空，表示适用于所有职位
     */
    @Column(length = 50)
    private String position;
    
    /**
     * 评估项目
     * 存储为JSON格式，包含评估项目名称、权重、描述等
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String items;
    
    /**
     * 描述
     */
    @Column(length = 200)
    private String description;
} 