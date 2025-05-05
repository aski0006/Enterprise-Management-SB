package com.asaki0019.enterprisemanagementsb.controller.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.service.salary.SalaryArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 薪资档案控制器
 */
@RestController
@RequestMapping("/api/salary")
public class SalaryArchiveController {
    
    private final SalaryArchiveService salaryArchiveService;
    
    @Autowired
    public SalaryArchiveController(SalaryArchiveService salaryArchiveService) {
        this.salaryArchiveService = salaryArchiveService;
    }
    
    // 获取员工薪资档案列表
    @GetMapping("/employees")
    public Result<?> getEmployeeSalaries(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return salaryArchiveService.getEmployeeSalaries(employeeId, employeeName, department, page, pageSize);
    }
    
    // 获取员工薪资详情
    @GetMapping("/employee/detail")
    public Result<?> getEmployeeSalaryDetail(@RequestParam String employeeId) {
        return salaryArchiveService.getEmployeeSalaryDetail(employeeId);
    }
    
    // 更新员工薪资信息
    @PostMapping("/employee/update")
    public Result<?> updateEmployeeSalary(@RequestBody Map<String, Object> request) {
        return salaryArchiveService.updateEmployeeSalary(request);
    }
    
    // 获取员工薪资变更历史
    @GetMapping("/employee/history")
    public Result<?> getEmployeeSalaryHistory(@RequestParam String employeeId) {
        return salaryArchiveService.getEmployeeSalaryHistory(employeeId);
    }
    
    // 导入员工薪资档案
    @PostMapping("/import")
    public Result<?> importSalaryArchives(
            @RequestParam String fileType,
            @RequestBody Map<String, Object> request) {
        
        return salaryArchiveService.importSalaryArchives(fileType, request);
    }
    
    // 导出员工薪资档案
    @GetMapping("/export")
    public Result<?> exportSalaryArchives(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String year) {
        
        return salaryArchiveService.exportSalaryArchives(department, year);
    }
    
    // 薪资基准数据
    @GetMapping("/benchmark")
    public Result<?> getSalaryBenchmark(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position) {
        
        return salaryArchiveService.getSalaryBenchmark(department, position);
    }
} 