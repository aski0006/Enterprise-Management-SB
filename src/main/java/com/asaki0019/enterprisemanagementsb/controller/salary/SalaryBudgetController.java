package com.asaki0019.enterprisemanagementsb.controller.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.service.salary.SalaryBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 薪资预算控制器
 */
@RestController
@RequestMapping("/api/salary/budget")
public class SalaryBudgetController {

    private final SalaryBudgetService salaryBudgetService;

    @Autowired
    public SalaryBudgetController(SalaryBudgetService salaryBudgetService) {
        this.salaryBudgetService = salaryBudgetService;
    }

    // 获取部门预算列表
    @GetMapping("/department")
    public Result<?> getDepartmentBudgets(
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String quarter,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return salaryBudgetService.getDepartmentBudgets(year, quarter, department, page, pageSize);
    }
    
    // 保存部门预算
    @PostMapping("/department/save")
    public Result<?> saveDepartmentBudget(@RequestBody Map<String, Object> request) {
        return salaryBudgetService.saveDepartmentBudget(request);
    }
    
    // 获取预算分配数据
    @GetMapping("/allocation")
    public Result<?> getBudgetAllocation(
            @RequestParam String departmentId,
            @RequestParam(defaultValue = "2023") String year) {
        
        return salaryBudgetService.getBudgetAllocation(departmentId, year);
    }
    
    // 保存预算分配
    @PostMapping("/allocation/save")
    public Result<?> saveBudgetAllocation(@RequestBody Map<String, Object> request) {
        return salaryBudgetService.saveBudgetAllocation(request);
    }
    
    // 获取预算执行报告
    @GetMapping("/execution-report")
    public Result<?> getBudgetExecutionReport(
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "2023") String year,
            @RequestParam(required = false) String quarter) {
        
        return salaryBudgetService.getBudgetExecutionReport(department, year, quarter);
    }
} 