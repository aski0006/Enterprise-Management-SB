package com.asaki0019.enterprisemanagementsb.entities.enter;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "user")
@Data
public class UserModel {
    /**
     * 用户ID，使用UUID生成
     */
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(128)", updatable = false, nullable = false)
    private String id;

    /**
     * 用户姓名，最大长度100
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 用户登录名，唯一，最大长度50
     */
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * 用户密码
     */
    @Column(nullable = false)
    private String password;

    /**
     * 用户邮箱，唯一
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * 用户角色
     */
    @Column(unique = false, nullable = false)
    private String role;

    @OneToOne
    @JoinColumn(name = "employee_id") // 假设数据库中有一个 employee_id 列来引用员工
    private Employee employee;
}