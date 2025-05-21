package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 部门仓库
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    
    /**
     * 根据部门名称查询部门
     */
    Department findByName(String name);
    
    /**
     * 查询所有部门名称
     */
    @Query("SELECT d.name FROM Department d ORDER BY d.name")
    List<String> findAllDepartmentNames();
}
