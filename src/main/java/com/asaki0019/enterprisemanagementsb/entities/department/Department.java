package com.asaki0019.enterprisemanagementsb.entities.department;


import java.util.List;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.salary.Position;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "departments")
@Data
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id") // 明确指定列名
    private Integer departmentId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_department_id", referencedColumnName = "department_id")
    private Department parentDepartment;

    @ManyToOne
    @JoinColumn(name = "manager_id") // 假设数据库表中有一个 manager_id 列来引用经理
    private Employee manager;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    @OneToMany(mappedBy = "department")
    private List<Position> positions;
}