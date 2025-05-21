package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

/**
 * 薪资等级请求
 */
@Data
public class SalaryGradeRequest {
    
    /**
     * 等级ID
     */
    private String id;
    
    /**
     * 等级名称
     */
    private String gradeName;
    
    /**
     * 等级级别
     */
    private Integer level;
    
    /**
     * 最低薪资
     */
    private Double minSalary;
    
    /**
     * 最高薪资
     */
    private Double maxSalary;
    
    /**
     * 所属部门
     */
    private String department;
    
    /**
     * 描述
     */
    private String description;
} 