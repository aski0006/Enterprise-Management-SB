package com.asaki0019.enterprisemanagementsb.service.employee;

import com.asaki0019.enterprisemanagementsb.core.model.Result;

/**
 * 职位管理服务接口
 */
public interface PositionService {
    
    /**
     * 获取所有职位列表
     * @param departmentId 部门ID，可选参数
     * @return 职位列表结果
     */
    Result<?> getAllPositions(Integer departmentId);
} 