package com.asaki0019.enterprisemanagementsb.entities.security;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    
    @ManyToMany(mappedBy = "roles")
    private List<Employee> employees;

    @OneToMany(mappedBy = "role")
    private List<RolePermission> rolePermissions;
}