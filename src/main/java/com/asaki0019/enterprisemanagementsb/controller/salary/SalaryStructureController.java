package com.asaki0019.enterprisemanagementsb.controller.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.salary.AdjustRuleRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.BonusSchemeRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.SalaryGradeRequest;
import com.asaki0019.enterprisemanagementsb.service.salary.SalaryStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary/structure")
public class SalaryStructureController {
    
    private final SalaryStructureService salaryStructureService;
    
    @Autowired
    public SalaryStructureController(SalaryStructureService salaryStructureService) {
        this.salaryStructureService = salaryStructureService;
    }
    
    // 获取薪资等级列表
    @GetMapping("/grades")
    public Result<?> getSalaryGrades(
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return salaryStructureService.getSalaryGrades(department, page, pageSize);
    }
    
    // 保存薪资等级
    @PostMapping("/grade/save")
    public Result<?> saveSalaryGrade(@RequestBody SalaryGradeRequest request) {
        return salaryStructureService.saveSalaryGrade(request);
    }
    
    // 获取调薪规则列表
    @GetMapping("/adjust-rules")
    public Result<?> getAdjustRules(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return salaryStructureService.getAdjustRules(type, page, pageSize);
    }
    
    // 保存调薪规则
    @PostMapping("/adjust-rule/save")
    public Result<?> saveAdjustRule(@RequestBody AdjustRuleRequest request) {
        return salaryStructureService.saveAdjustRule(request);
    }
    
    // 获取奖金方案列表
    @GetMapping("/bonus-schemes")
    public Result<?> getBonusSchemes(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return salaryStructureService.getBonusSchemes(department, year, page, pageSize);
    }
    
    // 保存奖金方案
    @PostMapping("/bonus-scheme/save")
    public Result<?> saveBonusScheme(@RequestBody BonusSchemeRequest request) {
        return salaryStructureService.saveBonusScheme(request);
    }
}