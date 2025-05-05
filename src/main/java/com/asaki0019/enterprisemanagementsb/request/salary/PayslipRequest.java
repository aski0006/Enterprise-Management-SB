package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

@Data
public class PayslipRequest {
    
    private String id;
    
    private String month;
    
    private String department;
    
    private String status;
} 