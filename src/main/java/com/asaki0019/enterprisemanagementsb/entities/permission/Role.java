package com.asaki0019.enterprisemanagementsb.entities.permission;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

// Role.java
@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @Column(unique = true, nullable = false, length = 50)
    private String roleName;
    
    // 显示名，兼容初始化器
    @Column(length = 50)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
    
    // 兼容初始化器
    public void setName(String name) {
        this.roleName = name;
    }
}

// User.java
