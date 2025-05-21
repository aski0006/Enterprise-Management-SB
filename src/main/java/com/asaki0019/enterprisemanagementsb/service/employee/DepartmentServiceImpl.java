package com.asaki0019.enterprisemanagementsb.service.employee;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.repositories.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门管理服务实现类
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Result<?> getAllDepartments() {
        log.info("获取所有部门列表");
        try {
            List<Department> departments = departmentRepository.findAll();
            List<Map<String, Object>> result = departments.stream()
                .map(department -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", department.getDepartmentId());
                    map.put("name", department.getName());
                    map.put("description", department.getDescription());
                    return map;
                })
                .collect(Collectors.toList());
                
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取部门列表失败", e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "获取部门列表失败");
        }
    }
} 