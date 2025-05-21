package com.asaki0019.enterprisemanagementsb.controller.employee;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.service.employee.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5001")
public class DepartmentController {

    private final DepartmentService departmentService;
    private static final Logger log = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * 获取所有部门列表
     * @return 部门列表
     */
    @GetMapping("/departments")
    public Result<?> getAllDepartments() {
        log.info("接收获取所有部门列表请求");
        return departmentService.getAllDepartments();
    }
} 