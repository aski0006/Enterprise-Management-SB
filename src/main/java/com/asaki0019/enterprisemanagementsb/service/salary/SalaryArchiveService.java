package com.asaki0019.enterprisemanagementsb.service.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;

import java.util.Map;

/**
 * 薪资档案服务接口
 */
public interface SalaryArchiveService {

    /**
     * 获取员工薪资档案列表
     *
     * @param employeeId   员工ID
     * @param employeeName 员工姓名
     * @param department   部门
     * @param page         页码
     * @param pageSize     每页数量
     * @return 员工薪资档案列表
     */
    Result<?> getEmployeeSalaries(String employeeId, String employeeName, String department, int page, int pageSize);

    /**
     * 获取员工薪资详情
     *
     * @param employeeId 员工ID
     * @return 员工薪资详情
     */
    Result<?> getEmployeeSalaryDetail(String employeeId);

    /**
     * 更新员工薪资信息
     *
     * @param request 薪资信息
     * @return 更新结果
     */
    Result<?> updateEmployeeSalary(Map<String, Object> request);

    /**
     * 获取员工薪资变更历史
     *
     * @param employeeId 员工ID
     * @return 员工薪资变更历史
     */
    Result<?> getEmployeeSalaryHistory(String employeeId);

    /**
     * 导入员工薪资档案
     *
     * @param fileType 文件类型
     * @param request  导入数据
     * @return 导入结果
     */
    Result<?> importSalaryArchives(String fileType, Map<String, Object> request);

    /**
     * 导出员工薪资档案
     *
     * @param department 部门
     * @param year       年份
     * @return 导出结果
     */
    Result<?> exportSalaryArchives(String department, String year);

    /**
     * 获取薪资基准数据
     *
     * @param department 部门
     * @param position   职位
     * @return 薪资基准数据
     */
    Result<?> getSalaryBenchmark(String department, String position);
} 