package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdjustRuleRequest {
    
    private String id;
    
    private String ruleName;
    
    private String ruleType;
    
    private String applyTo;
    
    private LocalDate effectiveDate;
    
    private LocalDate expireDate;
    
    private String adjustRatio;  // JSON格式
    
    private String approvalFlow;  // JSON格式
    
    private String status;
    
    private String description;
} 