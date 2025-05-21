package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.performance.PerformanceEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 绩效评估仓库接口
 */
@Repository
public interface PerformanceEvaluationRepository extends JpaRepository<PerformanceEvaluation, Integer> {
    
    /**
     * 根据评估编码查询绩效评估
     * @param evaluationCode 评估编码
     * @return 绩效评估
     */
    Optional<PerformanceEvaluation> findByEvaluationCode(String evaluationCode);
    
    /**
     * 根据员工查询绩效评估列表
     * @param employee 员工
     * @return 绩效评估列表
     */
    List<PerformanceEvaluation> findByEmployee(Employee employee);
    
    /**
     * 根据员工和年份查询绩效评估列表
     * @param employee 员工
     * @param year 年份
     * @return 绩效评估列表
     */
    List<PerformanceEvaluation> findByEmployeeAndYear(Employee employee, String year);
    
    /**
     * 根据员工、年份和季度查询绩效评估列表
     * @param employee 员工
     * @param year 年份
     * @param quarter 季度
     * @return 绩效评估列表
     */
    List<PerformanceEvaluation> findByEmployeeAndYearAndQuarter(Employee employee, String year, Integer quarter);
    
    /**
     * 根据员工、年份、季度和状态查询绩效评估列表
     * @param employee 员工
     * @param year 年份
     * @param quarter 季度
     * @param status 状态
     * @return 绩效评估列表
     */
    List<PerformanceEvaluation> findByEmployeeAndYearAndQuarterAndStatus(Employee employee, String year, Integer quarter, String status);
    
    /**
     * 根据年份查询绩效评估列表
     * @param year 年份
     * @param pageable 分页参数
     * @return 绩效评估列表
     */
    Page<PerformanceEvaluation> findByYear(String year, Pageable pageable);
    
    /**
     * 根据年份和季度查询绩效评估列表
     * @param year 年份
     * @param quarter 季度
     * @param pageable 分页参数
     * @return 绩效评估列表
     */
    Page<PerformanceEvaluation> findByYearAndQuarter(String year, Integer quarter, Pageable pageable);
    
    /**
     * 根据年份、季度和状态查询绩效评估列表
     * @param year 年份
     * @param quarter 季度
     * @param status 状态
     * @param pageable 分页
     * @return 绩效评估列表
     */
    Page<PerformanceEvaluation> findByYearAndQuarterAndStatus(String year, Integer quarter, String status, Pageable pageable);
} 