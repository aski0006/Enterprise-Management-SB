package com.asaki0019.enterprisemanagementsb.controller.employee;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.service.employee.PositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 职位管理控制器
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5001")
public class PositionController {

    private final PositionService positionService;
    private static final Logger log = LoggerFactory.getLogger(PositionController.class);

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    /**
     * 获取所有职位列表
     * @param departmentId 部门ID，可选参数
     * @return 职位列表
     */
    @GetMapping("/positions")
    public Result<?> getAllPositions(@RequestParam(required = false) Integer departmentId) {
        log.info("接收获取职位列表请求，部门ID：{}", departmentId);
        return positionService.getAllPositions(departmentId);
    }
} 