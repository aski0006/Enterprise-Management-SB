package com.asaki0019.enterprisemanagementsb.entities;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
    "com.asaki0019.enterprisemanagementsb.entities.department",
    "com.asaki0019.enterprisemanagementsb.entities.employee",
    "com.asaki0019.enterprisemanagementsb.entities.salary",
    "com.asaki0019.enterprisemanagementsb.entities.attendance",
    "com.asaki0019.enterprisemanagementsb.entities.leave",
    "com.asaki0019.enterprisemanagementsb.entities.performance",
    "com.asaki0019.enterprisemanagementsb.entities.security",
    "com.asaki0019.enterprisemanagementsb.entities.system"
})
public class EntitiesConfig {
}