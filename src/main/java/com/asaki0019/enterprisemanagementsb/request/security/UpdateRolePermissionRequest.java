package com.asaki0019.enterprisemanagementsb.request.security;

import lombok.Data;

import java.util.HashSet;


@Data
public class UpdateRolePermissionRequest {
    private String username;
    private String roleName;
}