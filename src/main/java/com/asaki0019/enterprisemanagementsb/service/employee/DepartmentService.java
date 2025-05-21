package com.asaki0019.enterprisemanagementsb.service.employee;

import com.asaki0019.enterprisemanagementsb.core.model.Result;

/**
 * 部门管理服务接口
 */
public interface DepartmentService {
    
    /**
     * 获取所有部门列表
     * @return 部门列表结果
     */
    Result<?> getAllDepartments();
} 