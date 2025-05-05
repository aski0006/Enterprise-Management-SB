package com.asaki0019.enterprisemanagementsb.entities.salary;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_calculation")
@Data
public class SalaryCalculation {
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
    private String period;

    @Column(nullable = false)
    private BigDecimal baseSalary;

    @Column(nullable = false)
    private BigDecimal performanceBonus;

    @Column(nullable = false)
    private BigDecimal overtimePay;

    @Column(nullable = false)
    private BigDecimal allowance;

    @Column(nullable = false)
    private BigDecimal insurance;

    @Column(nullable = false)
    private BigDecimal tax;

    @Column(nullable = false)
    private BigDecimal deduction;

    @Column(nullable = false)
    private BigDecimal totalSalary;

    @Column(nullable = false)
    private BigDecimal netSalary;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column
    private LocalDateTime confirmTime;

    @Column(length = 128)
    private String confirmedBy;
} 