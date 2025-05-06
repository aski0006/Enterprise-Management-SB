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
    
    @Override
    public Result<?> getBudgetForecast(String year, String department, Integer months) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getBudgetForecast",
                    "year", year,
                    "department", department,
                    "months", String.valueOf(months)
                )
            );
            
            // 如果没有指定预测月数，默认预测12个月
            int forecastMonths = months != null ? months : 12;
            
            // 获取基准预算数据
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
            
            if (filteredBudgets.isEmpty()) {
                sysLogger.warn(
                    MessageConstructor.constructPlainMessage(
                        "getBudgetForecast",
                        "warning", "未找到匹配的预算数据"
                    )
                );
                return Result.failure(ErrorCode.NOT_FOUND, "未找到匹配的预算数据");
            }
            
            // 生成预测数据
            List<Map<String, Object>> forecastData = new ArrayList<>();
            Random random = new Random();
            
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(year));
            calendar.set(Calendar.MONTH, 0); // 从1月开始
            
            // 获取当前实际消费的基准值
            Map<String, BigDecimal> baseConsumption = new HashMap<>();
            Map<String, BigDecimal> baseBudget = new HashMap<>();
            
            // 对每个部门，生成预测数据
            for (Map<String, Object> budget : filteredBudgets) {
                String departmentName = (String) budget.get("department");
                BigDecimal annualBudget = new BigDecimal(budget.get("annualBudget").toString());
                BigDecimal monthlyBudget = annualBudget.divide(new BigDecimal("12"), 2, BigDecimal.ROUND_HALF_UP);
                
                baseBudget.put(departmentName, monthlyBudget);
                
                // 生成当前实际消费的基准值
                Map<String, Object> actualExpenditure = (Map<String, Object>) budget.get("actualExpenditure");
                if (actualExpenditure != null) {
                    BigDecimal quarterlyExpenditure = BigDecimal.ZERO;
                    for (Object value : actualExpenditure.values()) {
                        quarterlyExpenditure = quarterlyExpenditure.add(new BigDecimal(value.toString()));
                    }
                    
                    // 平均每月消费
                    BigDecimal monthlyExpenditure = quarterlyExpenditure.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_HALF_UP);
                    baseConsumption.put(departmentName, monthlyExpenditure);
                } else {
                    // 如果没有实际消费数据，使用预算的90%作为基准
                    baseConsumption.put(departmentName, monthlyBudget.multiply(new BigDecimal("0.9")));
                }
            }
            
            // 生成每个月的预测数据
            for (int i = 0; i < forecastMonths; i++) {
                Map<String, Object> monthData = new HashMap<>();
                String monthKey = String.format("%04d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
                monthData.put("month", monthKey);
                
                // 部门数据
                List<Map<String, Object>> departmentData = new ArrayList<>();
                
                // 总预测消费
                BigDecimal totalForecast = BigDecimal.ZERO;
                BigDecimal totalBudget = BigDecimal.ZERO;
                
                for (Map<String, Object> budget : filteredBudgets) {
                    String departmentName = (String) budget.get("department");
                    BigDecimal monthlyBudget = baseBudget.get(departmentName);
                    BigDecimal monthlyConsumption = baseConsumption.get(departmentName);
                    
                    // 根据趋势调整预测值
                    double trend = 1.0 + (random.nextDouble() * 0.06 - 0.02); // -2% 到 +4% 的随机趋势
                    BigDecimal forecastConsumption = monthlyConsumption.multiply(new BigDecimal(trend)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    
                    // 季度性调整（第1季度+5%，第2季度+2%，第3季度-2%，第4季度+10%）
                    int quarter = (calendar.get(Calendar.MONTH) / 3) + 1;
                    double seasonalFactor;
                    switch (quarter) {
                        case 1: seasonalFactor = 1.05; break;
                        case 2: seasonalFactor = 1.02; break;
                        case 3: seasonalFactor = 0.98; break;
                        case 4: seasonalFactor = 1.10; break;
                        default: seasonalFactor = 1.0;
                    }
                    
                    forecastConsumption = forecastConsumption.multiply(new BigDecimal(seasonalFactor)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    
                    // 更新基准值（加入一定的趋势影响）
                    monthlyConsumption = monthlyConsumption.multiply(new BigDecimal(1.0 + (random.nextDouble() * 0.02 - 0.005))).setScale(2, BigDecimal.ROUND_HALF_UP);
                    baseConsumption.put(departmentName, monthlyConsumption);
                    
                    Map<String, Object> deptData = new HashMap<>();
                    deptData.put("department", departmentName);
                    deptData.put("budget", monthlyBudget);
                    deptData.put("forecast", forecastConsumption);
                    deptData.put("variance", monthlyBudget.subtract(forecastConsumption));
                    deptData.put("variancePercentage", 
                            forecastConsumption.multiply(new BigDecimal("100"))
                                .divide(monthlyBudget, 2, BigDecimal.ROUND_HALF_UP)
                                .subtract(new BigDecimal("100"))
                                .toString() + "%");
                    
                    departmentData.add(deptData);
                    
                    totalForecast = totalForecast.add(forecastConsumption);
                    totalBudget = totalBudget.add(monthlyBudget);
                }
                
                monthData.put("departments", departmentData);
                monthData.put("totalBudget", totalBudget);
                monthData.put("totalForecast", totalForecast);
                monthData.put("totalVariance", totalBudget.subtract(totalForecast));
                monthData.put("totalVariancePercentage", 
                        totalForecast.multiply(new BigDecimal("100"))
                            .divide(totalBudget, 2, BigDecimal.ROUND_HALF_UP)
                            .subtract(new BigDecimal("100"))
                            .toString() + "%");
                
                // 预警标识
                boolean hasWarning = totalForecast.compareTo(totalBudget) > 0;
                monthData.put("hasWarning", hasWarning);
                
                forecastData.add(monthData);
                
                // 下一个月
                calendar.add(Calendar.MONTH, 1);
            }
            
            // 生成月度总预测趋势
            List<Map<String, Object>> monthlyTrend = new ArrayList<>();
            for (Map<String, Object> monthData : forecastData) {
                Map<String, Object> trend = new HashMap<>();
                trend.put("month", monthData.get("month"));
                trend.put("budget", monthData.get("totalBudget"));
                trend.put("forecast", monthData.get("totalForecast"));
                trend.put("variance", monthData.get("totalVariance"));
                
                monthlyTrend.add(trend);
            }
            
            // 计算总预测
            BigDecimal totalBudget = BigDecimal.ZERO;
            BigDecimal totalForecast = BigDecimal.ZERO;
            
            for (Map<String, Object> monthData : forecastData) {
                totalBudget = totalBudget.add(new BigDecimal(monthData.get("totalBudget").toString()));
                totalForecast = totalForecast.add(new BigDecimal(monthData.get("totalForecast").toString()));
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("year", year);
            data.put("forecastMonths", forecastMonths);
            data.put("monthlyData", forecastData);
            data.put("monthlyTrend", monthlyTrend);
            data.put("totalBudget", totalBudget);
            data.put("totalForecast", totalForecast);
            data.put("totalVariance", totalBudget.subtract(totalForecast));
            data.put("totalVariancePercentage", 
                    totalForecast.multiply(new BigDecimal("100"))
                        .divide(totalBudget, 2, BigDecimal.ROUND_HALF_UP)
                        .subtract(new BigDecimal("100"))
                        .toString() + "%");
            
            if (department != null && !department.isEmpty()) {
                data.put("department", department);
            }
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getBudgetForecast",
                    "year", year,
                    "department", department,
                    "months", String.valueOf(forecastMonths),
                    "status", "success"
                )
            );
            
            return Result.success(data);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getBudgetForecast",
                    "year", year,
                    "department", department,
                    "months", String.valueOf(months),
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getBudgetForecast",
                    "year", year,
                    "department", department,
                    "months", String.valueOf(months),
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
    
    @Override
    public Result<?> getBudgetAdjustmentHistory(String departmentId, String year, int page, int pageSize) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getBudgetAdjustmentHistory",
                    "departmentId", departmentId,
                    "year", year,
                    "page", String.valueOf(page),
                    "pageSize", String.valueOf(pageSize)
                )
            );
            
            // 获取部门名称
            String department = "技术部";
            if ("DB002".equals(departmentId)) {
                department = "销售部";
            } else if ("DB003".equals(departmentId)) {
                department = "市场部";
            } else if ("DB004".equals(departmentId)) {
                department = "财务部";
            } else if ("DB005".equals(departmentId)) {
                department = "人力资源部";
            }
            
            // 生成调整历史记录
            List<Map<String, Object>> adjustments = new ArrayList<>();
            
            // 过滤年份
            String[] years = {"2023", "2024"};
            List<String> filteredYears = Arrays.asList(years);
            
            if (year != null && !year.isEmpty()) {
                filteredYears = filteredYears.stream()
                    .filter(y -> y.equals(year))
                    .toList();
            }
            
            Random random = new Random();
            String[] reasons = {
                "部门扩张需求",
                "人员结构调整",
                "薪资市场竞争力提升",
                "年度预算调整",
                "季度业绩奖金调整",
                "项目特殊需求",
                "福利政策调整",
                "人员优化调整"
            };
            
            String[] adjustmentTypes = {
                "增加",
                "减少"
            };
            
            String[] approvers = {
                "张总监",
                "李经理",
                "王财务总监",
                "刘HR总监",
                "黄总裁"
            };
            
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -1);
            
            // 为每一年生成调整记录
            for (String yr : filteredYears) {
                // 每年生成3-6条记录
                int recordCount = random.nextInt(4) + 3;
                
                for (int i = 0; i < recordCount; i++) {
                    Map<String, Object> adjustment = new HashMap<>();
                    
                    // 生成唯一ID
                    adjustment.put("id", "ADJ" + (10000 + random.nextInt(90000)));
                    adjustment.put("departmentId", departmentId);
                    adjustment.put("department", department);
                    adjustment.put("year", yr);
                    
                    // 随机生成调整日期
                    calendar.set(Calendar.YEAR, Integer.parseInt(yr));
                    calendar.set(Calendar.MONTH, random.nextInt(12));
                    calendar.set(Calendar.DAY_OF_MONTH, random.nextInt(28) + 1);
                    adjustment.put("adjustmentDate", String.format("%d-%02d-%02d", 
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)));
                    
                    // 调整类型和金额
                    String adjustmentType = adjustmentTypes[random.nextInt(adjustmentTypes.length)];
                    adjustment.put("adjustmentType", adjustmentType);
                    
                    double amount = (random.nextInt(500) + 50) * 1000.0;
                    adjustment.put("amount", amount);
                    
                    // 调整前后预算
                    double baseBudget = 9600000.0;
                    if ("销售部".equals(department)) {
                        baseBudget = 7200000.0;
                    } else if ("市场部".equals(department)) {
                        baseBudget = 5400000.0;
                    } else if ("财务部".equals(department)) {
                        baseBudget = 4800000.0;
                    } else if ("人力资源部".equals(department)) {
                        baseBudget = 3600000.0;
                    }
                    
                    // 随机调整基准预算
                    baseBudget = baseBudget * (1 + (random.nextDouble() * 0.1 - 0.05));
                    
                    double previousBudget = baseBudget;
                    double currentBudget;
                    
                    if ("增加".equals(adjustmentType)) {
                        currentBudget = previousBudget + amount;
                    } else {
                        currentBudget = previousBudget - amount;
                    }
                    
                    adjustment.put("previousBudget", previousBudget);
                    adjustment.put("currentBudget", currentBudget);
                    
                    // 调整原因
                    adjustment.put("reason", reasons[random.nextInt(reasons.length)]);
                    
                    // 审批人和状态
                    adjustment.put("approver", approvers[random.nextInt(approvers.length)]);
                    adjustment.put("status", "已审批");
                    
                    // 影响季度
                    int quarter = calendar.get(Calendar.MONTH) / 3 + 1;
                    adjustment.put("affectedQuarter", "Q" + quarter);
                    
                    // 计算调整百分比
                    double percentage = (amount / previousBudget) * 100;
                    adjustment.put("adjustmentPercentage", String.format("%.2f%%", percentage));
                    
                    // 备注
                    adjustment.put("remarks", "预算" + adjustmentType + "，用于" + adjustment.get("reason"));
                    
                    adjustments.add(adjustment);
                }
            }
            
            // 根据调整日期倒序排序
            adjustments.sort((a, b) -> {
                String dateA = (String) a.get("adjustmentDate");
                String dateB = (String) b.get("adjustmentDate");
                return dateB.compareTo(dateA);
            });
            
            // 分页处理
            int total = adjustments.size();
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);
            
            List<Map<String, Object>> paginatedAdjustments;
            if (startIndex < total) {
                paginatedAdjustments = adjustments.subList(startIndex, endIndex);
            } else {
                paginatedAdjustments = new ArrayList<>();
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("adjustments", paginatedAdjustments);
            data.put("total", total);
            data.put("department", department);
            data.put("departmentId", departmentId);
            
            if (year != null && !year.isEmpty()) {
                data.put("year", year);
            }
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getBudgetAdjustmentHistory",
                    "departmentId", departmentId,
                    "year", year,
                    "recordsFound", String.valueOf(total),
                    "status", "success"
                )
            );
            
            return Result.success(data);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getBudgetAdjustmentHistory",
                    "departmentId", departmentId,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getBudgetAdjustmentHistory",
                    "departmentId", departmentId,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public Result<?> getBudgetExecutionAnalysis(String department, String year, String quarter) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getBudgetExecutionAnalysis",
                    "department", department,
                    "year", year,
                    "quarter", quarter
                )
            );
            
            // 默认年份为当前年份
            if (year == null || year.isEmpty()) {
                Calendar calendar = Calendar.getInstance();
                year = String.valueOf(calendar.get(Calendar.YEAR));
            }
            
            // 生成预算执行分析数据
            Map<String, Object> analysisData = new HashMap<>();
            
            // 汇总数据
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalBudget", 28000000.0);
            summary.put("currentExpenditure", 19600000.0);
            summary.put("remainingBudget", 8400000.0);
            
            double executionRate = 70.0;  // 预算执行率，70%
            summary.put("executionRate", executionRate + "%");
            
            // 执行状态
            String executionStatus;
            if (executionRate < 60) {
                executionStatus = "进度滞后";
            } else if (executionRate < 80) {
                executionStatus = "正常执行";
            } else if (executionRate < 95) {
                executionStatus = "执行良好";
            } else {
                executionStatus = "预算紧张";
            }
            summary.put("executionStatus", executionStatus);
            
            // 计算预计结余
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            double projectedRemainder = 0;
            
            if (year.equals(String.valueOf(calendar.get(Calendar.YEAR)))) {
                double monthlyRate = executionRate / currentMonth;
                double projectedRate = monthlyRate * 12;
                
                if (projectedRate > 100) {
                    // 预计超支
                    double overBudgetRate = projectedRate - 100;
                    double overBudgetAmount = (Double) summary.get("totalBudget") * (overBudgetRate / 100);
                    projectedRemainder = -overBudgetAmount;
                } else {
                    // 预计结余
                    double remainderRate = 100 - projectedRate;
                    projectedRemainder = (Double) summary.get("totalBudget") * (remainderRate / 100);
                }
            }
            summary.put("projectedRemainder", projectedRemainder);
            
            analysisData.put("summary", summary);
            
            // 部门执行情况
            List<Map<String, Object>> departmentExecutions = new ArrayList<>();
            
            // 获取部门预算基础数据
            List<Map<String, Object>> budgets = generateDepartmentBudgets();
            for (Map<String, Object> budget : budgets) {
                if (department != null && !department.isEmpty() && 
                    !department.equals(budget.get("department"))) {
                    continue;
                }
                
                if (!year.equals(budget.get("year"))) {
                    continue;
                }
                
                // 只针对特定季度的数据
                if (quarter != null && !quarter.isEmpty()) {
                    // 如果有季度筛选，且当前预算不属于该季度，则跳过
                    if (!quarter.equals(budget.get("quarter"))) {
                        continue;
                    }
                }
                
                Map<String, Object> deptExecution = new HashMap<>();
                deptExecution.put("departmentId", budget.get("departmentId"));
                deptExecution.put("department", budget.get("department"));
                deptExecution.put("year", budget.get("year"));
                
                if (quarter != null && !quarter.isEmpty()) {
                    deptExecution.put("quarter", budget.get("quarter"));
                }
                
                double departmentBudget = Double.parseDouble(budget.get("budgetAmount").toString());
                deptExecution.put("budget", departmentBudget);
                
                // 实际支出数据
                Map<String, Object> actualExpenditure = (Map<String, Object>) budget.get("actualExpenditure");
                if (actualExpenditure != null) {
                    double totalExpenditure = 0;
                    for (Object value : actualExpenditure.values()) {
                        totalExpenditure += Double.parseDouble(value.toString());
                    }
                    deptExecution.put("expenditure", totalExpenditure);
                    deptExecution.put("remaining", departmentBudget - totalExpenditure);
                    
                    double deptExecutionRate = (totalExpenditure / departmentBudget) * 100;
                    deptExecutionRate = Math.round(deptExecutionRate * 10) / 10.0;
                    deptExecution.put("executionRate", deptExecutionRate + "%");
                    
                    // 执行状态
                    String deptStatus;
                    if (deptExecutionRate < 60) {
                        deptStatus = "进度滞后";
                    } else if (deptExecutionRate < 80) {
                        deptStatus = "正常执行";
                    } else if (deptExecutionRate < 95) {
                        deptStatus = "执行良好";
                    } else {
                        deptStatus = "预算紧张";
                    }
                    deptExecution.put("status", deptStatus);
                    
                    // 同比分析（假设数据）
                    Random random = new Random();
                    double lastYearRate = deptExecutionRate - 10 + random.nextDouble() * 20;
                    lastYearRate = Math.round(lastYearRate * 10) / 10.0;
                    deptExecution.put("lastYearRate", lastYearRate + "%");
                    
                    double yearOverYearChange = deptExecutionRate - lastYearRate;
                    yearOverYearChange = Math.round(yearOverYearChange * 10) / 10.0;
                    deptExecution.put("yearOverYearChange", (yearOverYearChange > 0 ? "+" : "") + yearOverYearChange + "%");
                    
                    // 月度执行明细
                    List<Map<String, Object>> monthlyDetails = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : actualExpenditure.entrySet()) {
                        Map<String, Object> monthDetail = new HashMap<>();
                        monthDetail.put("month", entry.getKey());
                        monthDetail.put("expenditure", entry.getValue());
                        
                        // 月度预算，按季度平均分配
                        double monthlyBudget = departmentBudget / 3;
                        monthDetail.put("budget", monthlyBudget);
                        
                        double monthRate = (Double.parseDouble(entry.getValue().toString()) / monthlyBudget) * 100;
                        monthRate = Math.round(monthRate * 10) / 10.0;
                        monthDetail.put("executionRate", monthRate + "%");
                        
                        monthlyDetails.add(monthDetail);
                    }
                    deptExecution.put("monthlyDetails", monthlyDetails);
                }
                
                departmentExecutions.add(deptExecution);
            }
            analysisData.put("departmentExecutions", departmentExecutions);
            
            // 预算执行趋势分析
            List<Map<String, Object>> trends = new ArrayList<>();
            
            // 按季度生成趋势数据
            String[] quarters = {"Q1", "Q2", "Q3", "Q4"};
            double[] rateProgression = {25.0, 50.0, 75.0, 100.0};  // 理想执行率
            
            Random random = new Random();
            double cumulativeRate = 0;
            
            for (int i = 0; i < quarters.length; i++) {
                Map<String, Object> trend = new HashMap<>();
                trend.put("period", quarters[i]);
                
                // 理想执行率
                trend.put("targetRate", rateProgression[i] + "%");
                
                // 实际执行率 - 加入一些随机波动
                double actualRate;
                if (i == 0) {
                    actualRate = rateProgression[i] - 5 + random.nextDouble() * 10;
                } else {
                    // 累积增长
                    double increment = (rateProgression[i] - rateProgression[i-1]) * (0.8 + random.nextDouble() * 0.4);
                    actualRate = cumulativeRate + increment;
                }
                actualRate = Math.round(actualRate * 10) / 10.0;
                cumulativeRate = actualRate;
                
                trend.put("actualRate", actualRate + "%");
                
                // 偏差
                double variance = actualRate - rateProgression[i];
                variance = Math.round(variance * 10) / 10.0;
                trend.put("variance", (variance > 0 ? "+" : "") + variance + "%");
                
                trends.add(trend);
            }
            analysisData.put("trends", trends);
            
            // 预算执行风险分析
            List<Map<String, Object>> risks = new ArrayList<>();
            
            // 生成一些风险条目
            String[] riskTypes = {"预算超支风险", "预算执行进度风险", "预算分配不合理风险", "预算调整频繁风险"};
            String[] riskLevels = {"低", "中", "高"};
            String[] riskDescriptions = {
                "部分部门可能存在超支风险，需密切关注",
                "预算执行进度滞后，可能影响年度目标完成",
                "预算分配可能不够合理，需要优化分配策略",
                "预算调整过于频繁，影响预算管理稳定性"
            };
            
            for (int i = 0; i < riskTypes.length; i++) {
                Map<String, Object> risk = new HashMap<>();
                risk.put("type", riskTypes[i]);
                
                int levelIndex = random.nextInt(riskLevels.length);
                risk.put("level", riskLevels[levelIndex]);
                
                risk.put("description", riskDescriptions[i]);
                
                // 建议措施
                StringBuilder suggestions = new StringBuilder();
                switch (i) {
                    case 0:
                        suggestions.append("加强预算控制，对超支部门进行预警；");
                        suggestions.append("分析超支原因，必要时调整预算分配。");
                        break;
                    case 1:
                        suggestions.append("加快预算执行进度，定期跟进执行情况；");
                        suggestions.append("分析滞后原因，提供必要支持。");
                        break;
                    case 2:
                        suggestions.append("基于历史数据和实际需求重新评估预算分配；");
                        suggestions.append("建立更科学的预算分配模型。");
                        break;
                    case 3:
                        suggestions.append("完善预算调整流程，减少非必要调整；");
                        suggestions.append("提高预算编制精确度，减少后期调整需求。");
                        break;
                }
                risk.put("suggestions", suggestions.toString());
                risks.add(risk);
            }
            analysisData.put("risks", risks);
            
            // 加入详细过滤信息
            analysisData.put("year", year);
            if (department != null && !department.isEmpty()) {
                analysisData.put("department", department);
            }
            if (quarter != null && !quarter.isEmpty()) {
                analysisData.put("quarter", quarter);
            }
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getBudgetExecutionAnalysis",
                    "department", department,
                    "year", year,
                    "quarter", quarter,
                    "status", "success"
                )
            );
            
            return Result.success(analysisData);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getBudgetExecutionAnalysis",
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
                    "getBudgetExecutionAnalysis",
                    "department", department,
                    "year", year,
                    "quarter", quarter,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
} 