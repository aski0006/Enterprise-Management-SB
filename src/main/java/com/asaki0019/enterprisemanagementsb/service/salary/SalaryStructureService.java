package com.asaki0019.enterprisemanagementsb.service.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.salary.SalaryGradeRequest;

/**
 * 薪资结构管理服务接口
 */
public interface SalaryStructureService {
    
    /**
     * 获取薪资等级列表
     * @param department 部门
     * @return 薪资等级列表
     */
    Result<?> getSalaryGrades(String department);
    
    /**
     * 保存薪资等级
     * @param request 薪资等级请求
     * @return 保存结果
     */
    Result<?> saveSalaryGrade(SalaryGradeRequest request);
    
    /**
     * 获取薪资调整规则
     * @param type 规则类型
     * @return 薪资调整规则
     */
    Result<?> getSalaryAdjustRules(String type);
    
    /**
     * 获取奖金方案
     * @param department 部门
     * @param year 年份
     * @param page 页码
     * @param pageSize 每页大小
     * @return 奖金方案列表
     */
    Result<?> getBonusSchemes(String department, String year, Integer page, Integer pageSize);
} 