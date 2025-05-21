package com.asaki0019.enterprisemanagementsb.entities.performance;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 绩效等级实体类
 */
@Entity
@Table(name = "performance_level")
@Data
public class PerformanceLevel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 等级
     * 例如：A, B, C, D, E
     */
    @Column(length = 2, nullable = false)
    private String level;
    
    /**
     * 等级名称
     * 例如：卓越、优秀、良好、需改进、不合格
     */
    @Column(length = 20, nullable = false)
    private String name;
    
    /**
     * 最低分数
     */
    @Column(nullable = false)
    private Integer minScore;
    
    /**
     * 最高分数
     */
    @Column(nullable = false)
    private Integer maxScore;
    
    /**
     * 奖金系数
     */
    @Column(nullable = false)
    private Double bonusRatio;
    
    /**
     * 描述
     */
    @Column(length = 200)
    private String description;
} 