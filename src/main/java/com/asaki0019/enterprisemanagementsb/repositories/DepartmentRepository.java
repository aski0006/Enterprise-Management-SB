package com.asaki0019.enterprisemanagementsb.repositories;
import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DepartmentRepository extends JpaRepository<Department, Integer>{
    Department findByName(String name);
}
