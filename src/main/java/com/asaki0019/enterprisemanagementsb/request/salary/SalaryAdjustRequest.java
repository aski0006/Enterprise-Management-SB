package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

/**
 * 薪资调整请求
 */
@Data
public class SalaryAdjustRequest {
    /**
     * 员工ID
     */
    private String employeeId;
    
    /**
     * 调整项目
     */
    private String item;
    
    /**
     * 调整值
     */
    private Double value;
    
    /**
     * 调整原因
     */
    private String reason;
} 