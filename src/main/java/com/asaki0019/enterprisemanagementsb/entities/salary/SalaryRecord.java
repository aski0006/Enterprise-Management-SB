package com.asaki0019.enterprisemanagementsb.entities.salary;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "salary_records")
public class SalaryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer salaryId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_period", nullable = false)
    private Date payPeriod;

    @Column(name = "total_gross", nullable = false, precision = 12)
    private Double totalGross;

    @Column(name = "total_deductions", nullable = false, precision = 12)
    private Double totalDeductions;

    @Column(name = "net_pay", nullable = false, precision = 12)
    private Double netPay;

    @Column(name = "payment_status", nullable = false, length = 10)
    private String paymentStatus = "待发放";

    
    @OneToMany(mappedBy = "salaryRecord")
    private List<SalaryDetail> salaryDetails;
}