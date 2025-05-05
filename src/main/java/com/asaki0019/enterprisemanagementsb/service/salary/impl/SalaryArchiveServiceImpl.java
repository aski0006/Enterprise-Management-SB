package com.asaki0019.enterprisemanagementsb.service.salary.impl;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.exception.BusinessException;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.asaki0019.enterprisemanagementsb.service.salary.SalaryArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 薪资档案服务实现类
 */
@Service
@Transactional
public class SalaryArchiveServiceImpl implements SalaryArchiveService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final Random random = new Random();
    private final SysLogger sysLogger;

    @Autowired
    public SalaryArchiveServiceImpl(SysLogger sysLogger) {
        this.sysLogger = sysLogger;
    }
    
    @Override
    public Result<?> getEmployeeSalaries(String employeeId, String employeeName, String department, int page, int pageSize) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaries",
                    "employeeId", employeeId,
                    "employeeName", employeeName,
                    "department", department,
                    "page", String.valueOf(page),
                    "pageSize", String.valueOf(pageSize)
                )
            );
            
            // 生成模拟员工薪资档案数据
            List<Map<String, Object>> employees = generateEmployeeRecords();
            
            // 应用过滤条件
            List<Map<String, Object>> filteredEmployees = employees;
            
            if (employeeId != null && !employeeId.isEmpty()) {
                filteredEmployees = filteredEmployees.stream()
                        .filter(emp -> employeeId.equals(emp.get("employeeId")))
                        .collect(Collectors.toList());
            }
            
            if (employeeName != null && !employeeName.isEmpty()) {
                filteredEmployees = filteredEmployees.stream()
                        .filter(emp -> ((String) emp.get("name")).contains(employeeName))
                        .collect(Collectors.toList());
            }
            
            if (department != null && !department.isEmpty()) {
                filteredEmployees = filteredEmployees.stream()
                        .filter(emp -> department.equals(emp.get("department")))
                        .collect(Collectors.toList());
            }
            
            // 分页
            int total = filteredEmployees.size();
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);
            
            List<Map<String, Object>> paginatedEmployees = filteredEmployees.subList(startIndex, endIndex);
            
            Map<String, Object> data = new HashMap<>();
            data.put("employees", paginatedEmployees);
            data.put("total", total);
            data.put("departments", Arrays.asList("技术部", "销售部", "市场部", "财务部", "人力资源部"));
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaries",
                    "recordsFound", String.valueOf(total),
                    "status", "success"
                )
            );

            return Result.success(data);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaries",
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaries",
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> getEmployeeSalaryDetail(String employeeId) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryDetail",
                    "employeeId", employeeId
                )
            );
            
            // 检查是否是有效的员工ID
            if (employeeId == null || !employeeId.matches("E\\d+")) {
                sysLogger.warn(
                    MessageConstructor.constructPlainMessage(
                        "getEmployeeSalaryDetail",
                        "employeeId", employeeId,
                        "warning", "员工ID不能为空或格式不正确"
                    )
                );
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "员工ID不能为空或格式不正确");
            }
            
            Map<String, Object> detail = generateEmployeeDetail(employeeId);

            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryDetail",
                    "employeeId", employeeId,
                    "status", "success"
                )
            );
            
            return Result.success(detail);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryDetail",
                    "employeeId", employeeId,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryDetail",
                    "employeeId", employeeId,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> updateEmployeeSalary(Map<String, Object> request) {
        try {
            String employeeId = request.get("employeeId") != null ? request.get("employeeId").toString() : null;
            
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "updateEmployeeSalary",
                    "employeeId", employeeId
                )
            );
            
            // 简单的校验
            if (employeeId == null) {
                sysLogger.warn(
                    MessageConstructor.constructPlainMessage(
                        "updateEmployeeSalary",
                        "warning", "员工ID不能为空"
                    )
                );
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "员工ID不能为空");
            }
            
            // 模拟更新成功
            Map<String, Object> updatedEmployee = new HashMap<>(request);
            updatedEmployee.put("lastUpdated", new Date());
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "updateEmployeeSalary",
                    "employeeId", employeeId,
                    "status", "success"
                )
            );
            
            return Result.success(updatedEmployee);
        } catch (BusinessException e) {
            String employeeId = request.get("employeeId") != null ? request.get("employeeId").toString() : "unknown";
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "updateEmployeeSalary",
                    "employeeId", employeeId,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            String employeeId = request.get("employeeId") != null ? request.get("employeeId").toString() : "unknown";
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "updateEmployeeSalary",
                    "employeeId", employeeId,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> getEmployeeSalaryHistory(String employeeId) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryHistory",
                    "employeeId", employeeId
                )
            );
            
            if (employeeId == null || employeeId.isEmpty()) {
                sysLogger.warn(
                    MessageConstructor.constructPlainMessage(
                        "getEmployeeSalaryHistory",
                        "warning", "员工ID不能为空"
                    )
                );
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "员工ID不能为空");
            }
            
            // 生成员工薪资变更历史
            List<Map<String, Object>> salaryHistory = generateSalaryHistory(employeeId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("employeeId", employeeId);
            data.put("name", "李" + (Integer.parseInt(employeeId.substring(1)) % 100));
            data.put("department", getRandomDepartment());
            data.put("position", getRandomPosition());
            data.put("history", salaryHistory);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryHistory",
                    "employeeId", employeeId,
                    "recordCount", String.valueOf(salaryHistory.size()),
                    "status", "success"
                )
            );
            
            return Result.success(data);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryHistory",
                    "employeeId", employeeId,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getEmployeeSalaryHistory",
                    "employeeId", employeeId,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> importSalaryArchives(String fileType, Map<String, Object> request) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "importSalaryArchives",
                    "fileType", fileType
                )
            );
            
            if (fileType == null || fileType.isEmpty()) {
                sysLogger.warn(
                    MessageConstructor.constructPlainMessage(
                        "importSalaryArchives",
                        "warning", "文件类型不能为空"
                    )
                );
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "文件类型不能为空");
            }
            
            // 模拟导入处理
            int totalRecords = random.nextInt(50) + 50;
            int successCount = totalRecords - random.nextInt(5);
            int failCount = totalRecords - successCount;
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalRecords", totalRecords);
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            
            if (failCount > 0) {
                List<Map<String, Object>> failRecords = new ArrayList<>();
                for (int i = 0; i < failCount; i++) {
                    Map<String, Object> failRecord = new HashMap<>();
                    failRecord.put("row", random.nextInt(totalRecords) + 1);
                    failRecord.put("employeeId", "E" + (1000 + random.nextInt(900)));
                    failRecord.put("reason", "数据格式不正确");
                    failRecords.add(failRecord);
                }
                result.put("failRecords", failRecords);
            }
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "importSalaryArchives",
                    "fileType", fileType,
                    "totalRecords", String.valueOf(totalRecords),
                    "successCount", String.valueOf(successCount),
                    "failCount", String.valueOf(failCount),
                    "status", "success"
                )
            );
            
            return Result.success(result);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "importSalaryArchives",
                    "fileType", fileType,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "importSalaryArchives",
                    "fileType", fileType,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> exportSalaryArchives(String department, String year) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "exportSalaryArchives",
                    "department", department,
                    "year", year
                )
            );
            
            // 在实际项目中，这里应该生成一个Excel或CSV文件供下载
            // 这里只是模拟导出功能
            
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", "员工薪资档案_" + (department != null ? department + "_" : "") + 
                    (year != null ? year : "全部") + ".xlsx");
            result.put("fileUrl", "/temp/exports/salary_archive_" + System.currentTimeMillis() + ".xlsx");
            result.put("fileSize", "256KB");
            result.put("recordCount", 50);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "exportSalaryArchives",
                    "department", department,
                    "year", year,
                    "fileName", result.get("fileName").toString(),
                    "status", "success"
                )
            );
            
            return Result.success(result);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "exportSalaryArchives",
                    "department", department,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "exportSalaryArchives",
                    "department", department,
                    "year", year,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<?> getSalaryBenchmark(String department, String position) {
        try {
            // 记录请求信息
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getSalaryBenchmark",
                    "department", department,
                    "position", position
                )
            );
            
            // 生成薪资基准数据
            Map<String, Object> benchmarkData = generateBenchmarkData(department, position);
            
            sysLogger.info(
                MessageConstructor.constructPlainMessage(
                    "getSalaryBenchmark",
                    "department", department,
                    "position", position,
                    "status", "success"
                )
            );
            
            return Result.success(benchmarkData);
        } catch (BusinessException e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getSalaryBenchmark",
                    "department", department,
                    "position", position,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        } catch (Exception e) {
            sysLogger.error(
                MessageConstructor.constructPlainMessage(
                    "getSalaryBenchmark",
                    "department", department,
                    "position", position,
                    "error", e.getMessage()
                ), e
            );
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 辅助方法：生成员工薪资记录
    private List<Map<String, Object>> generateEmployeeRecords() {
        List<Map<String, Object>> employees = new ArrayList<>();
        
        String[] departments = {"技术部", "销售部", "市场部", "财务部", "人力资源部"};
        String[] positions = {"初级工程师", "中级工程师", "高级工程师", "技术经理", "销售经理", "市场专员", "财务专员", "HR专员"};
        
        for (int i = 1; i <= 50; i++) {
            Map<String, Object> employee = new HashMap<>();
            
            String employeeId = "E" + (1000 + i);
            employee.put("employeeId", employeeId);
            employee.put("name", "李" + i);
            
            String department = departments[random.nextInt(departments.length)];
            employee.put("department", department);
            
            String position = positions[random.nextInt(positions.length)];
            employee.put("position", position);
            
            // 入职日期：2018-01-01至2023-12-31之间的随机日期
            Calendar hireDate = Calendar.getInstance();
            hireDate.set(2018 + random.nextInt(6), random.nextInt(12), 1 + random.nextInt(28));
            employee.put("hireDate", DATE_FORMAT.format(hireDate.getTime()));
            
            // 基本工资：8000-30000之间
            double baseSalary = 8000 + random.nextInt(22000);
            baseSalary = Math.round(baseSalary / 100.0) * 100.0;  // 取整到百位
            employee.put("baseSalary", baseSalary);
            
            // 绩效奖金：基本工资的0-30%
            double performanceBonus = baseSalary * (random.nextInt(30) / 100.0);
            performanceBonus = Math.round(performanceBonus / 100.0) * 100.0;  // 取整到百位
            employee.put("performanceBonus", performanceBonus);
            
            // 其他补贴：0-5000之间
            double allowance = random.nextInt(50) * 100.0;
            employee.put("allowance", allowance);
            
            // 总薪资
            double totalSalary = baseSalary + performanceBonus + allowance;
            employee.put("totalSalary", totalSalary);
            
            // 最后更新时间
            Calendar lastUpdated = Calendar.getInstance();
            lastUpdated.add(Calendar.DAY_OF_MONTH, -random.nextInt(60));
            employee.put("lastUpdated", DATE_FORMAT.format(lastUpdated.getTime()));
            
            employees.add(employee);
        }
        
        return employees;
    }
    
    // 辅助方法：生成员工详细信息
    private Map<String, Object> generateEmployeeDetail(String employeeId) {
        Map<String, Object> detail = new HashMap<>();
        detail.put("employeeId", employeeId);
        
        try {
            int id = Integer.parseInt(employeeId.substring(1));
            detail.put("name", "李" + (id % 100));
        } catch (NumberFormatException e) {
            detail.put("name", "未知员工");
        }
        
        String department = getRandomDepartment();
        detail.put("department", department);
        
        String position = getRandomPosition();
        detail.put("position", position);
        
        // 入职日期
        Calendar hireDate = Calendar.getInstance();
        hireDate.add(Calendar.YEAR, -random.nextInt(5) - 1);
        hireDate.add(Calendar.MONTH, -random.nextInt(12));
        hireDate.add(Calendar.DAY_OF_MONTH, -random.nextInt(30));
        detail.put("hireDate", DATE_FORMAT.format(hireDate.getTime()));
        
        // 薪资信息
        double baseSalary = 8000 + random.nextInt(22000);
        baseSalary = Math.round(baseSalary / 100.0) * 100.0;
        detail.put("baseSalary", baseSalary);
        
        double performanceBonus = baseSalary * (random.nextInt(30) / 100.0);
        performanceBonus = Math.round(performanceBonus / 100.0) * 100.0;
        detail.put("performanceBonus", performanceBonus);
        
        double allowance = random.nextInt(50) * 100.0;
        detail.put("allowance", allowance);
        
        double insuranceAndFund = (baseSalary * 0.08 + allowance * 0.08);
        insuranceAndFund = Math.round(insuranceAndFund * 100.0) / 100.0;
        detail.put("insuranceAndFund", insuranceAndFund);
        
        double individualIncomeTax = (baseSalary + performanceBonus + allowance) * 0.1 - 1000;
        individualIncomeTax = Math.max(0, individualIncomeTax);
        individualIncomeTax = Math.round(individualIncomeTax * 100.0) / 100.0;
        detail.put("individualIncomeTax", individualIncomeTax);
        
        double netSalary = baseSalary + performanceBonus + allowance - insuranceAndFund - individualIncomeTax;
        netSalary = Math.round(netSalary * 100.0) / 100.0;
        detail.put("netSalary", netSalary);
        
        // 银行账户信息
        detail.put("bankAccount", "6222 **** **** " + (1000 + random.nextInt(9000)));
        detail.put("bankName", "中国工商银行");
        
        // 薪资变更历史
        detail.put("salaryHistory", generateSalaryHistory(employeeId));
        
        // 人事信息
        detail.put("idNumber", "3301 **** **** " + (1000 + random.nextInt(9000)));
        detail.put("education", getRandomEducation());
        detail.put("address", "浙江省杭州市西湖区文三路XX号");
        detail.put("contactPhone", "138 **** " + (1000 + random.nextInt(9000)));
        
        return detail;
    }
    
    // 辅助方法：生成薪资变更历史
    private List<Map<String, Object>> generateSalaryHistory(String employeeId) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // 生成3-6条历史记录
        int recordCount = 3 + random.nextInt(4);
        
        Calendar date = Calendar.getInstance();
        date.add(Calendar.YEAR, -recordCount);
        
        double lastBaseSalary = 6000 + random.nextInt(4000);
        
        for (int i = 0; i < recordCount; i++) {
            Map<String, Object> record = new HashMap<>();
            
            date.add(Calendar.MONTH, 6 + random.nextInt(6));
            record.put("date", DATE_FORMAT.format(date.getTime()));
            
            record.put("beforeSalary", lastBaseSalary);
            
            // 加薪幅度：5%-20%
            double increaseRate = 0.05 + random.nextDouble() * 0.15;
            double newBaseSalary = lastBaseSalary * (1 + increaseRate);
            newBaseSalary = Math.round(newBaseSalary / 100.0) * 100.0;
            
            record.put("afterSalary", newBaseSalary);
            record.put("changeAmount", newBaseSalary - lastBaseSalary);
            record.put("changeRate", Math.round(increaseRate * 100.0 * 10.0) / 10.0);
            
            String[] reasons = {"定期调薪", "绩效晋升", "职位晋升", "试用期转正", "年度调薪"};
            record.put("reason", reasons[random.nextInt(reasons.length)]);
            record.put("approver", "王经理");
            
            history.add(record);
            lastBaseSalary = newBaseSalary;
        }
        
        // 按日期倒序排序
        history.sort((a, b) -> ((String)b.get("date")).compareTo((String)a.get("date")));
        
        return history;
    }
    
    // 辅助方法：生成薪资基准数据
    private Map<String, Object> generateBenchmarkData(String departmentFilter, String positionFilter) {
        Map<String, Object> data = new HashMap<>();
        
        // 准备部门和职位数据
        String[] departments = {"技术部", "销售部", "市场部", "财务部", "人力资源部"};
        String[] positions = {"初级工程师", "中级工程师", "高级工程师", "技术经理", "销售经理", "市场专员", "财务专员", "HR专员"};
        
        // 部门数据
        List<Map<String, Object>> departmentData = new ArrayList<>();
        for (String department : departments) {
            if (departmentFilter != null && !departmentFilter.isEmpty() && !department.equals(departmentFilter)) {
                continue;
            }
            
            Map<String, Object> dept = new HashMap<>();
            dept.put("department", department);
            
            double minSalary = 7000 + random.nextInt(3000);
            double maxSalary = 18000 + random.nextInt(12000);
            double avgSalary = (minSalary + maxSalary) / 2 + random.nextInt(2000);
            
            minSalary = Math.round(minSalary / 100.0) * 100.0;
            maxSalary = Math.round(maxSalary / 100.0) * 100.0;
            avgSalary = Math.round(avgSalary / 100.0) * 100.0;
            
            dept.put("minSalary", minSalary);
            dept.put("maxSalary", maxSalary);
            dept.put("avgSalary", avgSalary);
            dept.put("headcount", 20 + random.nextInt(30));
            
            departmentData.add(dept);
        }
        data.put("departmentData", departmentData);
        
        // 职位数据
        List<Map<String, Object>> positionData = new ArrayList<>();
        for (String position : positions) {
            if (positionFilter != null && !positionFilter.isEmpty() && !position.equals(positionFilter)) {
                continue;
            }
            
            Map<String, Object> pos = new HashMap<>();
            pos.put("position", position);
            
            double minSalary = 7000 + random.nextInt(3000);
            double maxSalary = 18000 + random.nextInt(12000);
            double avgSalary = (minSalary + maxSalary) / 2 + random.nextInt(2000);
            double marketMedian = avgSalary * (0.9 + random.nextDouble() * 0.2);
            
            minSalary = Math.round(minSalary / 100.0) * 100.0;
            maxSalary = Math.round(maxSalary / 100.0) * 100.0;
            avgSalary = Math.round(avgSalary / 100.0) * 100.0;
            marketMedian = Math.round(marketMedian / 100.0) * 100.0;
            
            pos.put("minSalary", minSalary);
            pos.put("maxSalary", maxSalary);
            pos.put("avgSalary", avgSalary);
            pos.put("marketMedian", marketMedian);
            pos.put("headcount", 5 + random.nextInt(15));
            
            positionData.add(pos);
        }
        data.put("positionData", positionData);
        
        // 市场薪资水平与公司比较
        Map<String, Object> marketComparison = new HashMap<>();
        marketComparison.put("companyAvg", 15000 + random.nextInt(3000));
        marketComparison.put("marketAvg", 14000 + random.nextInt(5000));
        marketComparison.put("difference", ((double)marketComparison.get("companyAvg") - (double)marketComparison.get("marketAvg")) / (double)marketComparison.get("marketAvg") * 100);
        data.put("marketComparison", marketComparison);
        
        return data;
    }
    
    // 辅助方法：随机获取部门
    private String getRandomDepartment() {
        String[] departments = {"技术部", "销售部", "市场部", "财务部", "人力资源部"};
        return departments[random.nextInt(departments.length)];
    }
    
    // 辅助方法：随机获取职位
    private String getRandomPosition() {
        String[] positions = {"初级工程师", "中级工程师", "高级工程师", "技术经理", "销售经理", "市场专员", "财务专员", "HR专员"};
        return positions[random.nextInt(positions.length)];
    }
    
    // 辅助方法：随机获取学历
    private String getRandomEducation() {
        String[] educations = {"大专", "本科", "硕士", "博士"};
        return educations[random.nextInt(educations.length)];
    }
} 