package com.asaki0019.enterprisemanagementsb.entities.position;

import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.employee.Position;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "onboarding_applications")
@Getter
@Setter
public class OnboardingApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(length = 10, nullable = false)
    private String gender;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(length = 20, nullable = false, unique = true)
    private String idNumber;

    @Column(length = 50, nullable = false)
    private String phone;

    @Column(length = 100, nullable = false)
    private String email;

    @Column
    private LocalDate joinDate;

    @Column(nullable = false)
    private LocalDateTime applyTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(length = 500)
    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
}