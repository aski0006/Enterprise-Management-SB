package com.asaki0019.enterprisemanagementsb.entities.salary;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.YearMonth;
import java.util.List;

@Entity
@Table(name = "salary_records")
@Data
public class SalaryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_period")
    private String payPeriod; // 改为String类型，格式如 "2023-05" 表示2023年5月
    
    private Double totalGross;
    private Double totalDeductions;
    private Double netPay;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ElementCollection
    @CollectionTable(
            name = "salary_items",
            joinColumns = @JoinColumn(name = "salary_id")
    )
    private List<SalaryItem> items;
    
    // 工具方法，将YearMonth转换为String
    public void setPayPeriodByYearMonth(YearMonth yearMonth) {
        this.payPeriod = yearMonth.toString();
    }
    
    // 工具方法，将String转换为YearMonth
    public YearMonth getPayPeriodAsYearMonth() {
        if (this.payPeriod == null) {
            return null;
        }
        return YearMonth.parse(this.payPeriod);
    }
}

