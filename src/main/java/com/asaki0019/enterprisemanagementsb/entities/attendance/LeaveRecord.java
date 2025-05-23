package com.asaki0019.enterprisemanagementsb.entities.attendance;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.asaki0019.enterprisemanagementsb.enums.LeaveType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_records")
@Getter
@Setter
public class LeaveRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING; // 默认值设为PENDING

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee approver;

    private LocalDateTime approvalTime;

    @Column(length = 500)
    private String comments; // 统一使用comments字段名

    // 新增业务方法
    public void approve(Employee approver, String comment) {
        this.status = ApprovalStatus.APPROVED;
        this.approver = approver;
        this.approvalTime = LocalDateTime.now();
        this.comments = comment;
    }

    public void reject(Employee approver, String comment) {
        this.status = ApprovalStatus.REJECTED;
        this.approver = approver;
        this.approvalTime = LocalDateTime.now();
        this.comments = comment;
    }
}