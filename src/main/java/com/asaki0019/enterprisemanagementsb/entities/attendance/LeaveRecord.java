package com.asaki0019.enterprisemanagementsb.entities.attendance;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.asaki0019.enterprisemanagementsb.enums.LeaveType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

// LeaveRecord.java
@Entity
@Table(name = "leave_records")
@Data
public class LeaveRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee approver;
}