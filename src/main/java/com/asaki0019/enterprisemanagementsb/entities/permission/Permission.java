package com.asaki0019.enterprisemanagementsb.entities.permission;

import jakarta.persistence.*;
import lombok.Data;

// Permission.java
@Entity
@Table(name = "permissions")
@Data
public class Permission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer permissionId;

    @Column(unique = true, nullable = false, length = 50)
    private String permissionCode;

    @Column(length = 255)
    private String description;
}