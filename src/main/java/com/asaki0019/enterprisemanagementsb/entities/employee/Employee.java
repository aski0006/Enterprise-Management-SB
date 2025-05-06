package com.asaki0019.enterprisemanagementsb.entities.employee;

import com.asaki0019.enterprisemanagementsb.entities.attendance.AttendanceRecord;
import com.asaki0019.enterprisemanagementsb.entities.department.Department;
import com.asaki0019.enterprisemanagementsb.entities.leave.LeaveRecord;
import com.asaki0019.enterprisemanagementsb.entities.performance.PerformanceEvaluation;
import com.asaki0019.enterprisemanagementsb.entities.salary.Position;
import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryRecord;
import com.asaki0019.enterprisemanagementsb.entities.security.Role;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "gender", nullable = false, length = 1)
    private String gender;

    @Column(name = "birth_date", nullable = false)
    private Date birthDate;

    @Column(name = "id_number", nullable = false, unique = true, length = 20)
    private String idNumber;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "hire_date", nullable = false)
    private Date hireDate;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Column(name = "employment_status", nullable = false, length = 10)
    private String employmentStatus = "在职";

    @Column(name = "bank_account", nullable = false, length = 50)
    private String bankAccount;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;


    
    @OneToMany(mappedBy = "manager")
    private List<Department> managedDepartments;

    @OneToMany(mappedBy = "employee")
    private List<AttendanceRecord> attendanceRecords;

    @OneToMany(mappedBy = "employee")
    private List<SalaryRecord> salaryRecords;

    @OneToMany(mappedBy = "employee")
    private List<LeaveRecord> leaveRecords;

    @OneToMany(mappedBy = "employee")
    private List<PerformanceEvaluation> performanceEvaluations;

    @OneToMany(mappedBy = "approver")
    private List<LeaveRecord> approvedLeaveRecords;

    @OneToMany(mappedBy = "evaluator")
    private List<PerformanceEvaluation> evaluatedPerformances;

    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
}