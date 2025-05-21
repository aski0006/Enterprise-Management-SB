package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.salary.EmployeeSalary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 员工薪资档案仓库
 */
public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalary, Long> {
    
    /**
     * 查找员工当前有效的薪资档案
     * @param employee 员工
     * @return 薪资档案
     */
    Optional<EmployeeSalary> findByEmployeeAndIsActiveTrue(Employee employee);
    
    /**
     * 查找员工的所有薪资档案（历史记录）
     * @param employee 员工
     * @return 薪资档案列表
     */
    List<EmployeeSalary> findByEmployeeOrderByEffectiveDateDesc(Employee employee);
    
    /**
     * 分页查询部门员工薪资档案
     * @param departmentName 部门名称
     * @param pageable 分页参数
     * @return 薪资档案分页数据
     */
    @Query("SELECT es FROM EmployeeSalary es JOIN es.employee e JOIN e.department d " +
            "WHERE d.name = :departmentName AND es.isActive = true")
    Page<EmployeeSalary> findByDepartmentNameAndActive(@Param("departmentName") String departmentName, Pageable pageable);
    
    /**
     * 关键词搜索员工薪资档案
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 薪资档案分页数据
     */
    @Query("SELECT es FROM EmployeeSalary es JOIN es.employee e " +
            "WHERE (e.firstName LIKE %:keyword% OR e.lastName LIKE %:keyword%) AND es.isActive = true")
    Page<EmployeeSalary> searchByNameKeyword(@Param("keyword") String keyword, Pageable pageable);
} 