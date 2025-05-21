package com.asaki0019.enterprisemanagementsb.service.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.salary.*;

/**
 * 薪资管理服务接口
 */
public interface SalaryManagementService {
    
    /**
     * 获取员工薪资档案列表
     * @param request 搜索条件
     * @return 员工薪资档案列表
     */
    Result<?> getEmployeeSalaries(EmployeeSalaryRequest request);
    
    /**
     * 获取单个员工薪资档案详情
     * @param employeeId 员工ID
     * @return 员工薪资档案详情
     */
    Result<?> getEmployeeSalaryDetail(String employeeId);
    
    /**
     * 更新员工薪资档案
     * @param request 薪资更新请求
     * @return 更新结果
     */
    Result<?> updateEmployeeSalary(SalaryUpdateRequest request);
    
    /**
     * 员工薪资核算
     * @param request 薪资核算请求
     * @return 核算结果
     */
    Result<?> calculateSalaries(SalaryCalculationRequest request);
    
    /**
     * 获取员工薪资计算数据
     * @param request 薪资计算查询请求
     * @return 薪资计算数据
     */
    Result<?> getSalaryCalculation(SalaryCalculationRequest request);
    
    /**
     * 确认薪资计算结果
     * @param request 确认请求
     * @return 确认结果
     */
    Result<?> confirmSalaryCalculation(SalaryCalculationRequest request);
    
    /**
     * 调整薪资数据
     * @param request 调整请求
     * @return 调整结果
     */
    Result<?> adjustSalaryCalculation(SalaryAdjustRequest request);
    
    /**
     * 工资单查询
     * @param request 工资单请求
     * @return 工资单列表
     */
    Result<?> getPayslips(PayslipRequest request);
    
    /**
     * 获取工资单详情
     * @param id 工资单ID
     * @return 工资单详情
     */
    Result<?> getPayslipDetail(String id);
    
    /**
     * 批量发放工资单
     * @param request 工资单发放请求
     * @return 发放结果
     */
    Result<?> distributePayslips(PayslipDistributeRequest request);
    
    /**
     * 生成工资单
     * @param request 工资单生成请求
     * @return 生成结果
     */
    Result<?> generatePayslips(PayslipGenerateRequest request);
    
    /**
     * 发放单个工资单
     * @param request 单个工资单发放请求
     * @return 发放结果
     */
    Result<?> distributeOnePayslip(PayslipDistributeOneRequest request);
} 