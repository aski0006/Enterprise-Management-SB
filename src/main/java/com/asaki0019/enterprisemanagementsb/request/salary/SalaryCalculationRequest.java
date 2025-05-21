package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;

@Data
public class SalaryCalculationRequest {
    private String period;
    private String department;
}