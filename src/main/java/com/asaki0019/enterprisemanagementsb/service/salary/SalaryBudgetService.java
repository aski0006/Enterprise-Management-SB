package com.asaki0019.enterprisemanagementsb.service.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;

import java.util.Map;

/**
 * 薪资预算服务接口
 */
public interface SalaryBudgetService {

    /**
     * 获取部门预算列表
     *
     * @param year       年份
     * @param quarter    季度
     * @param department 部门
     * @param page       页码
     * @param pageSize   每页数量
     * @return 部门预算列表
     */
    Result<?> getDepartmentBudgets(String year, String quarter, String department, int page, int pageSize);

    /**
     * 保存部门预算
     *
     * @param request 预算数据
     * @return 保存结果
     */
    Result<?> saveDepartmentBudget(Map<String, Object> request);

    /**
     * 获取预算分配数据
     *
     * @param departmentId 部门ID
     * @param year         年份
     * @return 预算分配数据
     */
    Result<?> getBudgetAllocation(String departmentId, String year);

    /**
     * 保存预算分配
     *
     * @param request 预算分配数据
     * @return 保存结果
     */
    Result<?> saveBudgetAllocation(Map<String, Object> request);

    /**
     * 获取预算执行报告
     *
     * @param department 部门
     * @param year       年份
     * @param quarter    季度
     * @return 预算执行报告
     */
    Result<?> getBudgetExecutionReport(String department, String year, String quarter);
} 