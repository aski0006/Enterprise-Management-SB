package com.asaki0019.enterprisemanagementsb.mapper;

import com.asaki0019.enterprisemanagementsb.entities.enter.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, String> {
    // 自动获得以下方法：
    // save(), findById(), findAll(), deleteById(), count() 等

    UserModel findByUsername(String username);
    List<UserModel> findByEmailEndsWith(String domain);
}