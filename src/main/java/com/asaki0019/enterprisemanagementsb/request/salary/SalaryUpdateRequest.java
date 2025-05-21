package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SalaryUpdateRequest {
    private String employeeId;
    private String name;
    private String department;
    private String position;
    private Double baseSalary;
    private Double performanceBonus;
    private Double allowance;
    private LocalDate effectiveDate;
    private String changeReason;
} 