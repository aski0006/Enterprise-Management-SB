package com.asaki0019.enterprisemanagementsb.entities.permission;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private String userId;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(length = 50)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String email;
    
    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Employee employee;
    
    public void setNickname(String nickname) {
        this.name = nickname;
    }
}