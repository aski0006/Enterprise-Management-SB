package com.asaki0019.enterprisemanagementsb.request.enter;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}