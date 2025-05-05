package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalaryGradeRequest {
    
    private String id;
    private String gradeName;
    private String level;
    private String title;
    private String department;
    private String positionTypes;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private BigDecimal baseSalaryRatio;
    private BigDecimal performanceRatio;
} 