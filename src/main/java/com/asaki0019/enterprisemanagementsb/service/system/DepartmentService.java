package com.asaki0019.enterprisemanagementsb.service.system;

import com.asaki0019.enterprisemanagementsb.core.model.Result;

import java.util.Map;

/**
 * 部门管理服务接口
 */
public interface DepartmentService {

    /**
     * 获取部门列表
     *
     * @param name     部门名称（可选，用于筛选）
     * @param status   部门状态（可选，用于筛选）
     * @param page     页码
     * @param pageSize 每页条数
     * @return 部门列表及分页信息
     */
    Result<?> getDepartments(String name, String status, int page, int pageSize);

    /**
     * 根据ID获取部门详情
     *
     * @param id 部门ID
     * @return 部门详情
     */
    Result<?> getDepartmentById(String id);

    /**
     * 创建部门
     *
     * @param department 部门信息
     * @return 创建结果
     */
    Result<?> createDepartment(Map<String, Object> department);

    /**
     * 更新部门信息
     *
     * @param id         部门ID
     * @param department 部门信息
     * @return 更新结果
     */
    Result<?> updateDepartment(String id, Map<String, Object> department);

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 删除结果
     */
    Result<?> deleteDepartment(String id);
} 