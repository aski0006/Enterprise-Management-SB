package com.asaki0019.enterprisemanagementsb.entities.salary;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_grade")
@Data
public class SalaryGrade {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(128)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 50)
    private String gradeName;

    @Column(nullable = false, length = 10)
    private String level;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 50)
    private String department;

    @Column(length = 50)
    private String positionTypes;

    @Column(nullable = false)
    private BigDecimal minSalary;

    @Column(nullable = false)
    private BigDecimal maxSalary;

    @Column(nullable = false)
    private BigDecimal midSalary;

    @Column(nullable = false)
    private BigDecimal baseSalaryRatio;

    @Column(nullable = false)
    private BigDecimal performanceRatio;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private LocalDateTime updatedTime;
} 