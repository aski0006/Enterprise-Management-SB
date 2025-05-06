package com.asaki0019.enterprisemanagementsb.controller.system;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.service.system.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/api")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * 获取部门列表
     */
    @GetMapping("/departments")
    public Result<?> getDepartments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return departmentService.getDepartments(name, status, page, pageSize);
    }

    /**
     * 获取部门详情
     */
    @GetMapping("/departments/{id}")
    public Result<?> getDepartmentById(@PathVariable String id) {
        return departmentService.getDepartmentById(id);
    }

    /**
     * 创建部门
     */
    @PostMapping("/departments")
    public Result<?> createDepartment(@RequestBody Map<String, Object> department) {
        return departmentService.createDepartment(department);
    }

    /**
     * 更新部门
     */
    @PutMapping("/departments/{id}")
    public Result<?> updateDepartment(
            @PathVariable String id,
            @RequestBody Map<String, Object> department) {
        return departmentService.updateDepartment(id, department);
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/departments/{id}")
    public Result<?> deleteDepartment(@PathVariable String id) {
        return departmentService.deleteDepartment(id);
    }
} 