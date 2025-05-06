package com.asaki0019.enterprisemanagementsb.entities.security;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer permissionId;

    @Column(name = "permission_code", nullable = false, unique = true, length = 50)
    private String permissionCode;

    @Column(name = "description", length = 255)
    private String description;

    
    @OneToMany(mappedBy = "permission")
    private List<RolePermission> rolePermissions;
}