package com.asaki0019.enterprisemanagementsb.controller.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.salary.*;
import com.asaki0019.enterprisemanagementsb.service.salary.SalaryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 薪资管理控制器
 */
@RestController
@RequestMapping("/api/salary")
@CrossOrigin(origins = "http://localhost:5001")
public class SalaryManagementController {
    
    private final SalaryManagementService salaryManagementService;
    
    @Autowired
    public SalaryManagementController(SalaryManagementService salaryManagementService) {
        this.salaryManagementService = salaryManagementService;
    }
    
    /**
     * 获取员工薪资档案列表
     */
    @GetMapping("/employees")
    public Result<?> getEmployeeSalaries(EmployeeSalaryRequest request) {
        return salaryManagementService.getEmployeeSalaries(request);
    }
    
    /**
     * 获取员工薪资档案详情
     */
    @GetMapping("/employee/{id}")
    public Result<?> getEmployeeSalaryDetail(@PathVariable String id) {
        return salaryManagementService.getEmployeeSalaryDetail(id);
    }
    
    /**
     * 更新员工薪资档案
     */
    @PostMapping("/employee/update")
    public Result<?> updateEmployeeSalary(@RequestBody SalaryUpdateRequest request) {
        return salaryManagementService.updateEmployeeSalary(request);
    }
    
    /**
     * 薪资核算
     */
    @PostMapping("/calculate")
    public Result<?> calculateSalaries(@RequestBody SalaryCalculationRequest request) {
        return salaryManagementService.calculateSalaries(request);
    }
    
    /**
     * 获取员工薪资计算数据
     * 对应API接口文档：获取员工薪资计算数据
     */
    @GetMapping("/calculation")
    public Result<?> getSalaryCalculation(SalaryCalculationRequest request) {
        return salaryManagementService.getSalaryCalculation(request);
    }
    
    /**
     * 确认薪资计算结果
     * 对应API接口文档：确认薪资计算结果
     */
    @PostMapping("/calculation/confirm")
    public Result<?> confirmSalaryCalculation(@RequestBody SalaryCalculationRequest request) {
        return salaryManagementService.confirmSalaryCalculation(request);
    }
    
    /**
     * 调整薪资数据
     * 对应API接口文档：调整薪资数据
     */
    @PostMapping("/calculation/adjust")
    public Result<?> adjustSalaryCalculation(@RequestBody SalaryAdjustRequest request) {
        return salaryManagementService.adjustSalaryCalculation(request);
    }
    
    /**
     * 工资单查询
     */
    @GetMapping("/payslips")
    public Result<?> getPayslips(PayslipRequest request) {
        return salaryManagementService.getPayslips(request);
    }
    
    /**
     * 获取工资单详情
     */
    @GetMapping("/payslips/detail")
    public Result<?> getPayslipDetail(@RequestParam String id) {
        return salaryManagementService.getPayslipDetail(id);
    }
    
    /**
     * 批量发放工资单
     */
    @PostMapping("/payslips/distribute")
    public Result<?> distributePayslips(@RequestBody PayslipDistributeRequest request) {
        return salaryManagementService.distributePayslips(request);
    }
    
    /**
     * 生成工资单
     */
    @PostMapping("/payslips/generate")
    public Result<?> generatePayslips(@RequestBody PayslipGenerateRequest request) {
        return salaryManagementService.generatePayslips(request);
    }
    
    /**
     * 发放单个工资单
     */
    @PostMapping("/payslips/distribute-one")
    public Result<?> distributeOnePayslip(@RequestBody PayslipDistributeOneRequest request) {
        return salaryManagementService.distributeOnePayslip(request);
    }
} 