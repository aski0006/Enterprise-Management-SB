package com.asaki0019.enterprisemanagementsb.service.salary.impl;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.exception.BusinessException;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.asaki0019.enterprisemanagementsb.service.salary.SalaryReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 薪资报表服务实现类
 */
@Service
@Transactional
public class SalaryReportsServiceImpl implements SalaryReportsService {

    private final SysLogger sysLogger;
    
    @Autowired
    public SalaryReportsServiceImpl(SysLogger sysLogger) {
        this.sysLogger = sysLogger;
    }

    @Override
    public Result<?> getEmployeeSalaryTrends(String employeeId, String startDate, String endDate) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryTrends",
                    "employeeId", employeeId,
                    "startDate", startDate,
                    "endDate", endDate
                )
            );
            
            if (employeeId == null || employeeId.isEmpty()) {
                sysLogger.warn(
                    MessageConstructor.constructPlainMessage(
                        "getEmployeeSalaryTrends",
                        "warning", "员工ID不能为空"
                    )
                );
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "员工ID不能为空");
            }
            
            // 基本薪资数据
            Map<String, Object> baseData = new HashMap<>();
            baseData.put("employeeId", employeeId);
            baseData.put("employeeName", "员工" + employeeId.replaceAll("\\D", ""));
            baseData.put("department", getRandomDepartment());
            baseData.put("joinDate", "2020-06-01");
            
            // 生成薪资趋势数据
            List<Map<String, Object>> salaryRecords = generateSalaryTrends(employeeId, startDate, endDate);
            
            // 计算统计数据
            Map<String, Object> statistics = calculateStatistics(salaryRecords);
            
            Map<String, Object> data = new HashMap<>();
            data.put("employeeInfo", baseData);
            data.put("salaryRecords", salaryRecords);
            data.put("statistics", statistics);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryTrends",
                    "employeeId", employeeId,
                    "recordCount", String.valueOf(salaryRecords.size()),
                    "status", "success"
                )
            );
            
            return Result.success(data);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryTrends",
                    "employeeId", employeeId,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryTrends",
                    "employeeId", employeeId,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> getDepartmentComparison(String year, String quarter) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getDepartmentComparison",
                    "year", year,
                    "quarter", quarter
                )
            );
            
            // 生成部门薪资对比数据
            List<Map<String, Object>> departmentData = generateDepartmentComparisonData(year, quarter);
            
            // 计算公司整体数据
            Map<String, Object> companyData = calculateCompanyData(departmentData);
            
            Map<String, Object> data = new HashMap<>();
            data.put("departmentData", departmentData);
            data.put("companyData", companyData);
            data.put("year", year);
            data.put("quarter", quarter);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getDepartmentComparison",
                    "year", year,
                    "quarter", quarter,
                    "departmentCount", String.valueOf(departmentData.size()),
                    "status", "success"
                )
            );
            
            return Result.success(data);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getDepartmentComparison",
                    "year", year,
                    "quarter", quarter,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getDepartmentComparison",
                    "year", year,
                    "quarter", quarter,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> getSalaryDistribution(String department, String year) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getSalaryDistribution",
                    "department", department,
                    "year", year
                )
            );
            
            // 生成薪资分布数据
            Map<String, Object> distributionData = generateSalaryDistributionData(department, year);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getSalaryDistribution",
                    "department", department,
                    "year", year,
                    "status", "success"
                )
            );
            
            return Result.success(distributionData);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getSalaryDistribution",
                    "department", department,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getSalaryDistribution",
                    "department", department,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> getSalaryCostAnalysis(String startDate, String endDate, String department) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getSalaryCostAnalysis",
                    "startDate", startDate,
                    "endDate", endDate,
                    "department", department
                )
            );
            
            // 生成薪资成本分析数据
            Map<String, Object> costAnalysisData = generateCostAnalysisData(startDate, endDate, department);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getSalaryCostAnalysis",
                    "startDate", startDate,
                    "endDate", endDate,
                    "department", department,
                    "status", "success"
                )
            );
            
            return Result.success(costAnalysisData);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getSalaryCostAnalysis",
                    "startDate", startDate,
                    "endDate", endDate,
                    "department", department,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getSalaryCostAnalysis",
                    "startDate", startDate,
                    "endDate", endDate,
                    "department", department,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public Result<?> getRaiseAnalysis(String department, String year, String quarter) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getRaiseAnalysis",
                    "department", department,
                    "year", year,
                    "quarter", quarter
                )
            );
            
            // 生成加薪分析数据
            Map<String, Object> raiseAnalysisData = generateRaiseAnalysisData(department, year, quarter);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getRaiseAnalysis",
                    "department", department,
                    "year", year,
                    "quarter", quarter,
                    "status", "success"
                )
            );
            
            return Result.success(raiseAnalysisData);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getRaiseAnalysis",
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
                    "getRaiseAnalysis",
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
    public Result<?> getCompensationSummary(String department, String year, String month) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getCompensationSummary",
                    "department", department,
                    "year", year,
                    "month", month
                )
            );
            
            // 生成薪酬总结数据
            Map<String, Object> compensationSummaryData = generateCompensationSummaryData(department, year, month);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getCompensationSummary",
                    "department", department,
                    "year", year,
                    "month", month,
                    "status", "success"
                )
            );
            
            return Result.success(compensationSummaryData);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getCompensationSummary",
                    "department", department,
                    "year", year,
                    "month", month,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getCompensationSummary",
                    "department", department,
                    "year", year,
                    "month", month,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 辅助方法：随机获取部门
    private String getRandomDepartment() {
        String[] departments = {"技术部", "销售部", "市场部", "财务部", "人力资源部"};
        return departments[new Random().nextInt(departments.length)];
    }
    
    // 辅助方法：生成薪资趋势数据
    private List<Map<String, Object>> generateSalaryTrends(String employeeId, String startDate, String endDate) {
        List<Map<String, Object>> salaryRecords = new ArrayList<>();
        
        int startYear = startDate != null ? Integer.parseInt(startDate.substring(0, 4)) : 2020;
        int startMonth = startDate != null ? Integer.parseInt(startDate.substring(5, 7)) : 6;
        int endYear = endDate != null ? Integer.parseInt(endDate.substring(0, 4)) : 2023;
        int endMonth = endDate != null ? Integer.parseInt(endDate.substring(5, 7)) : 12;
        
        BigDecimal baseSalary = new BigDecimal(8000 + new Random().nextInt(4000));
        int currentYear = startYear;
        int currentMonth = startMonth;
        
        Random random = new Random();
        
        while (currentYear < endYear || (currentYear == endYear && currentMonth <= endMonth)) {
            // 每年4月和10月可能有加薪
            if ((currentMonth == 4 || currentMonth == 10) && random.nextDouble() > 0.4) {
                baseSalary = baseSalary.multiply(
                        BigDecimal.ONE.add(new BigDecimal("0.05").add(new BigDecimal(random.nextDouble() * 0.1)))
                ).setScale(2, RoundingMode.HALF_UP);
            }
            
            BigDecimal performanceBonus = baseSalary.multiply(
                    new BigDecimal("0.1").add(new BigDecimal(random.nextDouble() * 0.2))
            ).setScale(2, RoundingMode.HALF_UP);
            
            BigDecimal totalSalary = baseSalary.add(performanceBonus).setScale(2, RoundingMode.HALF_UP);
            
            String changeReason = "";
            if ((currentMonth == 4 || currentMonth == 10) && random.nextDouble() > 0.4) {
                String[] reasons = {"绩效加薪", "职位晋升", "定期调整", "技能提升"};
                changeReason = reasons[random.nextInt(reasons.length)];
            }
            
            Map<String, Object> salaryRecord = new HashMap<>();
            salaryRecord.put("yearMonth", String.format("%d-%02d", currentYear, currentMonth));
            salaryRecord.put("baseSalary", baseSalary);
            salaryRecord.put("performanceBonus", performanceBonus);
            salaryRecord.put("overtimePay", new BigDecimal(random.nextDouble() * 1000).setScale(2, RoundingMode.HALF_UP));
            salaryRecord.put("totalSalary", totalSalary);
            salaryRecord.put("taxAmount", totalSalary.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP));
            salaryRecord.put("netSalary", totalSalary.multiply(new BigDecimal("0.92")).setScale(2, RoundingMode.HALF_UP));
            salaryRecord.put("changeReason", changeReason);
            
            salaryRecords.add(salaryRecord);
            
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
        }
        
        return salaryRecords;
    }
    
    // 辅助方法：计算统计数据
    private Map<String, Object> calculateStatistics(List<Map<String, Object>> salaryRecords) {
        Map<String, Object> statistics = new HashMap<>();
        
        if (salaryRecords.isEmpty()) {
            return statistics;
        }
        
        BigDecimal initialSalary = (BigDecimal) salaryRecords.get(0).get("baseSalary");
        BigDecimal currentSalary = (BigDecimal) salaryRecords.get(salaryRecords.size() - 1).get("baseSalary");
        BigDecimal growthAmount = currentSalary.subtract(initialSalary);
        BigDecimal growthRate = growthAmount.divide(initialSalary, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal avgAnnualIncrease = growthRate.divide(new BigDecimal(salaryRecords.size()).divide(new BigDecimal(12), 2, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP);
        
        long salaryChangeCount = salaryRecords.stream()
                .filter(record -> record.get("changeReason") != null && !((String) record.get("changeReason")).isEmpty())
                .count();
        
        statistics.put("initialSalary", initialSalary);
        statistics.put("currentSalary", currentSalary);
        statistics.put("growthRate", growthRate);
        statistics.put("growthAmount", growthAmount);
        statistics.put("avgAnnualIncrease", avgAnnualIncrease);
        statistics.put("salaryChangeCount", salaryChangeCount);
        
        return statistics;
    }
    
    // 辅助方法：生成部门薪资对比数据
    private List<Map<String, Object>> generateDepartmentComparisonData(String year, String quarter) {
        String[] departments = {"技术部", "销售部", "市场部", "财务部", "人力资源部"};
        int[] headcounts = {38, 31, 19, 15, 10};
        int[] averageSalaries = {24000, 22000, 20000, 21000, 19000};
        
        List<Map<String, Object>> departmentData = new ArrayList<>();
        
        for (int i = 0; i < departments.length; i++) {
            Map<String, Object> dept = new HashMap<>();
            dept.put("department", departments[i]);
            dept.put("headcount", headcounts[i]);
            
            int avgSalary = averageSalaries[i];
            int totalSalary = avgSalary * headcounts[i];
            
            dept.put("totalSalary", totalSalary);
            dept.put("averageSalary", avgSalary);
            dept.put("medianSalary", avgSalary - 1500);
            dept.put("minSalary", avgSalary / 2);
            dept.put("maxSalary", avgSalary * 2 - 3000);
            
            // 计算薪资占比
            double ratio = (double) totalSalary / Arrays.stream(headcounts).map(h -> h * averageSalaries[h == 38 ? 0 : h == 31 ? 1 : h == 19 ? 2 : h == 15 ? 3 : 4]).sum();
            dept.put("salaryRatio", new BigDecimal(ratio).setScale(2, RoundingMode.HALF_UP));
            
            // 季度数据
            Map<String, Object> quarterlyData = new HashMap<>();
            
            for (int q = 1; q <= 4; q++) {
                Map<String, Object> qData = new HashMap<>();
                qData.put("averageSalary", avgSalary - 500 + (q * 200));
                qData.put("headcount", headcounts[i] - (q == 1 ? 1 : 0));
                quarterlyData.put("Q" + q, qData);
            }
            
            dept.put("quarterlyData", quarterlyData);
            
            // 如果指定了季度，只返回该季度的数据
            if (quarter != null && !quarter.isEmpty()) {
                String quarterKey = "Q" + quarter;
                Map<String, Object> qData = (Map<String, Object>) quarterlyData.get(quarterKey);
                dept.put("averageSalary", qData.get("averageSalary"));
                dept.put("headcount", qData.get("headcount"));
                dept.remove("quarterlyData");
            }
            
            departmentData.add(dept);
        }
        
        return departmentData;
    }
    
    // 辅助方法：计算公司整体数据
    private Map<String, Object> calculateCompanyData(List<Map<String, Object>> departmentData) {
        Map<String, Object> companyData = new HashMap<>();
        
        int totalHeadcount = departmentData.stream().mapToInt(dept -> (int) dept.get("headcount")).sum();
        int totalSalary = departmentData.stream().mapToInt(dept -> (int) dept.get("totalSalary")).sum();
        
        int averageSalary = totalSalary / totalHeadcount;
        
        // 中位数薪资（简化计算）
        int medianSalary = departmentData.stream()
                .mapToInt(dept -> (int) dept.get("headcount") * (int) dept.get("medianSalary"))
                .sum() / totalHeadcount;
        
        int maxDeptSalary = departmentData.stream().mapToInt(dept -> (int) dept.get("averageSalary")).max().orElse(0);
        int minDeptSalary = departmentData.stream().mapToInt(dept -> (int) dept.get("averageSalary")).min().orElse(0);
        
        companyData.put("totalHeadcount", totalHeadcount);
        companyData.put("totalSalary", totalSalary);
        companyData.put("averageSalary", averageSalary);
        companyData.put("medianSalary", medianSalary);
        companyData.put("maxDepartmentSalary", maxDeptSalary);
        companyData.put("minDepartmentSalary", minDeptSalary);
        companyData.put("salaryGap", maxDeptSalary - minDeptSalary);
        
        return companyData;
    }
    
    // 辅助方法：生成薪资分布数据
    private Map<String, Object> generateSalaryDistributionData(String department, String year) {
        Map<String, Object> data = new HashMap<>();
        
        // 模拟薪资分布
        int[] rangeLabels = {8000, 10000, 15000, 20000, 25000, 30000, 35000, 40000, 45000, 50000};
        int[] counts = {15, 23, 35, 28, 17, 12, 8, 5, 3, 1};
        
        if (department != null && !department.equals("全部")) {
            // 按部门调整分布
            switch (department) {
                case "技术部":
                    counts = new int[]{8, 10, 18, 20, 15, 10, 7, 4, 3, 1};
                    break;
                case "销售部":
                    counts = new int[]{5, 8, 15, 9, 6, 5, 3, 2, 1, 0};
                    break;
                // 其他部门...
            }
        }
        
        // 生成分布数据
        List<Map<String, Object>> distributionData = new ArrayList<>();
        for (int i = 0; i < rangeLabels.length; i++) {
            Map<String, Object> range = new HashMap<>();
            String rangeLabel = i < rangeLabels.length - 1 
                    ? rangeLabels[i] + "-" + (rangeLabels[i+1] - 1) 
                    : rangeLabels[i] + "+";
            
            range.put("range", rangeLabel);
            range.put("count", counts[i]);
            range.put("percentage", new BigDecimal(counts[i] * 100.0 / Arrays.stream(counts).sum()).setScale(1, RoundingMode.HALF_UP));
            
            distributionData.add(range);
        }
        
        // 薪资统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("minSalary", 8000);
        statistics.put("maxSalary", 58000);
        statistics.put("avgSalary", 23500);
        statistics.put("medianSalary", 22000);
        statistics.put("most_common_range", "15000-19999");
        
        data.put("distributionData", distributionData);
        data.put("statistics", statistics);
        data.put("totalEmployees", Arrays.stream(counts).sum());
        
        return data;
    }
    
    // 辅助方法：生成薪资成本分析数据
    private Map<String, Object> generateCostAnalysisData(String startDate, String endDate, String department) {
        Map<String, Object> data = new HashMap<>();
        
        // 默认日期范围
        LocalDate start = startDate != null 
                ? LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE)
                : LocalDate.now().withDayOfMonth(1).minusMonths(11);
                
        LocalDate end = endDate != null 
                ? LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE)
                : LocalDate.now();
        
        // 生成月度成本数据
        List<Map<String, Object>> monthlyCosts = new ArrayList<>();
        LocalDate current = start;
        
        Random random = new Random();
        double baseCost = department != null && !department.equals("全部") ? 2000000 : 8000000;
        double previousCost = baseCost;
        
        while (!current.isAfter(end)) {
            Map<String, Object> monthData = new HashMap<>();
            String monthLabel = current.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            // 模拟一些波动
            double variation = random.nextDouble() * 0.1 - 0.03; // -3% to +7%
            double currentCost = previousCost * (1 + variation);
            previousCost = currentCost;
            
            monthData.put("month", monthLabel);
            monthData.put("totalCost", new BigDecimal(currentCost).setScale(2, RoundingMode.HALF_UP));
            
            // 细分成本
            monthData.put("basicSalary", new BigDecimal(currentCost * 0.65).setScale(2, RoundingMode.HALF_UP));
            monthData.put("bonus", new BigDecimal(currentCost * 0.15).setScale(2, RoundingMode.HALF_UP));
            monthData.put("benefits", new BigDecimal(currentCost * 0.12).setScale(2, RoundingMode.HALF_UP));
            monthData.put("overtime", new BigDecimal(currentCost * 0.08).setScale(2, RoundingMode.HALF_UP));
            
            monthlyCosts.add(monthData);
            current = current.plusMonths(1);
        }
        
        // 计算整体统计
        BigDecimal totalCost = monthlyCosts.stream()
                .map(m -> (BigDecimal) m.get("totalCost"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        BigDecimal avgMonthlyCost = totalCost.divide(new BigDecimal(monthlyCosts.size()), 2, RoundingMode.HALF_UP);
        
        // 计算同比增长
        BigDecimal firstMonthCost = (BigDecimal) monthlyCosts.get(0).get("totalCost");
        BigDecimal lastMonthCost = (BigDecimal) monthlyCosts.get(monthlyCosts.size() - 1).get("totalCost");
        BigDecimal growthRate = lastMonthCost.subtract(firstMonthCost)
                .divide(firstMonthCost, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCost", totalCost);
        statistics.put("avgMonthlyCost", avgMonthlyCost);
        statistics.put("growthRate", growthRate);
        statistics.put("costPerEmployee", new BigDecimal(18500));
        
        data.put("monthlyCosts", monthlyCosts);
        data.put("statistics", statistics);
        
        return data;
    }

    private Map<String, Object> generateRaiseAnalysisData(String department, String year, String quarter) {
        // 默认年份为当前年份
        String reportYear = year != null ? year : String.valueOf(LocalDate.now().getYear());
        // 默认季度为当前季度
        String reportQuarter = quarter != null ? quarter : String.valueOf((LocalDate.now().getMonthValue() - 1) / 3 + 1);
        
        // 部门列表
        List<String> departments = Arrays.asList("技术部", "市场部", "销售部", "财务部", "人力资源部", "运营部");
        List<String> filteredDepartments = department != null ? Collections.singletonList(department) : departments;
        
        Random random = new Random();
        
        // 生成部门加薪数据
        List<Map<String, Object>> departmentRaiseData = new ArrayList<>();
        for (String dept : filteredDepartments) {
            Map<String, Object> deptData = new HashMap<>();
            deptData.put("department", dept);
            
            // 部门人数
            int employeeCount = 30 + random.nextInt(50);
            deptData.put("employeeCount", employeeCount);
            
            // 加薪人数
            int raiseCount = 10 + random.nextInt(20);
            deptData.put("raiseCount", raiseCount);
            
            // 加薪比例
            double raiseRatio = roundToTwoDecimals((double) raiseCount / employeeCount * 100);
            deptData.put("raiseRatio", raiseRatio + "%");
            
            // 平均加薪幅度
            double averageRaisePercentage = roundToTwoDecimals(5 + random.nextDouble() * 10);
            deptData.put("averageRaisePercentage", averageRaisePercentage + "%");
            
            // 加薪总金额
            double totalRaiseAmount = roundToTwoDecimals(raiseCount * (8000 + random.nextDouble() * 4000) * averageRaisePercentage / 100);
            deptData.put("totalRaiseAmount", totalRaiseAmount);
            
            // 加薪前部门平均薪资
            double beforeAverageSalary = roundToTwoDecimals(10000 + random.nextDouble() * 5000);
            deptData.put("beforeAverageSalary", beforeAverageSalary);
            
            // 加薪后部门平均薪资
            double afterAverageSalary = roundToTwoDecimals(beforeAverageSalary * (1 + (raiseRatio / 100) * (averageRaisePercentage / 100)));
            deptData.put("afterAverageSalary", afterAverageSalary);
            
            // 加薪原因分布
            Map<String, Object> raiseReasons = new HashMap<>();
            raiseReasons.put("绩效优秀", roundToTwoDecimals(40 + random.nextDouble() * 20) + "%");
            raiseReasons.put("晋升", roundToTwoDecimals(20 + random.nextDouble() * 15) + "%");
            raiseReasons.put("调整", roundToTwoDecimals(10 + random.nextDouble() * 15) + "%");
            raiseReasons.put("留任", roundToTwoDecimals(5 + random.nextDouble() * 10) + "%");
            raiseReasons.put("其他", roundToTwoDecimals(5 + random.nextDouble() * 10) + "%");
            deptData.put("raiseReasons", raiseReasons);
            
            departmentRaiseData.add(deptData);
        }
        
        // 生成加薪水平分布
        Map<String, Object> raiseDistribution = new HashMap<>();
        raiseDistribution.put("小于5%", roundToTwoDecimals(20 + random.nextDouble() * 15) + "%");
        raiseDistribution.put("5%-10%", roundToTwoDecimals(30 + random.nextDouble() * 20) + "%");
        raiseDistribution.put("10%-15%", roundToTwoDecimals(25 + random.nextDouble() * 15) + "%");
        raiseDistribution.put("15%-20%", roundToTwoDecimals(15 + random.nextDouble() * 10) + "%");
        raiseDistribution.put("大于20%", roundToTwoDecimals(5 + random.nextDouble() * 10) + "%");
        
        // 按职级分布的加薪情况
        List<Map<String, Object>> levelRaiseData = new ArrayList<>();
        String[] levels = {"初级", "中级", "高级", "专家", "管理"};
        
        for (String level : levels) {
            Map<String, Object> levelData = new HashMap<>();
            levelData.put("level", level);
            levelData.put("employeeCount", 10 + random.nextInt(30));
            levelData.put("raiseCount", 5 + random.nextInt(15));
            levelData.put("averageRaisePercentage", roundToTwoDecimals(5 + random.nextDouble() * 15) + "%");
            levelData.put("minRaisePercentage", roundToTwoDecimals(2 + random.nextDouble() * 5) + "%");
            levelData.put("maxRaisePercentage", roundToTwoDecimals(15 + random.nextDouble() * 10) + "%");
            
            levelRaiseData.add(levelData);
        }
        
        // 组装最终结果
        Map<String, Object> result = new HashMap<>();
        result.put("year", reportYear);
        result.put("quarter", reportQuarter);
        result.put("departmentRaiseData", departmentRaiseData);
        result.put("raiseDistribution", raiseDistribution);
        result.put("levelRaiseData", levelRaiseData);
        
        // 计算总体统计
        int totalEmployeeCount = departmentRaiseData.stream().mapToInt(d -> (int) d.get("employeeCount")).sum();
        int totalRaiseCount = departmentRaiseData.stream().mapToInt(d -> (int) d.get("raiseCount")).sum();
        double totalRaiseAmount = departmentRaiseData.stream().mapToDouble(d -> (double) d.get("totalRaiseAmount")).sum();
        double totalRaiseRatio = roundToTwoDecimals((double) totalRaiseCount / totalEmployeeCount * 100);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEmployeeCount", totalEmployeeCount);
        summary.put("totalRaiseCount", totalRaiseCount);
        summary.put("totalRaiseRatio", totalRaiseRatio + "%");
        summary.put("totalRaiseAmount", roundToTwoDecimals(totalRaiseAmount));
        summary.put("averageRaisePercentage", roundToTwoDecimals(departmentRaiseData.stream()
                .mapToDouble(d -> Double.parseDouble(((String) d.get("averageRaisePercentage")).replace("%", "")))
                .average()
                .orElse(0)) + "%");
        
        result.put("summary", summary);
        
        return result;
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private Map<String, Object> generateCompensationSummaryData(String department, String year, String month) {
        // 默认年份为当前年份
        String reportYear = year != null ? year : String.valueOf(LocalDate.now().getYear());
        // 默认月份为当前月份
        String reportMonth = month != null ? month : String.valueOf(LocalDate.now().getMonthValue());
        
        // 部门列表
        List<String> departments = Arrays.asList("技术部", "市场部", "销售部", "财务部", "人力资源部", "运营部");
        List<String> filteredDepartments = department != null ? Collections.singletonList(department) : departments;
        
        Random random = new Random();
        
        // 生成部门薪酬数据
        List<Map<String, Object>> departmentCompensationData = new ArrayList<>();
        for (String dept : filteredDepartments) {
            Map<String, Object> deptData = new HashMap<>();
            deptData.put("department", dept);
            
            // 部门人数
            int employeeCount = 30 + random.nextInt(50);
            deptData.put("employeeCount", employeeCount);
            
            // 基本工资总额
            double baseSalaryTotal = roundToTwoDecimals(employeeCount * (8000 + random.nextDouble() * 4000));
            deptData.put("baseSalaryTotal", baseSalaryTotal);
            
            // 绩效奖金总额
            double performanceBonusTotal = roundToTwoDecimals(baseSalaryTotal * (0.1 + random.nextDouble() * 0.2));
            deptData.put("performanceBonusTotal", performanceBonusTotal);
            
            // 津贴总额
            double allowancesTotal = roundToTwoDecimals(baseSalaryTotal * (0.05 + random.nextDouble() * 0.1));
            deptData.put("allowancesTotal", allowancesTotal);
            
            // 加班费总额
            double overtimePayTotal = roundToTwoDecimals(baseSalaryTotal * (0.03 + random.nextDouble() * 0.08));
            deptData.put("overtimePayTotal", overtimePayTotal);
            
            // 社保和公积金总额
            double benefitsTotal = roundToTwoDecimals(baseSalaryTotal * (0.15 + random.nextDouble() * 0.05));
            deptData.put("benefitsTotal", benefitsTotal);
            
            // 税前薪酬总额
            double preTaxTotal = roundToTwoDecimals(baseSalaryTotal + performanceBonusTotal + allowancesTotal + overtimePayTotal);
            deptData.put("preTaxTotal", preTaxTotal);
            
            // 个税总额
            double taxTotal = roundToTwoDecimals(preTaxTotal * (0.05 + random.nextDouble() * 0.05));
            deptData.put("taxTotal", taxTotal);
            
            // 税后薪酬总额
            double afterTaxTotal = roundToTwoDecimals(preTaxTotal - taxTotal);
            deptData.put("afterTaxTotal", afterTaxTotal);
            
            // 人均薪酬
            double averageSalary = roundToTwoDecimals(preTaxTotal / employeeCount);
            deptData.put("averageSalary", averageSalary);
            
            // 薪酬中位数（模拟计算）
            double medianSalary = roundToTwoDecimals(averageSalary * (0.9 + random.nextDouble() * 0.2));
            deptData.put("medianSalary", medianSalary);
            
            // 薪酬范围
            double minSalary = roundToTwoDecimals(averageSalary * 0.6);
            double maxSalary = roundToTwoDecimals(averageSalary * 1.8);
            Map<String, Object> salaryRange = new HashMap<>();
            salaryRange.put("min", minSalary);
            salaryRange.put("max", maxSalary);
            deptData.put("salaryRange", salaryRange);
            
            departmentCompensationData.add(deptData);
        }
        
        // 生成薪酬组成分析
        Map<String, Object> compensationComponents = new HashMap<>();
        double totalCompensation = departmentCompensationData.stream().mapToDouble(d -> (double) d.get("preTaxTotal")).sum();
        
        double baseSalaryRatio = roundToTwoDecimals(departmentCompensationData.stream().mapToDouble(d -> (double) d.get("baseSalaryTotal")).sum() / totalCompensation * 100);
        double performanceBonusRatio = roundToTwoDecimals(departmentCompensationData.stream().mapToDouble(d -> (double) d.get("performanceBonusTotal")).sum() / totalCompensation * 100);
        double allowancesRatio = roundToTwoDecimals(departmentCompensationData.stream().mapToDouble(d -> (double) d.get("allowancesTotal")).sum() / totalCompensation * 100);
        double overtimePayRatio = roundToTwoDecimals(departmentCompensationData.stream().mapToDouble(d -> (double) d.get("overtimePayTotal")).sum() / totalCompensation * 100);
        
        compensationComponents.put("baseSalary", baseSalaryRatio + "%");
        compensationComponents.put("performanceBonus", performanceBonusRatio + "%");
        compensationComponents.put("allowances", allowancesRatio + "%");
        compensationComponents.put("overtimePay", overtimePayRatio + "%");
        
        // 薪酬水平分布
        Map<String, Object> salaryLevelDistribution = new HashMap<>();
        salaryLevelDistribution.put("低于8000", roundToTwoDecimals(10 + random.nextDouble() * 10) + "%");
        salaryLevelDistribution.put("8000-12000", roundToTwoDecimals(20 + random.nextDouble() * 15) + "%");
        salaryLevelDistribution.put("12000-18000", roundToTwoDecimals(25 + random.nextDouble() * 15) + "%");
        salaryLevelDistribution.put("18000-25000", roundToTwoDecimals(20 + random.nextDouble() * 10) + "%");
        salaryLevelDistribution.put("25000-35000", roundToTwoDecimals(10 + random.nextDouble() * 10) + "%");
        salaryLevelDistribution.put("35000以上", roundToTwoDecimals(5 + random.nextDouble() * 5) + "%");
        
        // 计算总体统计
        int totalEmployeeCount = departmentCompensationData.stream().mapToInt(d -> (int) d.get("employeeCount")).sum();
        double totalPreTaxSalary = departmentCompensationData.stream().mapToDouble(d -> (double) d.get("preTaxTotal")).sum();
        double totalAfterTaxSalary = departmentCompensationData.stream().mapToDouble(d -> (double) d.get("afterTaxTotal")).sum();
        double totalTax = departmentCompensationData.stream().mapToDouble(d -> (double) d.get("taxTotal")).sum();
        double companyAverageSalary = roundToTwoDecimals(totalPreTaxSalary / totalEmployeeCount);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEmployeeCount", totalEmployeeCount);
        summary.put("totalPreTaxSalary", roundToTwoDecimals(totalPreTaxSalary));
        summary.put("totalAfterTaxSalary", roundToTwoDecimals(totalAfterTaxSalary));
        summary.put("totalTax", roundToTwoDecimals(totalTax));
        summary.put("companyAverageSalary", companyAverageSalary);
        summary.put("taxRate", roundToTwoDecimals(totalTax / totalPreTaxSalary * 100) + "%");
        
        // 与市场对比数据（模拟）
        Map<String, Object> marketComparison = new HashMap<>();
        marketComparison.put("industryAverageSalary", roundToTwoDecimals(companyAverageSalary * (0.9 + random.nextDouble() * 0.2)));
        marketComparison.put("companyPercentile", roundToTwoDecimals(60 + random.nextDouble() * 30) + "%");
        
        // 组装最终结果
        Map<String, Object> result = new HashMap<>();
        result.put("year", reportYear);
        result.put("month", reportMonth);
        result.put("departmentData", departmentCompensationData);
        result.put("compensationComponents", compensationComponents);
        result.put("salaryLevelDistribution", salaryLevelDistribution);
        result.put("summary", summary);
        result.put("marketComparison", marketComparison);
        
        return result;
    }
} 