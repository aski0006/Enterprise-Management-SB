package com.asaki0019.enterprisemanagementsb.entities.enter;

import lombok.Data;

@Data
public class UserModel {
    private String name;
    private String username;
    private String password;
    private String email;
    private Long id;
}