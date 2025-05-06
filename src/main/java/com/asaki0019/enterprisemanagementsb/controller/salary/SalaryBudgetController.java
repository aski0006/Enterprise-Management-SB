package com.asaki0019.enterprisemanagementsb.controller.salary;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
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
    @PostMapping("/department")
    public Result<?> saveDepartmentBudget(@RequestBody Map<String, Object> requestBody) {
        // 验证必要参数
        if (requestBody == null || !requestBody.containsKey("departmentId") || 
                requestBody.get("departmentId") == null || requestBody.get("departmentId").toString().isEmpty()) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "部门ID不能为空");
        }
        
        if (!requestBody.containsKey("year") || requestBody.get("year") == null || requestBody.get("year").toString().isEmpty()) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "年份不能为空");
        }
        
        if (!requestBody.containsKey("quarter") || requestBody.get("quarter") == null || requestBody.get("quarter").toString().isEmpty()) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "季度不能为空");
        }
        
        if (!requestBody.containsKey("budgetAmount") || requestBody.get("budgetAmount") == null) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "预算金额不能为空");
        }
        
        return salaryBudgetService.saveDepartmentBudget(requestBody);
    }
    
    // 获取预算配置数据
    @GetMapping("/allocation")
    public Result<?> getBudgetAllocation(
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String year) {
        
        if (departmentId == null || departmentId.isEmpty()) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "部门ID不能为空");
        }
        
        return salaryBudgetService.getBudgetAllocation(departmentId, year);
    }
    
    // 保存预算配置
    @PostMapping("/allocation")
    public Result<?> saveBudgetAllocation(@RequestBody Map<String, Object> requestBody) {
        // 验证必要参数
        if (requestBody == null || !requestBody.containsKey("departmentId") || 
                requestBody.get("departmentId") == null || requestBody.get("departmentId").toString().isEmpty()) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "部门ID不能为空");
        }
        
        if (!requestBody.containsKey("items") || requestBody.get("items") == null) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "预算分配明细不能为空");
        }
        
        return salaryBudgetService.saveBudgetAllocation(requestBody);
    }
    
    // 获取预算执行报告
    @GetMapping("/execution-report")
    public Result<?> getBudgetExecutionReport(
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "2023") String year,
            @RequestParam(required = false) String quarter) {
        
        return salaryBudgetService.getBudgetExecutionReport(department, year, quarter);
    }
    
    // 获取预算预测
    @PostMapping("/forecast")
    public Result<?> getBudgetForecast(@RequestBody Map<String, Object> requestBody) {
        // 验证必要参数
        if (requestBody == null) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "请求参数不能为空");
        }
        
        String year = requestBody.get("year") != null ? requestBody.get("year").toString() : null;
        String department = requestBody.get("department") != null ? requestBody.get("department").toString() : null;
        Integer months = requestBody.get("months") != null ? Integer.parseInt(requestBody.get("months").toString()) : 12;
        
        if (year == null || year.isEmpty()) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "年份不能为空");
        }
        
        return salaryBudgetService.getBudgetForecast(year, department, months);
    }
    
    // 获取预算调整历史记录
    @GetMapping("/adjustment-history")
    public Result<?> getBudgetAdjustmentHistory(
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        if (departmentId == null || departmentId.isEmpty()) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "部门ID不能为空");
        }
        
        return salaryBudgetService.getBudgetAdjustmentHistory(departmentId, year, page, pageSize);
    }
    
    // 获取预算执行分析
    @GetMapping("/execution-analysis")
    public Result<?> getBudgetExecutionAnalysis(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String quarter) {
        
        return salaryBudgetService.getBudgetExecutionAnalysis(department, year, quarter);
    }
} 