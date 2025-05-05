package com.asaki0019.enterprisemanagementsb.entities.salary;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_bonus_scheme")
@Data
public class BonusScheme {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(128)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String schemeName;

    @Column(nullable = false, length = 50)
    private String department;

    @Column(nullable = false, length = 10)
    private String year;

    @Column(nullable = false, length = 50)
    private String formulaType;

    @Column(columnDefinition = "TEXT")
    private String formulaDetail;  // 使用JSON格式存储公式详情

    @Column(nullable = false)
    private BigDecimal totalBudget;

    @Column(nullable = false, length = 50)
    private String distributionMethod;

    @Column(nullable = false, length = 20)
    private String approvalStatus;

    @Column(length = 100)
    private String paymentDate;

    @Column(nullable = false, length = 50)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(columnDefinition = "TEXT")
    private String description;
} 