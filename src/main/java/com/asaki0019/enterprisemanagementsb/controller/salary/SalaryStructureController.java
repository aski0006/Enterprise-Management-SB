package com.asaki0019.enterprisemanagementsb.controller.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.salary.SalaryGradeRequest;
import com.asaki0019.enterprisemanagementsb.service.salary.SalaryStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 薪资结构管理控制器
 */
@RestController
@RequestMapping("/api/salary/structure")
@CrossOrigin(origins = "http://localhost:5001")
public class SalaryStructureController {
    
    private final SalaryStructureService salaryStructureService;
    
    @Autowired
    public SalaryStructureController(SalaryStructureService salaryStructureService) {
        this.salaryStructureService = salaryStructureService;
    }
    
    /**
     * 获取薪资等级列表
     */
    @GetMapping("/grades")
    public Result<?> getSalaryGrades(@RequestParam(required = false) String department) {
        return salaryStructureService.getSalaryGrades(department);
    }
    
    /**
     * 保存薪资等级
     */
    @PostMapping("/grade/save")
    public Result<?> saveSalaryGrade(@RequestBody SalaryGradeRequest request) {
        return salaryStructureService.saveSalaryGrade(request);
    }
    
    /**
     * 获取薪资调整规则
     */
    @GetMapping("/adjust-rules")
    public Result<?> getSalaryAdjustRules(@RequestParam(required = false) String type) {
        return salaryStructureService.getSalaryAdjustRules(type);
    }
    
    /**
     * 获取奖金方案列表
     */
    @GetMapping("/bonus-schemes")
    public Result<?> getBonusSchemes(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String year,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return salaryStructureService.getBonusSchemes(department, year, page, pageSize);
    }
} 