package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

/**
 * 工资单生成请求
 */
@Data
public class PayslipGenerateRequest {
    /**
     * 月份(格式: YYYY-MM)
     */
    private String month;
    
    /**
     * 部门
     */
    private String department;
} 