package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 薪资等级请求参数
 * 所有必填字段的验证将在Service层进行
 */
@Data
public class SalaryGradeRequest {
    
    private String id;
    private String gradeName;    // 等级名称，必填
    private String level;        // 级别，必填
    private String title;        // 标题，可选（如果为空将自动生成）
    private String department;   // 部门，必填
    private String positionTypes; // 职位类型，可选
    private BigDecimal minSalary; // 最低薪资，必填，必须大于0
    private BigDecimal maxSalary; // 最高薪资，必填，必须大于0且大于最低薪资
    private BigDecimal baseSalaryRatio; // 基本工资比例，必填，必须大于0
    private BigDecimal performanceRatio; // 绩效比例，必填，必须大于0
} 