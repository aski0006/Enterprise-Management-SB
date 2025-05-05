package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

import java.util.List;

/**
 * 工资单请求类
 */
@Data
public class PayslipRequest {
    
    // 员工ID，用于单个工资单操作
    private String id;
    
    // 月份，所有工资单操作必填
    private String month;
    
    // 部门，用于按部门筛选工资单
    private String department;
    
    // 工资单状态
    private String status;
    
    // 批量操作的员工ID列表
    private List<String> employeeIds;
    
    // 分页参数
    private Integer page;
    private Integer pageSize;
} 