package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BonusSchemeRequest {
    
    private String id;
    
    private String schemeName;
    
    private String department;
    
    private String year;
    
    private String formulaType;
    
    private String formulaDetail;  // JSON格式
    
    private BigDecimal totalBudget;
    
    private String distributionMethod;
    
    private String approvalStatus;
    
    private String paymentDate;
    
    private String description;
} 