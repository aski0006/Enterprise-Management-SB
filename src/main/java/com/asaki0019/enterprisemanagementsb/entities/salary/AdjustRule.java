package com.asaki0019.enterprisemanagementsb.entities.salary;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_adjust_rule")
@Data
public class AdjustRule {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(128)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String ruleName;

    @Column(nullable = false, length = 50)
    private String ruleType;

    @Column(nullable = false, length = 50)
    private String applyTo;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private LocalDate expireDate;

    @Column(columnDefinition = "TEXT")
    private String adjustRatio;  // 使用JSON格式存储调整比例

    @Column(columnDefinition = "TEXT")
    private String approvalFlow;  // 使用JSON格式存储审批流程

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, length = 50)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(columnDefinition = "TEXT")
    private String description;
} 