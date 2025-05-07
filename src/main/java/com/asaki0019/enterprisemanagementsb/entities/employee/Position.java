package com.asaki0019.enterprisemanagementsb.entities.employee;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// Position.java
@Entity
@Table(name = "positions")
@Getter
@Setter
public class Position {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer positionId;

    @Column(unique = true, nullable = false, length = 50)
    private String title;

    private Double baseSalary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}