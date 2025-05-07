package com.asaki0019.enterprisemanagementsb.entities.employee;
import com.asaki0019.enterprisemanagementsb.entities.attendance.LeaveRecord;
import com.asaki0019.enterprisemanagementsb.entities.permission.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Employee.java
@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;

    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate birthDate;

    @Column(length = 20)
    private String idNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<LeaveRecord> leaveRecords = new ArrayList<>();
}

