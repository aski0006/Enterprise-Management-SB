package com.asaki0019.enterprisemanagementsb.service.performance;

import com.asaki0019.enterprisemanagementsb.core.model.Result;

/**
 * 绩效管理服务接口
 */
public interface PerformanceService {
    
    /**
     * 获取员工绩效评估列表
     * @param employeeId 员工ID
     * @param year 年份
     * @param quarter 季度
     * @param page 页码
     * @param pageSize 每页条数
     * @return 员工绩效评估列表
     */
    Result<?> getEmployeeEvaluations(String employeeId, String year, String quarter, Integer page, Integer pageSize);
    
    /**
     * 获取评估详情
     * @param id 评估ID
     * @return 评估详情
     */
    Result<?> getEvaluationDetail(String id);
    
    /**
     * 获取部门绩效汇总
     * @param year 年份
     * @param quarter 季度
     * @param department 部门
     * @return 部门绩效汇总
     */
    Result<?> getDepartmentSummary(String year, String quarter, String department);
    
    /**
     * 获取绩效评估标准
     * @param departmentId 部门ID
     * @param position 职位
     * @return 绩效评估标准
     */
    Result<?> getPerformanceCriteria(String departmentId, String position);
} 