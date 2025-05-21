package com.asaki0019.enterprisemanagementsb.entities.salary;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 薪资等级实体类
 */
@Entity
@Table(name = "salary_grade")
@Data
public class SalaryGrade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 等级编码
     */
    @Column(length = 20, unique = true)
    private String gradeCode;
    
    /**
     * 等级名称
     */
    @Column(length = 50, nullable = false)
    private String gradeName;
    
    /**
     * 等级级别
     */
    @Column(nullable = false)
    private Integer level;
    
    /**
     * 最低薪资
     */
    @Column(nullable = false)
    private Double minSalary;
    
    /**
     * 最高薪资
     */
    @Column(nullable = false)
    private Double maxSalary;
    
    /**
     * 所属部门
     */
    @Column(length = 50, nullable = false)
    private String department;
    
    /**
     * 描述
     */
    @Column(length = 200)
    private String description;
} 