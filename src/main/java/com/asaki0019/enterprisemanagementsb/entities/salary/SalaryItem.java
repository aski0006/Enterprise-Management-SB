package com.asaki0019.enterprisemanagementsb.entities.salary;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "salary_items")
public class SalaryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;

    @Column(name = "item_name", nullable = false, unique = true, length = 50)
    private String itemName;

    @Column(name = "item_type", nullable = false, length = 10)
    private String itemType;

    @Column(name = "calculation_rule", columnDefinition = "TEXT")
    private String calculationRule;

    
    @OneToMany(mappedBy = "salaryItem")
    private List<SalaryDetail> salaryDetails;
}