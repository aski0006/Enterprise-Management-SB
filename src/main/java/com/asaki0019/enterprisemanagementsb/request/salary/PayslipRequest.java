package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

/**
 * 工资单查询请求
 */
@Data
public class PayslipRequest {

    /**
     * 员工ID（可选查询条件）
     */
    private String employeeId;
    
    /**
     * 部门ID（可选查询条件）
     */
    private String departmentId;
    
    /**
     * 年份（可选查询条件）
     */
    private Integer year;
    
    /**
     * 月份（可选查询条件，1-12）
     */
    private Integer month;
    
    /**
     * 发放状态（可选查询条件，"generated"-已生成，"distributed"-已发放）
     */
    private String status;
    
    /**
     * 当前页码，从0开始
     */
    private Integer page = 0;
    
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
} 