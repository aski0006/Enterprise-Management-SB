package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.performance.PerformanceCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 绩效评估标准仓库接口
 */
@Repository
public interface PerformanceCriteriaRepository extends JpaRepository<PerformanceCriteria, Integer> {
    
    /**
     * 根据适用部门查询绩效评估标准
     * @param department 适用部门
     * @return 绩效评估标准列表
     */
    List<PerformanceCriteria> findByDepartment(String department);
    
    /**
     * 根据适用职位查询绩效评估标准
     * @param position 适用职位
     * @return 绩效评估标准列表
     */
    List<PerformanceCriteria> findByPosition(String position);
    
    /**
     * 根据适用部门和适用职位查询绩效评估标准
     * @param department 适用部门
     * @param position 适用职位
     * @return 绩效评估标准列表
     */
    List<PerformanceCriteria> findByDepartmentAndPosition(String department, String position);
    
    /**
     * 查询通用的绩效评估标准（适用部门和适用职位均为空）
     * @return 绩效评估标准列表
     */
    List<PerformanceCriteria> findByDepartmentIsNullAndPositionIsNull();
} 