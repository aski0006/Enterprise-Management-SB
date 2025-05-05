package com.asaki0019.enterprisemanagementsb.service.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;

/**
 * 薪资报表服务接口
 */
public interface SalaryReportsService {

    /**
     * 获取员工薪资趋势数据
     *
     * @param employeeId 员工ID
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return 员工薪资趋势数据
     */
    Result<?> getEmployeeSalaryTrends(String employeeId, String startDate, String endDate);

    /**
     * 获取部门薪资对比数据
     *
     * @param year    年份
     * @param quarter 季度
     * @return 部门薪资对比数据
     */
    Result<?> getDepartmentComparison(String year, String quarter);

    /**
     * 获取薪资分布统计
     *
     * @param department 部门
     * @param year       年份
     * @return 薪资分布统计数据
     */
    Result<?> getSalaryDistribution(String department, String year);

    /**
     * 获取薪资成本分析
     *
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param department 部门
     * @return 薪资成本分析数据
     */
    Result<?> getSalaryCostAnalysis(String startDate, String endDate, String department);
} 