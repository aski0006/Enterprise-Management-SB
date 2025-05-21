package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.permission.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleName(String roleName);
}
