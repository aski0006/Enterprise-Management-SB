package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 薪资等级仓库接口
 */
@Repository
public interface SalaryGradeRepository extends JpaRepository<SalaryGrade, Integer> {
    
    /**
     * 根据部门查询薪资等级
     * @param department 部门
     * @return 薪资等级列表
     */
    List<SalaryGrade> findByDepartment(String department);
    
    /**
     * 根据等级编码查询薪资等级
     * @param gradeCode 等级编码
     * @return 薪资等级
     */
    Optional<SalaryGrade> findByGradeCode(String gradeCode);
    
    /**
     * 根据等级名称查询薪资等级
     * @param gradeName 等级名称
     * @return 薪资等级
     */
    Optional<SalaryGrade> findByGradeName(String gradeName);
    
    /**
     * 根据部门和等级查询薪资等级
     * @param department 部门
     * @param level 等级
     * @return 薪资等级
     */
    Optional<SalaryGrade> findByDepartmentAndLevel(String department, Integer level);
} 