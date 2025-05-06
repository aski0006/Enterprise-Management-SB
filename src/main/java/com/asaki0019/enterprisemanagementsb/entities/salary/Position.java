package com.asaki0019.enterprisemanagementsb.entities.salary;

import com.asaki0019.enterprisemanagementsb.entities.department.Department;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "positions")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer positionId;

    @Column(name = "title", nullable = false, unique = true, length = 50)
    private String title;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "base_salary", nullable = false, precision = 10)
    private Double baseSalary;

    
    @OneToMany(mappedBy = "position")
    private List<Employee> employees;
}