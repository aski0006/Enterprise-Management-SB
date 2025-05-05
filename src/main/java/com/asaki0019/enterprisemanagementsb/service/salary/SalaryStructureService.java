package com.asaki0019.enterprisemanagementsb.service.salary;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.exception.BusinessException;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.entities.salary.AdjustRule;
import com.asaki0019.enterprisemanagementsb.entities.salary.BonusScheme;
import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryGrade;
import com.asaki0019.enterprisemanagementsb.mapper.salary.AdjustRuleRepository;
import com.asaki0019.enterprisemanagementsb.mapper.salary.BonusSchemeRepository;
import com.asaki0019.enterprisemanagementsb.mapper.salary.SalaryGradeRepository;
import com.asaki0019.enterprisemanagementsb.request.salary.AdjustRuleRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.BonusSchemeRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.SalaryGradeRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class SalaryStructureService {
    
    private final SalaryGradeRepository salaryGradeRepository;
    private final AdjustRuleRepository adjustRuleRepository;
    private final BonusSchemeRepository bonusSchemeRepository;
    private final SysLogger sysLogger;
    
    @Autowired
    public SalaryStructureService(
            SalaryGradeRepository salaryGradeRepository,
            AdjustRuleRepository adjustRuleRepository,
            BonusSchemeRepository bonusSchemeRepository,
            SysLogger sysLogger) {
        this.salaryGradeRepository = salaryGradeRepository;
        this.adjustRuleRepository = adjustRuleRepository;
        this.bonusSchemeRepository = bonusSchemeRepository;
        this.sysLogger = sysLogger;
    }
    
    // 获取薪资等级列表
    public Result<?> getSalaryGrades(String department, int page, int pageSize) {
        try {
            Page<SalaryGrade> gradesPage;
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
            
            if (department != null && !department.equals("全部部门")) {
                gradesPage = salaryGradeRepository.findByDepartmentOrderByGradeNameAscLevelAsc(department, pageRequest);
            } else {
                gradesPage = salaryGradeRepository.findAllByOrderByGradeNameAscLevelAsc(pageRequest);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("grades", gradesPage.getContent());
            data.put("total", gradesPage.getTotalElements());
            
            // 获取所有部门列表
            List<String> departments = new ArrayList<>();
            departments.add("全部部门");
            departments.add("技术部");
            departments.add("人力资源部");
            departments.add("财务部");
            departments.add("市场部");
            departments.add("销售部");
            
            data.put("departments", departments);
            
            return Result.success(data);
        } catch (Exception e) {
            sysLogger.error("获取薪资等级列表失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "获取薪资等级列表失败");
        }
    }
    
    // 保存薪资等级
    public Result<?> saveSalaryGrade(SalaryGradeRequest request) {
        try {
            // 校验请求参数
            if (request.getMinSalary().compareTo(request.getMaxSalary()) > 0) {
                return Result.failure(ErrorCode.BAD_REQUEST, "最低薪资不能高于最高薪资");
            }
            
            SalaryGrade salaryGrade;
            boolean isNew = request.getId() == null || request.getId().isEmpty();
            
            if (isNew) {
                salaryGrade = new SalaryGrade();
                salaryGrade.setCreatedTime(LocalDateTime.now());
            } else {
                salaryGrade = salaryGradeRepository.findById(request.getId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "薪资等级不存在"));
            }
            
            // 复制属性
            BeanUtils.copyProperties(request, salaryGrade);
            
            // 自动计算中位薪资
            salaryGrade.setMidSalary(
                    request.getMinSalary().add(request.getMaxSalary()).divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP)
            );
            
            // 自动生成标题
            if (request.getTitle() == null || request.getTitle().isEmpty()) {
                salaryGrade.setTitle(request.getGradeName() + request.getLevel());
            }
            
            salaryGrade.setUpdatedTime(LocalDateTime.now());
            
            salaryGradeRepository.save(salaryGrade);
            
            return Result.success(salaryGrade);
        } catch (BusinessException e) {
            sysLogger.error("保存薪资等级失败", e);
            throw e;
        } catch (Exception e) {
            sysLogger.error("保存薪资等级失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "保存薪资等级失败");
        }
    }
    
    // 获取调薪规则列表
    public Result<?> getAdjustRules(String type, int page, int pageSize) {
        try {
            Page<AdjustRule> rulesPage;
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
            
            if (type != null && !type.isEmpty()) {
                rulesPage = adjustRuleRepository.findByRuleType(type, pageRequest);
            } else {
                rulesPage = adjustRuleRepository.findAll(pageRequest);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("rules", rulesPage.getContent());
            data.put("total", rulesPage.getTotalElements());
            
            List<String> ruleTypes = Arrays.asList(
                    "定期调薪", "晋升调薪", "绩效调薪", "转正调薪", "特殊调薪"
            );
            data.put("ruleTypes", ruleTypes);
            
            return Result.success(data);
        } catch (Exception e) {
            sysLogger.error("获取调薪规则列表失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "获取调薪规则列表失败");
        }
    }
    
    // 保存调薪规则
    public Result<?> saveAdjustRule(AdjustRuleRequest request) {
        try {
            AdjustRule adjustRule;
            boolean isNew = request.getId() == null || request.getId().isEmpty();
            
            if (isNew) {
                adjustRule = new AdjustRule();
                adjustRule.setCreatedTime(LocalDateTime.now());
                adjustRule.setCreatedBy("当前用户"); // 应该从当前登录用户中获取
            } else {
                adjustRule = adjustRuleRepository.findById(request.getId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "调薪规则不存在"));
            }
            
            // 复制属性
            BeanUtils.copyProperties(request, adjustRule);
            
            adjustRuleRepository.save(adjustRule);
            
            return Result.success(adjustRule);
        } catch (BusinessException e) {
            sysLogger.error("保存调薪规则失败", e);
            throw e;
        } catch (Exception e) {
            sysLogger.error("保存调薪规则失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "保存调薪规则失败");
        }
    }
    
    // 获取奖金方案列表
    public Result<?> getBonusSchemes(String department, String year, int page, int pageSize) {
        try {
            Page<BonusScheme> schemesPage;
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
            
            if (department != null && !department.equals("全部部门") && year != null && !year.isEmpty()) {
                schemesPage = bonusSchemeRepository.findByDepartmentAndYear(department, year, pageRequest);
            } else if (department != null && !department.equals("全部部门")) {
                schemesPage = bonusSchemeRepository.findByDepartment(department, pageRequest);
            } else if (year != null && !year.isEmpty()) {
                schemesPage = bonusSchemeRepository.findByYear(year, pageRequest);
            } else {
                schemesPage = bonusSchemeRepository.findAll(pageRequest);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("schemes", schemesPage.getContent());
            data.put("total", schemesPage.getTotalElements());
            
            // 获取所有部门列表
            List<String> departments = new ArrayList<>();
            departments.add("全部部门");
            departments.add("技术部");
            departments.add("人力资源部");
            departments.add("财务部");
            departments.add("市场部");
            departments.add("销售部");
            
            data.put("departments", departments);
            
            // 获取年份列表
            List<String> years = Arrays.asList("2023", "2022", "2021");
            data.put("years", years);
            
            return Result.success(data);
        } catch (Exception e) {
            sysLogger.error("获取奖金方案列表失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "获取奖金方案列表失败");
        }
    }
    
    // 保存奖金方案
    public Result<?> saveBonusScheme(BonusSchemeRequest request) {
        try {
            BonusScheme bonusScheme;
            boolean isNew = request.getId() == null || request.getId().isEmpty();
            
            if (isNew) {
                bonusScheme = new BonusScheme();
                bonusScheme.setCreatedTime(LocalDateTime.now());
                bonusScheme.setCreatedBy("当前用户"); // 应该从当前登录用户中获取
            } else {
                bonusScheme = bonusSchemeRepository.findById(request.getId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "奖金方案不存在"));
            }
            
            // 复制属性
            BeanUtils.copyProperties(request, bonusScheme);
            
            bonusSchemeRepository.save(bonusScheme);
            
            return Result.success(bonusScheme);
        } catch (BusinessException e) {
            sysLogger.error("保存奖金方案失败", e);
            throw e;
        } catch (Exception e) {
            sysLogger.error("保存奖金方案失败", e);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "保存奖金方案失败");
        }
    }
} 