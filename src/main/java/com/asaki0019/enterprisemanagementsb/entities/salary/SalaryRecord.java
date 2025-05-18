package com.asaki0019.enterprisemanagementsb.entities.salary;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.List;

@Entity
@Table(name = "salary_records")
@Getter
@Setter
public class SalaryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private YearMonth payPeriod;
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
}

