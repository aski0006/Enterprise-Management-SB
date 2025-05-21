package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

/**
 * 员工薪资查询请求
 */
@Data
public class EmployeeSalaryRequest {

    /**
     * 员工姓名（可选查询条件）
     */
    private String name;
    
    /**
     * 部门ID（可选查询条件）
     */
    private String departmentId;
    
    /**
     * 职位ID（可选查询条件）
     */
    private String positionId;
    
    /**
     * 薪资范围下限（可选查询条件）
     */
    private Double salaryMin;
    
    /**
     * 薪资范围上限（可选查询条件）
     */
    private Double salaryMax;
    
    /**
     * 当前页码，从0开始
     */
    private Integer page = 0;
    
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
} 