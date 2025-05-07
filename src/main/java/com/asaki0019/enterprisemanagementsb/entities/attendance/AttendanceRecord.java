package com.asaki0019.enterprisemanagementsb.entities.attendance;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.asaki0019.enterprisemanagementsb.enums.AttendanceStatus;
import com.asaki0019.enterprisemanagementsb.enums.LeaveType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// AttendanceRecord.java
@Entity
@Table(name = "attendance_records")
@Getter
@Setter
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private LocalDateTime clockIn;
    private LocalDateTime clockOut;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;
}

