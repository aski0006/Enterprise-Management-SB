package com.asaki0019.enterprisemanagementsb.entities.salary;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_payslip")
@Data
public class SalaryPayslip {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(128)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 128)
    private String employeeId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String department;

    @Column(nullable = false, length = 50)
    private String position;

    @Column(nullable = false, length = 10)
    private String month;

    @Column(nullable = false)
    private BigDecimal baseSalary;

    @Column(nullable = false)
    private BigDecimal performanceSalary;

    @Column(nullable = false)
    private BigDecimal overtimePay;

    @Column(nullable = false)
    private BigDecimal otherBonus;

    @Column(nullable = false)
    private BigDecimal totalSalary;

    @Column(nullable = false)
    private BigDecimal socialInsurance;

    @Column(nullable = false)
    private BigDecimal housingFund;

    @Column(nullable = false)
    private BigDecimal incomeTax;

    @Column(nullable = false)
    private BigDecimal otherDeduction;

    @Column(nullable = false)
    private BigDecimal totalDeduction;

    @Column(nullable = false)
    private BigDecimal actualSalary;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column
    private LocalDateTime distributeTime;
} 