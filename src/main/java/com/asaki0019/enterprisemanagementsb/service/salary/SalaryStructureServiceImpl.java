package com.asaki0019.enterprisemanagementsb.service.salary;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
import com.asaki0019.enterprisemanagementsb.entities.salary.BonusScheme;
import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryAdjustRule;
import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryGrade;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.repositories.BonusSchemeRepository;
import com.asaki0019.enterprisemanagementsb.repositories.DepartmentRepository;
import com.asaki0019.enterprisemanagementsb.repositories.SalaryAdjustRuleRepository;
import com.asaki0019.enterprisemanagementsb.repositories.SalaryGradeRepository;
import com.asaki0019.enterprisemanagementsb.request.salary.SalaryGradeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 薪资结构管理服务实现类
 */
@Service
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final SysLogger sysLogger;
    private final SalaryGradeRepository salaryGradeRepository;
    private final SalaryAdjustRuleRepository salaryAdjustRuleRepository;
    private final BonusSchemeRepository bonusSchemeRepository;
    private final ObjectMapper objectMapper;
    private final DepartmentRepository departmentRepository;
    
    private static final Logger log = LoggerFactory.getLogger(SalaryStructureServiceImpl.class);
    
    @Autowired
    public SalaryStructureServiceImpl(
            SysLogger sysLogger,
            SalaryGradeRepository salaryGradeRepository,
            SalaryAdjustRuleRepository salaryAdjustRuleRepository,
            BonusSchemeRepository bonusSchemeRepository,
            ObjectMapper objectMapper,
            DepartmentRepository departmentRepository) {
        this.sysLogger = sysLogger;
        this.salaryGradeRepository = salaryGradeRepository;
        this.salaryAdjustRuleRepository = salaryAdjustRuleRepository;
        this.bonusSchemeRepository = bonusSchemeRepository;
        this.objectMapper = objectMapper;
        this.departmentRepository = departmentRepository;
        
        // 初始化奖金方案数据
        try {
            if (bonusSchemeRepository.count() == 0) {
                initBonusSchemeData();
            }
        } catch (Exception e) {
            String errorMsg = MessageConstructor.constructPlainMessage("构造函数中初始化奖金方案数据失败", "error", e.getMessage());
            log.error("初始化奖金方案数据失败", e);
            sysLogger.error("SalaryStructure", "constructor", errorMsg, e);
        }
    }
    
    @Override
    public Result<?> getSalaryGrades(String department) {
        log.info("获取薪资等级列表，部门：{}", department);
        try {
            List<SalaryGrade> grades;
            
            // 根据部门查询，如果部门为空，则查询所有
            if (StringUtils.hasText(department)) {
                grades = salaryGradeRepository.findByDepartment(department);
            } else {
                grades = salaryGradeRepository.findAll();
            }
            
            // 转换为前端需要的格式
            List<Map<String, Object>> resultList = grades.stream()
                    .map(this::convertGradeToMap)
                    .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("grades", resultList);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取薪资等级列表异常", e);
            String errorMsg = MessageConstructor.constructPlainMessage("获取薪资等级列表失败", "department", department, "error", e.getMessage());
            sysLogger.error("SalaryStructure", "getSalaryGrades", errorMsg, e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "获取薪资等级列表失败");
        }
    }
    
    /**
     * 将SalaryGrade对象转换为Map
     */
    private Map<String, Object> convertGradeToMap(SalaryGrade grade) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", grade.getGradeCode());
        map.put("gradeName", grade.getGradeName());
        map.put("level", grade.getLevel());
        map.put("department", grade.getDepartment());
        map.put("minSalary", grade.getMinSalary());
        map.put("maxSalary", grade.getMaxSalary());
        map.put("description", grade.getDescription());
        return map;
    }
    
    @Override
    @Transactional
    public Result<?> saveSalaryGrade(SalaryGradeRequest request) {
        try {
            // 记录日志
            String logMessage = MessageConstructor.constructPlainMessage("保存薪资等级", 
                    "id", request.getId(),
                    "gradeName", request.getGradeName(), 
                    "level", request.getLevel() != null ? request.getLevel().toString() : null,
                    "department", request.getDepartment());
            sysLogger.info("SalaryStructure", "saveSalaryGrade", logMessage);
            
            // 参数校验
            if (!StringUtils.hasText(request.getGradeName())) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "等级名称不能为空");
            }
            if (request.getLevel() == null) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "等级级别不能为空");
            }
            if (request.getMinSalary() == null) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "最低薪资不能为空");
            }
            if (request.getMaxSalary() == null) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "最高薪资不能为空");
            }
            if (!StringUtils.hasText(request.getDepartment())) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "所属部门不能为空");
            }
            
            // 检查department是否存在于数据库中
            try {
                Department department = departmentRepository.findByName(request.getDepartment());
                if (department == null) {
                    sysLogger.warn("SalaryStructure", "saveSalaryGrade", 
                            MessageConstructor.constructPlainMessage("部门不存在", "department", request.getDepartment()));
                }
            } catch (Exception e) {
                // 这里选择继续执行，而不是返回错误
            }
            
            SalaryGrade grade;
            
            // 如果提供了ID，表示更新
            if (StringUtils.hasText(request.getId())) {
                Optional<SalaryGrade> existingGrade = salaryGradeRepository.findByGradeCode(request.getId());
                if (existingGrade.isEmpty()) {
                    String warnMsg = MessageConstructor.constructPlainMessage("未找到指定的薪资等级", "id", request.getId());
                    sysLogger.warn("SalaryStructure", "saveSalaryGrade", warnMsg);
                    return Result.failure(ErrorCode.NOT_FOUND, "未找到指定的薪资等级");
                }
                grade = existingGrade.get();
            } else {
                // 新增
                grade = new SalaryGrade();
                grade.setGradeCode("G" + System.currentTimeMillis());
            }
            
            // 更新字段
            grade.setGradeName(request.getGradeName());
            grade.setLevel(request.getLevel());
            grade.setMinSalary(request.getMinSalary());
            grade.setMaxSalary(request.getMaxSalary());
            grade.setDepartment(request.getDepartment());
            grade.setDescription(request.getDescription());
            
            // 保存
            try {
                grade = salaryGradeRepository.save(grade);
                sysLogger.info("SalaryStructure", "saveSalaryGrade", 
                        MessageConstructor.constructPlainMessage("薪资等级保存成功", "gradeCode", grade.getGradeCode()));
            } catch (Exception e) {
                String errorMsg = MessageConstructor.constructPlainMessage("保存薪资等级失败", "error", e.getMessage());
                sysLogger.error("SalaryStructure", "saveSalaryGrade", errorMsg, e);
                return Result.failure(ErrorCode.BUSINESS_ERROR, "保存薪资等级失败: " + e.getMessage());
            }
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("grade", convertGradeToMap(grade));
            
            return Result.success(result);
        } catch (Exception e) {
            String errorMsg = MessageConstructor.constructPlainMessage("处理薪资等级保存请求失败", "error", e.getMessage());
            sysLogger.error("SalaryStructure", "saveSalaryGrade", errorMsg, e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "保存薪资等级失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<?> getSalaryAdjustRules(String type) {
        try {
            // 记录日志
            String logMessage = MessageConstructor.constructPlainMessage("获取薪资调整规则列表", "type", type);
            sysLogger.info("SalaryStructure", "getSalaryAdjustRules", logMessage);
            
            List<SalaryAdjustRule> rules;
            
            // 根据类型查询
            if (StringUtils.hasText(type)) {
                rules = salaryAdjustRuleRepository.findByRuleType(type);
            } else {
                rules = salaryAdjustRuleRepository.findAll();
            }
            
            // 转换为前端格式
            List<Map<String, Object>> resultList = rules.stream()
                    .map(this::convertRuleToMap)
                    .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("rules", resultList);
            
            return Result.success(result);
        } catch (Exception e) {
            String errorMsg = MessageConstructor.constructPlainMessage("获取薪资调整规则列表失败", "type", type, "error", e.getMessage());
            sysLogger.error("SalaryStructure", "getSalaryAdjustRules", errorMsg, e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "获取薪资调整规则列表失败");
        }
    }
    
    /**
     * 将SalaryAdjustRule对象转换为Map
     */
    private Map<String, Object> convertRuleToMap(SalaryAdjustRule rule) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", rule.getRuleCode());
        map.put("ruleName", rule.getRuleName());
        map.put("ruleType", rule.getRuleType());
        map.put("applyTo", rule.getApplyTo());
        
        // 根据adjustRatio的类型进行不同处理
        if ("fixed".equals(rule.getRatioType())) {
            // 固定比例，直接转为Double
            map.put("adjustRatio", Double.parseDouble(rule.getAdjustRatio()));
        } else if ("range".equals(rule.getRatioType())) {
            // 范围比例，是一个JSON字符串，需要解析
            // 这里简化处理，直接返回字符串，前端自行解析
            map.put("adjustRatio", rule.getAdjustRatio());
        }
        
        map.put("effectiveDate", rule.getEffectiveDate().toString());
        map.put("description", rule.getDescription());
        return map;
    }
    
    @Override
    public Result<?> getBonusSchemes(String department, String year, Integer page, Integer pageSize) {
        try {
            // 记录日志
            String logMessage = MessageConstructor.constructPlainMessage("获取奖金方案列表", 
                    "department", department,
                    "year", year,
                    "page", page != null ? page.toString() : null,
                    "pageSize", pageSize != null ? pageSize.toString() : null);
            sysLogger.info("SalaryStructure", "getBonusSchemes", logMessage);
            
            // 处理页码和每页大小
            int actualPage = (page != null && page > 0) ? page - 1 : 0; // JPA分页从0开始
            int actualPageSize = (pageSize != null && pageSize > 0) ? pageSize : 10;
            
            // 分页查询
            Pageable pageable = PageRequest.of(actualPage, actualPageSize);
            Page<BonusScheme> bonusSchemePage = bonusSchemeRepository.findByDepartmentAndYearWithPagination(
                    department, year, pageable);
            
            // 转换为前端需要的数据格式
            List<Map<String, Object>> schemeList = bonusSchemePage.getContent().stream()
                    .map(this::convertBonusSchemeToMap)
                    .collect(Collectors.toList());
            
            // 从数据库获取部门列表
            List<String> departments = departmentRepository.findAll().stream()
                    .map(Department::getName)
                    .collect(Collectors.toList());
            // 添加"全部部门"选项
            departments.add(0, "全部部门");
            
            // 从数据库获取奖金方案中的所有年份
            List<String> years = bonusSchemeRepository.findDistinctYears();
            if (years == null || years.isEmpty()) {
                // 如果没有历史数据，添加当前年份作为默认值
                years = Collections.singletonList(String.valueOf(LocalDate.now().getYear()));
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("schemes", schemeList);
            result.put("total", bonusSchemePage.getTotalElements());
            result.put("departments", departments);
            result.put("years", years);
            
            return Result.success(result);
        } catch (Exception e) {
            String errorMsg = MessageConstructor.constructPlainMessage("获取奖金方案列表失败", 
                    "department", department,
                    "year", year,
                    "error", e.getMessage());
            sysLogger.error("SalaryStructure", "getBonusSchemes", errorMsg, e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "获取奖金方案列表失败");
        }
    }
    
    /**
     * 将BonusScheme对象转换为Map
     */
    private Map<String, Object> convertBonusSchemeToMap(BonusScheme scheme) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", scheme.getSchemeCode());
        map.put("schemeName", scheme.getSchemeName());
        map.put("department", scheme.getDepartment());
        map.put("year", scheme.getYear());
        map.put("formulaType", scheme.getFormulaType());
        
        // 将公式详情JSON字符串转换为对象
        try {
            if (scheme.getFormulaDetail() != null && !scheme.getFormulaDetail().isEmpty()) {
                map.put("formulaDetail", objectMapper.readValue(scheme.getFormulaDetail(), Map.class));
            }
        } catch (Exception e) {
            log.error("解析公式详情失败", e);
            map.put("formulaDetail", scheme.getFormulaDetail());
        }
        
        map.put("totalBudget", scheme.getTotalBudget());
        map.put("distributionMethod", scheme.getDistributionMethod());
        map.put("approvalStatus", scheme.getApprovalStatus());
        map.put("paymentDate", scheme.getPaymentDate());
        map.put("createdBy", scheme.getCreatedBy());
        map.put("createdTime", scheme.getCreatedTime() != null ? scheme.getCreatedTime().toString() : null);
        map.put("description", scheme.getDescription());
        
        return map;
    }
    
    /**
     * 初始化奖金方案数据
     */
    private void initBonusSchemeData() {
        try {
            // 记录日志
            String logMessage = MessageConstructor.constructPlainMessage("初始化奖金方案数据");
            sysLogger.info("SalaryStructure", "initBonusSchemeData", logMessage);
            
            // 创建几个基础的奖金方案案例
            BonusScheme scheme1 = new BonusScheme();
            scheme1.setSchemeCode("BS001");
            scheme1.setSchemeName("2023年技术部绩效奖金方案");
            scheme1.setDepartment("技术部");
            scheme1.setYear("2023");
            scheme1.setFormulaType("绩效百分比");
            scheme1.setDescription("基于个人年度绩效评估结果和部门KPI完成情况");
            scheme1.setTotalBudget(500000.0);
            scheme1.setCreatedTime(LocalDateTime.now());
            
            BonusScheme scheme2 = new BonusScheme();
            scheme2.setSchemeCode("BS002");
            scheme2.setSchemeName("2023年销售部提成奖金方案");
            scheme2.setDepartment("销售部");
            scheme2.setYear("2023");
            scheme2.setFormulaType("销售业绩比例");
            scheme2.setDescription("基于销售业绩和客户满意度评分");
            scheme2.setTotalBudget(600000.0);
            scheme2.setCreatedTime(LocalDateTime.now());
            
            try {
                // 保存到数据库
                bonusSchemeRepository.save(scheme1);
                bonusSchemeRepository.save(scheme2);
                
                String successMsg = MessageConstructor.constructPlainMessage("初始化奖金方案数据成功", 
                        "scheme1", scheme1.getSchemeName(), 
                        "scheme2", scheme2.getSchemeName());
                sysLogger.info("SalaryStructure", "initBonusSchemeData", successMsg);
            } catch (Exception e) {
                String errorMsg = MessageConstructor.constructPlainMessage("保存奖金方案数据失败", "error", e.getMessage());
                sysLogger.error("SalaryStructure", "initBonusSchemeData", errorMsg, e);
            }
        } catch (Exception e) {
            String errorMsg = MessageConstructor.constructPlainMessage("初始化奖金方案数据异常", "error", e.getMessage());
            sysLogger.error("SalaryStructure", "initBonusSchemeData", errorMsg, e);
        }
    }
} 