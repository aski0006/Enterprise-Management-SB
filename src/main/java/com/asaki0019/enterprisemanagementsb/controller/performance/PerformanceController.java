package com.asaki0019.enterprisemanagementsb.controller.performance;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.service.performance.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 绩效管理控制器
 */
@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "http://localhost:5001")
public class PerformanceController {
    
    private final PerformanceService performanceService;
    
    @Autowired
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }
    
    /**
     * 获取员工绩效评估列表
     */
    @GetMapping("/employee-evaluations")
    public Result<?> getEmployeeEvaluations(@RequestParam(required = false) String employeeId,
                                              @RequestParam(required = false) String year,
                                              @RequestParam(required = false) String quarter,
                                              @RequestParam(required = false, defaultValue = "0") Integer page,
                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return performanceService.getEmployeeEvaluations(employeeId, year, quarter, page, pageSize);
    }
    
    /**
     * 获取评估详情
     */
    @GetMapping("/evaluation-detail")
    public Result<?> getEvaluationDetail(@RequestParam String id) {
        return performanceService.getEvaluationDetail(id);
    }
    
    /**
     * 获取部门绩效汇总
     */
    @GetMapping("/department-summary")
    public Result<?> getDepartmentSummary(@RequestParam(required = false) String year,
                                            @RequestParam(required = false) String quarter,
                                            @RequestParam(required = false) String department) {
        return performanceService.getDepartmentSummary(year, quarter, department);
    }
    
    /**
     * 获取绩效评估标准
     */
    @GetMapping("/criteria")
    public Result<?> getPerformanceCriteria(@RequestParam(required = false) String departmentId,
                                              @RequestParam(required = false) String position) {
        return performanceService.getPerformanceCriteria(departmentId, position);
    }
} 