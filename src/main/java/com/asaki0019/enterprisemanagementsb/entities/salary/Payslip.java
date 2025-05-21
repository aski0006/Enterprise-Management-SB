package com.asaki0019.enterprisemanagementsb.entities.salary;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "payslips")
@Data
public class Payslip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    private String month;
    private Double baseSalary;
    private Double performanceSalary;
    private Double overtimePay;
    private Double otherBonus;
    private Double totalSalary;
    
    private Double socialInsurance;
    private Double housingFund;
    private Double incomeTax;
    private Double otherDeduction;
    private Double totalDeduction;
    private Double actualSalary;
    
    private String status; // generated, distributed
    private LocalDateTime createTime;
    private LocalDateTime distributeTime;
} 