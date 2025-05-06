package com.asaki0019.enterprisemanagementsb.entities.salary;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "salary_details")
public class SalaryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer detailId;

    @ManyToOne
    @JoinColumn(name = "salary_id", nullable = false)
    private SalaryRecord salaryRecord;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private SalaryItem salaryItem;

    @Column(name = "amount", nullable = false, precision = 12)
    private Double amount;
}