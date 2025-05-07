package com.asaki0019.enterprisemanagementsb.entities.salary;

import com.asaki0019.enterprisemanagementsb.enums.ItemType;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.math.BigDecimal;

@Embeddable  // 被嵌入到SalaryRecord表中
@Data
public class SalaryItem {
    private String itemName;
    private ItemType itemType;
    private BigDecimal amount;
}