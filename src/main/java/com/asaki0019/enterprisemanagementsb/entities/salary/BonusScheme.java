package com.asaki0019.enterprisemanagementsb.entities.salary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 奖金方案实体类
 */
@Entity
@Table(name = "bonus_scheme")
@Data
public class BonusScheme {
    
    @Id
    @Column(name = "scheme_code", length = 20)
    @Comment("方案编码")
    private String schemeCode;
    
    @Column(name = "scheme_name", nullable = false, length = 100)
    @Comment("方案名称")
    private String schemeName;
    
    @Column(name = "department", length = 50)
    @Comment("适用部门")
    private String department;
    
    @Column(name = "year", length = 10)
    @Comment("适用年份")
    private String year;
    
    @Column(name = "formula_type", length = 50)
    @Comment("计算公式类型")
    private String formulaType;
    
    @Column(name = "formula_detail", columnDefinition = "TEXT")
    @Comment("计算公式详情(JSON格式)")
    private String formulaDetail;
    
    @Column(name = "total_budget")
    @Comment("总预算")
    private Double totalBudget;
    
    @Column(name = "distribution_method", length = 100)
    @Comment("分配方式")
    private String distributionMethod;
    
    @Column(name = "approval_status", length = 20)
    @Comment("审批状态")
    private String approvalStatus;
    
    @Column(name = "payment_date", length = 100)
    @Comment("发放日期")
    private String paymentDate;
    
    @Column(name = "created_by", length = 50)
    @Comment("创建人")
    private String createdBy;
    
    @Column(name = "created_time")
    @Comment("创建时间")
    private LocalDateTime createdTime;
    
    @Column(name = "updated_by", length = 50)
    @Comment("更新人")
    private String updatedBy;
    
    @Column(name = "updated_time")
    @Comment("更新时间")
    private LocalDateTime updatedTime;
    
    @Column(name = "description", columnDefinition = "TEXT")
    @Comment("方案描述")
    private String description;
    
    @PrePersist
    public void prePersist() {
        if (createdTime == null) {
            createdTime = LocalDateTime.now();
        }
        updatedTime = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedTime = LocalDateTime.now();
    }
} 