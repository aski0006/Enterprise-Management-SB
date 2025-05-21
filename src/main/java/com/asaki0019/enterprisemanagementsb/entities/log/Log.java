package com.asaki0019.enterprisemanagementsb.entities.log;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "log")
@Getter
@Setter
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logID;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private Date timestamp;

    @Column(nullable = false)
    private String controller;

    @Column(nullable = false)
    private String message;
}