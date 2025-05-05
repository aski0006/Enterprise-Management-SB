package com.asaki0019.enterprisemanagementsb.entities.enter;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "user")
@Data
public class UserModel {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(128)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = false, nullable = false)
    private String role;
}