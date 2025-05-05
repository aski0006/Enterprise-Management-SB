package com.asaki0019.enterprisemanagementsb.controller.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.service.salary.SalaryReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 薪资报表控制器
 */
@RestController
@RequestMapping("/api/salary/reports")
public class SalaryReportsController {

    private final SalaryReportsService salaryReportsService;

    @Autowired
    public SalaryReportsController(SalaryReportsService salaryReportsService) {
        this.salaryReportsService = salaryReportsService;
    }

    // 获取员工薪资趋势数据
    @GetMapping("/employee-trends")
    public Result<?> getEmployeeSalaryTrends(
            @RequestParam String employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        return salaryReportsService.getEmployeeSalaryTrends(employeeId, startDate, endDate);
    }
    
    // 获取部门薪资对比数据
    @GetMapping("/department-comparison")
    public Result<?> getDepartmentComparison(
            @RequestParam(defaultValue = "2023") String year,
            @RequestParam(required = false) String quarter) {
        
        return salaryReportsService.getDepartmentComparison(year, quarter);
    }
    
    // 获取薪资分布统计
    @GetMapping("/salary-distribution")
    public Result<?> getSalaryDistribution(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String year) {
        
        return salaryReportsService.getSalaryDistribution(department, year);
    }
    
    // 获取薪资成本分析
    @GetMapping("/cost-analysis")
    public Result<?> getSalaryCostAnalysis(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String department) {
        
        return salaryReportsService.getSalaryCostAnalysis(startDate, endDate, department);
    }
}