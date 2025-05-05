package com.asaki0019.enterprisemanagementsb.service.salary.impl;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.exception.BusinessException;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.asaki0019.enterprisemanagementsb.service.salary.SalaryBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 薪资预算服务实现类
 */
@Service
@Transactional
public class SalaryBudgetServiceImpl implements SalaryBudgetService {

    private final SysLogger sysLogger;
    
    @Autowired
    public SalaryBudgetServiceImpl(SysLogger sysLogger) {
        this.sysLogger = sysLogger;
    }

    @Override
    public Result<?> getDepartmentBudgets(String year, String quarter, String department, int page, int pageSize) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getDepartmentBudgets",
                    "year", year,
                    "quarter", quarter,
                    "department", department,
                    "page", String.valueOf(page),
                    "pageSize", String.valueOf(pageSize)
                )
            );
            
            // 生成模拟部门预算数据
            List<Map<String, Object>> budgets = generateDepartmentBudgets();
            
            // 过滤条件
            List<Map<String, Object>> filteredBudgets = budgets;
            
            if (year != null && !year.isEmpty()) {
                filteredBudgets = filteredBudgets.stream()
                        .filter(budget -> year.equals(budget.get("year")))
                        .toList();
            }
            
            if (department != null && !department.isEmpty()) {
                filteredBudgets = filteredBudgets.stream()
                        .filter(budget -> department.equals(budget.get("department")))
                        .toList();
            }
            
            // 分页
            int total = filteredBudgets.size();
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);
            
            List<Map<String, Object>> paginatedBudgets = filteredBudgets.subList(startIndex, endIndex);
            
            // 如果指定了季度，计算该季度的数据
            if (quarter != null && !quarter.isEmpty()) {
                String quarterKey = "Q" + quarter;
                paginatedBudgets.forEach(budget -> {
                    Map<String, Object> quarterlyBudget = (Map<String, Object>) budget.get("quarterlyBudget");
                    Map<String, Object> actualExpenditure = (Map<String, Object>) budget.get("actualExpenditure");
                    
                    if (quarterlyBudget != null && quarterlyBudget.containsKey(quarterKey)) {
                        budget.put("quarterBudget", quarterlyBudget.get(quarterKey));
                        budget.put("quarterActual", actualExpenditure.get(quarterKey));
                    }
                });
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("budgets", paginatedBudgets);
            data.put("total", total);
            data.put("years", Arrays.asList("2022", "2023", "2024"));
            data.put("departments", Arrays.asList("技术部", "销售部", "市场部", "财务部", "人力资源部"));
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getDepartmentBudgets",
                    "year", year,
                    "quarter", quarter,
                    "department", department,
                    "recordsFound", String.valueOf(total),
                    "status", "success"
                )
            );
            
            return Result.success(data);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getDepartmentBudgets",
                    "year", year,
                    "quarter", quarter,
                    "department", department,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getDepartmentBudgets",
                    "year", year,
                    "quarter", quarter,
                    "department", department,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> saveDepartmentBudget(Map<String, Object> request) {
        try {
            String department = request.get("department") != null ? request.get("department").toString() : null;
            String year = request.get("year") != null ? request.get("year").toString() : null;
            
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "saveDepartmentBudget",
                    "department", department,
                    "year", year
                )
            );
            
            // 简单的校验
            if (department == null || year == null) {
                sysLogger.warn(
                    MessageConstructor.constructPlainMessage(
                        "saveDepartmentBudget",
                        "warning", "部门和年份不能为空"
                    )
                );
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "部门和年份不能为空");
            }
            
            // 模拟保存成功
            String id = request.get("id") != null 
                    ? (String) request.get("id") 
                    : "DB" + (new Random().nextInt(900) + 100);
                    
            request.put("id", id);
            request.put("lastUpdated", new Date());
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "saveDepartmentBudget",
                    "department", department,
                    "year", year,
                    "id", id,
                    "status", "success"
                )
            );
            
            return Result.success(request);
        } catch (BusinessException e) {
            String department = request.get("department") != null ? request.get("department").toString() : "unknown";
            String year = request.get("year") != null ? request.get("year").toString() : "unknown";
            
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "saveDepartmentBudget",
                    "department", department,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            String department = request.get("department") != null ? request.get("department").toString() : "unknown";
            String year = request.get("year") != null ? request.get("year").toString() : "unknown";
            
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "saveDepartmentBudget",
                    "department", department,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> getBudgetAllocation(String departmentId, String year) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getBudgetAllocation",
                    "departmentId", departmentId,
                    "year", year
                )
            );
            
            // 获取部门名称
            String department = "技术部";
            if ("DB002".equals(departmentId)) {
                department = "销售部";
            } else if ("DB003".equals(departmentId)) {
                department = "市场部";
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("departmentId", departmentId);
            data.put("department", department);
            data.put("year", year);
            
            // 总预算
            BigDecimal totalBudget = new BigDecimal("9600000");
            data.put("totalBudget", totalBudget);
            
            // 预算分配明细
            List<Map<String, Object>> allocationItems = new ArrayList<>();
            
            // 基本工资
            Map<String, Object> item1 = new HashMap<>();
            item1.put("category", "基本工资");
            item1.put("amount", new BigDecimal("6240000"));
            item1.put("percentage", new BigDecimal("65"));
            item1.put("allocated", new BigDecimal("6200000"));
            item1.put("remaining", new BigDecimal("40000"));
            allocationItems.add(item1);
            
            // 绩效奖金
            Map<String, Object> item2 = new HashMap<>();
            item2.put("category", "绩效奖金");
            item2.put("amount", new BigDecimal("1440000"));
            item2.put("percentage", new BigDecimal("15"));
            item2.put("allocated", new BigDecimal("1430000"));
            item2.put("remaining", new BigDecimal("10000"));
            allocationItems.add(item2);
            
            // 福利津贴
            Map<String, Object> item3 = new HashMap<>();
            item3.put("category", "福利津贴");
            item3.put("amount", new BigDecimal("960000"));
            item3.put("percentage", new BigDecimal("10"));
            item3.put("allocated", new BigDecimal("950000"));
            item3.put("remaining", new BigDecimal("10000"));
            allocationItems.add(item3);
            
            // 加班费用
            Map<String, Object> item4 = new HashMap<>();
            item4.put("category", "加班费用");
            item4.put("amount", new BigDecimal("480000"));
            item4.put("percentage", new BigDecimal("5"));
            item4.put("allocated", new BigDecimal("470000"));
            item4.put("remaining", new BigDecimal("10000"));
            allocationItems.add(item4);
            
            // 培训费用
            Map<String, Object> item5 = new HashMap<>();
            item5.put("category", "培训费用");
            item5.put("amount", new BigDecimal("480000"));
            item5.put("percentage", new BigDecimal("5"));
            item5.put("allocated", new BigDecimal("300000"));
            item5.put("remaining", new BigDecimal("180000"));
            allocationItems.add(item5);
            
            data.put("allocationItems", allocationItems);
            
            // 计算已分配和剩余预算
            BigDecimal allocated = allocationItems.stream()
                    .map(item -> (BigDecimal) item.get("allocated"))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
            BigDecimal remaining = allocationItems.stream()
                    .map(item -> (BigDecimal) item.get("remaining"))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
            data.put("totalAllocated", allocated);
            data.put("totalRemaining", remaining);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getBudgetAllocation",
                    "departmentId", departmentId,
                    "department", department,
                    "year", year,
                    "status", "success"
                )
            );
            
            return Result.success(data);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getBudgetAllocation",
                    "departmentId", departmentId,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getBudgetAllocation",
                    "departmentId", departmentId,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> saveBudgetAllocation(Map<String, Object> request) {
        try {
            String departmentId = request.get("departmentId") != null ? request.get("departmentId").toString() : null;
            String year = request.get("year") != null ? request.get("year").toString() : null;
            
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "saveBudgetAllocation",
                    "departmentId", departmentId,
                    "year", year
                )
            );
            
            // 简单的校验
            if (departmentId == null || year == null) {
                sysLogger.warn(
                    MessageConstructor.constructPlainMessage(
                        "saveBudgetAllocation",
                        "warning", "部门ID和年份不能为空"
                    )
                );
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "部门ID和年份不能为空");
            }
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "saveBudgetAllocation",
                    "departmentId", departmentId,
                    "year", year,
                    "status", "success"
                )
            );
            
            return Result.success(request);
        } catch (BusinessException e) {
            String departmentId = request.get("departmentId") != null ? request.get("departmentId").toString() : "unknown";
            String year = request.get("year") != null ? request.get("year").toString() : "unknown";
            
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "saveBudgetAllocation",
                    "departmentId", departmentId,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            String departmentId = request.get("departmentId") != null ? request.get("departmentId").toString() : "unknown";
            String year = request.get("year") != null ? request.get("year").toString() : "unknown";
            
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "saveBudgetAllocation",
                    "departmentId", departmentId,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> getBudgetExecutionReport(String department, String year, String quarter) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getBudgetExecutionReport",
                    "department", department,
                    "year", year,
                    "quarter", quarter
                )
            );
            
            Map<String, Object> data = new HashMap<>();
            data.put("year", year);
            data.put("department", department);
            
            if (quarter != null && !quarter.isEmpty()) {
                data.put("quarter", quarter);
            }
            
            // 预算执行概况
            Map<String, Object> overview = new HashMap<>();
            overview.put("totalBudget", 9600000);
            overview.put("actualExpenditure", 9120000);
            overview.put("executionRate", 95.0);
            overview.put("budgetBalance", 480000);
            
            if (quarter != null && !quarter.isEmpty()) {
                int q = Integer.parseInt(quarter);
                switch (q) {
                    case 1:
                        overview.put("quarterBudget", 2400000);
                        overview.put("quarterActual", 2350000);
                        overview.put("quarterExecutionRate", 97.9);
                        break;
                    case 2:
                        overview.put("quarterBudget", 2400000);
                        overview.put("quarterActual", 2380000);
                        overview.put("quarterExecutionRate", 99.2);
                        break;
                    case 3:
                        overview.put("quarterBudget", 2400000);
                        overview.put("quarterActual", 2320000);
                        overview.put("quarterExecutionRate", 96.7);
                        break;
                    case 4:
                        overview.put("quarterBudget", 2400000);
                        overview.put("quarterActual", 2070000);
                        overview.put("quarterExecutionRate", 86.3);
                        break;
                }
            }
            
            data.put("overview", overview);
            
            // 各类别预算执行情况
            List<Map<String, Object>> categoryExecutions = new ArrayList<>();
            
            String[] categories = {"基本工资", "绩效奖金", "福利津贴", "加班费用", "培训费用"};
            double[] budgetRatios = {0.65, 0.15, 0.1, 0.05, 0.05};
            double[] executionRates = {0.98, 0.95, 0.93, 0.9, 0.85};
            
            for (int i = 0; i < categories.length; i++) {
                Map<String, Object> category = new HashMap<>();
                category.put("category", categories[i]);
                
                double categoryBudget = 9600000 * budgetRatios[i];
                double categoryActual = categoryBudget * executionRates[i];
                
                category.put("budget", categoryBudget);
                category.put("actual", categoryActual);
                category.put("executionRate", executionRates[i] * 100);
                category.put("variance", categoryBudget - categoryActual);
                
                categoryExecutions.add(category);
            }
            
            data.put("categoryExecutions", categoryExecutions);
            
            // 月度预算执行趋势
            List<Map<String, Object>> monthlyTrends = new ArrayList<>();
            
            int[] monthlyBudgets = {800000, 800000, 800000, 800000, 800000, 800000, 800000, 800000, 800000, 800000, 800000, 800000};
            double[] monthlyRates = {0.92, 0.94, 0.96, 0.93, 0.95, 0.97, 0.94, 0.96, 0.98, 0.93, 0.95, 0.83};
            
            for (int i = 0; i < 12; i++) {
                Map<String, Object> month = new HashMap<>();
                month.put("month", String.format("%s-%02d", year, i + 1));
                month.put("budget", monthlyBudgets[i]);
                month.put("actual", monthlyBudgets[i] * monthlyRates[i]);
                month.put("executionRate", monthlyRates[i] * 100);
                
                monthlyTrends.add(month);
            }
            
            data.put("monthlyTrends", monthlyTrends);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getBudgetExecutionReport",
                    "department", department,
                    "year", year,
                    "quarter", quarter,
                    "status", "success"
                )
            );
            
            return Result.success(data);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getBudgetExecutionReport",
                    "department", department,
                    "year", year,
                    "quarter", quarter,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getBudgetExecutionReport",
                    "department", department,
                    "year", year,
                    "quarter", quarter,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 辅助方法：生成部门预算数据
    private List<Map<String, Object>> generateDepartmentBudgets() {
        List<Map<String, Object>> budgets = new ArrayList<>();
        
        // 技术部2023年预算
        Map<String, Object> budget1 = new HashMap<>();
        budget1.put("id", "DB001");
        budget1.put("department", "技术部");
        budget1.put("year", "2023");
        budget1.put("totalBudget", 9600000);
        budget1.put("allocatedBudget", 9350000);
        budget1.put("remainingBudget", 250000);
        
        Map<String, Object> quarterlyBudget1 = new HashMap<>();
        quarterlyBudget1.put("Q1", 2400000);
        quarterlyBudget1.put("Q2", 2400000);
        quarterlyBudget1.put("Q3", 2400000);
        quarterlyBudget1.put("Q4", 2400000);
        budget1.put("quarterlyBudget", quarterlyBudget1);
        
        Map<String, Object> actualExpenditure1 = new HashMap<>();
        actualExpenditure1.put("Q1", 2350000);
        actualExpenditure1.put("Q2", 2380000);
        actualExpenditure1.put("Q3", 2320000);
        actualExpenditure1.put("Q4", 2070000);
        budget1.put("actualExpenditure", actualExpenditure1);
        
        Map<String, Object> headcount1 = new HashMap<>();
        headcount1.put("planned", 40);
        headcount1.put("actual", 38);
        budget1.put("headcount", headcount1);
        
        budget1.put("avgSalary", 18250);
        budget1.put("status", "执行中");
        budget1.put("lastUpdated", "2023-12-10");
        
        budgets.add(budget1);
        
        // 销售部2023年预算
        Map<String, Object> budget2 = new HashMap<>();
        budget2.put("id", "DB002");
        budget2.put("department", "销售部");
        budget2.put("year", "2023");
        budget2.put("totalBudget", 7200000);
        budget2.put("allocatedBudget", 7150000);
        budget2.put("remainingBudget", 50000);
        
        Map<String, Object> quarterlyBudget2 = new HashMap<>();
        quarterlyBudget2.put("Q1", 1800000);
        quarterlyBudget2.put("Q2", 1800000);
        quarterlyBudget2.put("Q3", 1800000);
        quarterlyBudget2.put("Q4", 1800000);
        budget2.put("quarterlyBudget", quarterlyBudget2);
        
        Map<String, Object> actualExpenditure2 = new HashMap<>();
        actualExpenditure2.put("Q1", 1780000);
        actualExpenditure2.put("Q2", 1810000);
        actualExpenditure2.put("Q3", 1790000);
        actualExpenditure2.put("Q4", 1770000);
        budget2.put("actualExpenditure", actualExpenditure2);
        
        Map<String, Object> headcount2 = new HashMap<>();
        headcount2.put("planned", 30);
        headcount2.put("actual", 31);
        budget2.put("headcount", headcount2);
        
        budget2.put("avgSalary", 19220);
        budget2.put("status", "执行中");
        budget2.put("lastUpdated", "2023-12-08");
        
        budgets.add(budget2);
        
        // 技术部2024年预算
        Map<String, Object> budget3 = new HashMap<>();
        budget3.put("id", "DB006");
        budget3.put("department", "技术部");
        budget3.put("year", "2024");
        budget3.put("totalBudget", 10800000);
        budget3.put("allocatedBudget", 0);
        budget3.put("remainingBudget", 10800000);
        
        Map<String, Object> quarterlyBudget3 = new HashMap<>();
        quarterlyBudget3.put("Q1", 2700000);
        quarterlyBudget3.put("Q2", 2700000);
        quarterlyBudget3.put("Q3", 2700000);
        quarterlyBudget3.put("Q4", 2700000);
        budget3.put("quarterlyBudget", quarterlyBudget3);
        
        Map<String, Object> actualExpenditure3 = new HashMap<>();
        actualExpenditure3.put("Q1", 0);
        actualExpenditure3.put("Q2", 0);
        actualExpenditure3.put("Q3", 0);
        actualExpenditure3.put("Q4", 0);
        budget3.put("actualExpenditure", actualExpenditure3);
        
        Map<String, Object> headcount3 = new HashMap<>();
        headcount3.put("planned", 45);
        headcount3.put("actual", 0);
        budget3.put("headcount", headcount3);
        
        budget3.put("avgSalary", 20000);
        budget3.put("status", "计划中");
        budget3.put("lastUpdated", "2023-11-15");
        
        budgets.add(budget3);
        
        return budgets;
    }
} 