package com.asaki0019.enterprisemanagementsb.service.salary;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.exception.BusinessException;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryCalculation;
import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryPayslip;
import com.asaki0019.enterprisemanagementsb.mapper.salary.SalaryCalculationRepository;
import com.asaki0019.enterprisemanagementsb.mapper.salary.SalaryPayslipRepository;
import com.asaki0019.enterprisemanagementsb.request.salary.PayslipRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.SalaryCalculationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SalaryPayrollService {

    private final SalaryCalculationRepository salaryCalculationRepository;
    private final SalaryPayslipRepository salaryPayslipRepository;
    private final SysLogger sysLogger;

    @Autowired
    public SalaryPayrollService(
            SalaryCalculationRepository salaryCalculationRepository,
            SalaryPayslipRepository salaryPayslipRepository,
            SysLogger sysLogger) {
        this.salaryCalculationRepository = salaryCalculationRepository;
        this.salaryPayslipRepository = salaryPayslipRepository;
        this.sysLogger = sysLogger;
    }
    
    // 获取薪资核算数据
    public Result<?> getSalaryCalculation(String period, String department) {
        try {
            Page<SalaryCalculation> calculationsPage;
            PageRequest pageRequest = PageRequest.of(0, 1000); // 获取所有数据
            
            if (department != null && !department.isEmpty()) {
                calculationsPage = salaryCalculationRepository.findByPeriodAndDepartment(period, department, pageRequest);
            } else {
                calculationsPage = salaryCalculationRepository.findByPeriod(period, pageRequest);
            }
            
            List<SalaryCalculation> employees = calculationsPage.getContent();
            
            // 计算汇总数据
            int totalEmployees = employees.size();
            BigDecimal totalSalary = BigDecimal.ZERO;
            BigDecimal totalInsurance = BigDecimal.ZERO;
            BigDecimal totalTax = BigDecimal.ZERO;
            
            for (SalaryCalculation emp : employees) {
                totalSalary = totalSalary.add(emp.getTotalSalary());
                totalInsurance = totalInsurance.add(emp.getInsurance());
                totalTax = totalTax.add(emp.getTax());
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalEmployees", totalEmployees);
            data.put("totalSalary", totalSalary);
            data.put("totalInsurance", totalInsurance);
            data.put("totalTax", totalTax);
            data.put("employees", employees);
            
            return Result.success(data);
        } catch (Exception e) {
            sysLogger.error("获取薪资核算数据失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "获取薪资核算数据失败");
        }
    }
    
    // 确认薪资数据
    public Result<?> confirmSalaryCalculation(SalaryCalculationRequest request) {
        try {
            String period = request.getPeriod();
            String department = request.getDepartment();
            
            if (period == null || period.isEmpty()) {
                return Result.failure(ErrorCode.BAD_REQUEST, "缺少必要参数: period");
            }
            
            List<SalaryCalculation> calculations;
            if (department != null && !department.isEmpty()) {
                calculations = salaryCalculationRepository.findByPeriodAndDepartment(
                        period, department, PageRequest.of(0, 1000)).getContent();
            } else {
                calculations = salaryCalculationRepository.findByPeriod(
                        period, PageRequest.of(0, 1000)).getContent();
            }
            
            for (SalaryCalculation calculation : calculations) {
                calculation.setStatus("confirmed");
                calculation.setConfirmTime(LocalDateTime.now());
                calculation.setConfirmedBy("当前用户"); // 应该从当前登录用户中获取
                salaryCalculationRepository.save(calculation);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("period", period);
            data.put("department", department);
            data.put("confirmTime", LocalDateTime.now());
            data.put("status", "confirmed");
            
            return Result.success(data);
        } catch (Exception e) {
            sysLogger.error("确认薪资数据失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "确认薪资数据失败");
        }
    }
    
    // 调整薪资
    public Result<?> adjustSalary(SalaryCalculationRequest request) {
        try {
            String employeeId = request.getEmployeeId();
            String period = request.getPeriod();
            String item = request.getItem();
            String value = request.getValue();
            String reason = request.getReason();
            
            // 校验必填项
            if (employeeId == null || period == null || item == null || reason == null) {
                return Result.failure(ErrorCode.BAD_REQUEST, "缺少必要参数");
            }
            
            // 查找员工薪资记录
            List<SalaryCalculation> calculations = salaryCalculationRepository.findByEmployeeIdAndPeriod(employeeId, period);
            if (calculations.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "未找到员工薪资记录");
            }
            
            SalaryCalculation calculation = calculations.get(0);
            
            // 根据调整项更新数据
            BigDecimal newValue = new BigDecimal(value);
            switch (item) {
                case "baseSalary":
                    calculation.setBaseSalary(newValue);
                    break;
                case "performanceBonus":
                    calculation.setPerformanceBonus(newValue);
                    break;
                case "overtimePay":
                    calculation.setOvertimePay(newValue);
                    break;
                case "allowance":
                    calculation.setAllowance(newValue);
                    break;
                case "insurance":
                    calculation.setInsurance(newValue);
                    break;
                case "tax":
                    calculation.setTax(newValue);
                    break;
                case "deduction":
                    calculation.setDeduction(newValue);
                    break;
                default:
                    return Result.failure(ErrorCode.BAD_REQUEST, "无效的调整项");
            }
            
            // 重新计算总薪资和净薪资
            BigDecimal totalSalary = calculation.getBaseSalary()
                    .add(calculation.getPerformanceBonus())
                    .add(calculation.getOvertimePay())
                    .add(calculation.getAllowance());
            
            BigDecimal netSalary = totalSalary
                    .subtract(calculation.getInsurance())
                    .subtract(calculation.getTax())
                    .subtract(calculation.getDeduction());
            
            calculation.setTotalSalary(totalSalary);
            calculation.setNetSalary(netSalary);
            
            salaryCalculationRepository.save(calculation);
            
            Map<String, Object> data = new HashMap<>();
            data.put("employeeId", employeeId);
            data.put("item", item);
            data.put("value", newValue);
            data.put("adjustTime", LocalDateTime.now());
            data.put("operator", "当前用户"); // 应该从当前登录用户中获取
            
            return Result.success(data);
        } catch (Exception e) {
            sysLogger.error("调整薪资失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "调整薪资失败");
        }
    }
    
    // 获取工资单列表
    public Result<?> getPayslipList(String month, String department, String status, int page, int pageSize) {
        try {
            Page<SalaryPayslip> payslipsPage;
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
            
            if (month != null && !month.isEmpty() && department != null && !department.isEmpty() && status != null && !status.isEmpty()) {
                payslipsPage = salaryPayslipRepository.findByMonthAndDepartmentAndStatus(month, department, status, pageRequest);
            } else if (month != null && !month.isEmpty() && department != null && !department.isEmpty()) {
                payslipsPage = salaryPayslipRepository.findByMonthAndDepartment(month, department, pageRequest);
            } else if (month != null && !month.isEmpty() && status != null && !status.isEmpty()) {
                payslipsPage = salaryPayslipRepository.findByMonthAndStatus(month, status, pageRequest);
            } else if (month != null && !month.isEmpty()) {
                payslipsPage = salaryPayslipRepository.findByMonth(month, pageRequest);
            } else {
                payslipsPage = salaryPayslipRepository.findAll(pageRequest);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("payslips", payslipsPage.getContent());
            data.put("total", payslipsPage.getTotalElements());
            
            return Result.success(data);
        } catch (Exception e) {
            sysLogger.error("获取工资单列表失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "获取工资单列表失败");
        }
    }
    
    // 获取工资单详情
    public Result<?> getPayslipDetail(String id, String month) {
        try {
            if (id == null || month == null) {
                return Result.failure(ErrorCode.BAD_REQUEST, "缺少必要参数");
            }
            
            List<SalaryPayslip> payslips = salaryPayslipRepository.findByEmployeeIdAndMonth(id, month);
            if (payslips.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "未找到工资单");
            }
            
            return Result.success(payslips.get(0));
        } catch (Exception e) {
            sysLogger.error("获取工资单详情失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "获取工资单详情失败");
        }
    }
    
    // 生成工资单
    public Result<?> generatePayslip(PayslipRequest request) {
        try {
            String month = request.getMonth();
            String department = request.getDepartment();
            
            if (month == null || month.isEmpty()) {
                return Result.failure(ErrorCode.BAD_REQUEST, "月份不能为空");
            }
            
            // 获取已确认的薪资核算数据
            List<SalaryCalculation> calculations;
            if (department != null && !department.isEmpty()) {
                // 查找特定部门已确认的薪资核算数据
                Page<SalaryCalculation> page = salaryCalculationRepository.findByPeriodAndDepartment(month, department, PageRequest.of(0, 1000));
                calculations = page.getContent().stream()
                        .filter(calc -> "confirmed".equals(calc.getStatus()))
                        .toList();
            } else {
                // 查找所有已确认的薪资核算数据
                calculations = salaryCalculationRepository.findByPeriodAndStatus(month, "confirmed");
            }
            
            int generateCount = 0;
            
            // 生成工资单
            for (SalaryCalculation calc : calculations) {
                // 检查是否已存在工资单
                List<SalaryPayslip> existingPayslips = salaryPayslipRepository.findByEmployeeIdAndMonth(
                        calc.getEmployeeId(), month);
                
                if (!existingPayslips.isEmpty()) {
                    continue; // 已存在工资单，跳过
                }
                
                SalaryPayslip payslip = new SalaryPayslip();
                payslip.setEmployeeId(calc.getEmployeeId());
                payslip.setName(calc.getName());
                payslip.setDepartment(calc.getDepartment());
                payslip.setPosition(calc.getPosition());
                payslip.setMonth(month);
                
                // 计算社保和公积金
                BigDecimal baseSalary = calc.getBaseSalary();
                BigDecimal socialInsurance = baseSalary.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
                BigDecimal housingFund = baseSalary.multiply(new BigDecimal("0.07")).setScale(2, RoundingMode.HALF_UP);
                
                payslip.setBaseSalary(calc.getBaseSalary());
                payslip.setPerformanceSalary(calc.getPerformanceBonus());
                payslip.setOvertimePay(calc.getOvertimePay());
                payslip.setOtherBonus(calc.getAllowance());
                payslip.setTotalSalary(calc.getTotalSalary());
                
                payslip.setSocialInsurance(socialInsurance);
                payslip.setHousingFund(housingFund);
                payslip.setIncomeTax(calc.getTax());
                payslip.setOtherDeduction(calc.getDeduction());
                
                BigDecimal totalDeduction = socialInsurance
                        .add(housingFund)
                        .add(calc.getTax())
                        .add(calc.getDeduction());
                
                payslip.setTotalDeduction(totalDeduction);
                payslip.setActualSalary(calc.getTotalSalary().subtract(totalDeduction));
                
                payslip.setStatus("generated");
                payslip.setCreateTime(LocalDateTime.now());
                
                salaryPayslipRepository.save(payslip);
                generateCount++;
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("month", month);
            data.put("department", department);
            data.put("generateCount", generateCount);
            data.put("generateTime", LocalDateTime.now());
            
            return Result.success(data);
        } catch (Exception e) {
            sysLogger.error("生成工资单失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "生成工资单失败");
        }
    }
    
    // 发放工资单（批量）
    public Result<?> distributePayslips(PayslipRequest request) {
        try {
            String month = request.getMonth();
            String department = request.getDepartment();
            
            if (month == null || month.isEmpty()) {
                return Result.failure(ErrorCode.BAD_REQUEST, "月份不能为空");
            }
            
            List<SalaryPayslip> payslips;
            if (department != null && !department.isEmpty()) {
                payslips = salaryPayslipRepository.findByMonthAndDepartmentAndStatus(
                        month, department, "generated", PageRequest.of(0, 1000)).getContent();
            } else {
                payslips = salaryPayslipRepository.findByMonthAndStatus(
                        month, "generated", PageRequest.of(0, 1000)).getContent();
            }
            
            LocalDateTime distributeTime = LocalDateTime.now();
            int distributeCount = 0;
            
            for (SalaryPayslip payslip : payslips) {
                payslip.setStatus("distributed");
                payslip.setDistributeTime(distributeTime);
                salaryPayslipRepository.save(payslip);
                distributeCount++;
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("month", month);
            data.put("department", department);
            data.put("distributeCount", distributeCount);
            data.put("distributeTime", distributeTime);
            
            return Result.success(data);
        } catch (Exception e) {
            sysLogger.error("发放工资单失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "发放工资单失败");
        }
    }
    
    // 发放单个工资单
    public Result<?> distributeOnePayslip(PayslipRequest request) {
        try {
            String id = request.getId();
            String month = request.getMonth();
            
            if (id == null || month == null) {
                return Result.failure(ErrorCode.BAD_REQUEST, "缺少必要参数");
            }
            
            List<SalaryPayslip> payslips = salaryPayslipRepository.findByEmployeeIdAndMonth(id, month);
            if (payslips.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "未找到工资单");
            }
            
            SalaryPayslip payslip = payslips.get(0);
            payslip.setStatus("distributed");
            payslip.setDistributeTime(LocalDateTime.now());
            salaryPayslipRepository.save(payslip);
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            data.put("month", month);
            data.put("status", "distributed");
            data.put("distributeTime", LocalDateTime.now());
            
            return Result.success(data);
        } catch (Exception e) {
            sysLogger.error("发放工资单失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "发放工资单失败");
        }
    }
} 