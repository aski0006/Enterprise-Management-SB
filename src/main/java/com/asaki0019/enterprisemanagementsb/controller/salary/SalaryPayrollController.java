package com.asaki0019.enterprisemanagementsb.controller.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.salary.PayslipRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.SalaryCalculationRequest;
import com.asaki0019.enterprisemanagementsb.service.salary.SalaryPayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SalaryPayrollController {
    
    private final SalaryPayrollService salaryPayrollService;
    
    @Autowired
    public SalaryPayrollController(SalaryPayrollService salaryPayrollService) {
        this.salaryPayrollService = salaryPayrollService;
    }
    
    // 获取薪资核算数据
    @GetMapping("/salary/calculation")
    public Result<?> getSalaryCalculation(
            @RequestParam String period,
            @RequestParam(required = false) String department) {
        return salaryPayrollService.getSalaryCalculation(period, department);
    }
    
    // 确认薪资数据
    @PostMapping("/salary/calculation/confirm")
    public Result<?> confirmSalaryCalculation(@RequestBody SalaryCalculationRequest request) {
        return salaryPayrollService.confirmSalaryCalculation(request);
    }
    
    // 调整薪资
    @PostMapping("/salary/calculation/adjust")
    public Result<?> adjustSalary(@RequestBody SalaryCalculationRequest request) {
        return salaryPayrollService.adjustSalary(request);
    }
    
    // 获取工资单列表
    @GetMapping("/payslip/list")
    public Result<?> getPayslipList(
            @RequestParam String month,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return salaryPayrollService.getPayslipList(month, department, status, page, pageSize);
    }
    
    // 获取工资单详情
    @GetMapping("/payslip/detail")
    public Result<?> getPayslipDetail(
            @RequestParam String id,
            @RequestParam String month) {
        return salaryPayrollService.getPayslipDetail(id, month);
    }
    
    // 生成工资单
    @PostMapping("/payslip/generate")
    public Result<?> generatePayslip(@RequestBody PayslipRequest request) {
        return salaryPayrollService.generatePayslip(request);
    }
    
    // 发放工资单（批量）
    @PostMapping("/payslip/distribute")
    public Result<?> distributePayslips(@RequestBody PayslipRequest request) {
        return salaryPayrollService.distributePayslips(request);
    }
    
    // 发放单个工资单
    @PostMapping("/payslip/distribute-one")
    public Result<?> distributeOnePayslip(@RequestBody PayslipRequest request) {
        return salaryPayrollService.distributeOnePayslip(request);
    }
} 