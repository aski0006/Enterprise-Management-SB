package com.asaki0019.enterprisemanagementsb.service.performance;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.performance.PerformanceCriteria;
import com.asaki0019.enterprisemanagementsb.entities.performance.PerformanceEvaluation;
import com.asaki0019.enterprisemanagementsb.entities.performance.PerformanceLevel;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.repositories.DepartmentRepository;
import com.asaki0019.enterprisemanagementsb.repositories.EmployeeRepository;
import com.asaki0019.enterprisemanagementsb.repositories.PerformanceCriteriaRepository;
import com.asaki0019.enterprisemanagementsb.repositories.PerformanceEvaluationRepository;
import com.asaki0019.enterprisemanagementsb.repositories.PerformanceLevelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 绩效管理服务实现类
 */
@Service
public class PerformanceServiceImpl implements PerformanceService {

    private final SysLogger sysLogger;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PerformanceEvaluationRepository performanceEvaluationRepository;
    private final PerformanceCriteriaRepository performanceCriteriaRepository;
    private final PerformanceLevelRepository performanceLevelRepository;
    
    private static final Logger log = LoggerFactory.getLogger(PerformanceServiceImpl.class);
    
    @Autowired
    public PerformanceServiceImpl(
            SysLogger sysLogger,
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            PerformanceEvaluationRepository performanceEvaluationRepository,
            PerformanceCriteriaRepository performanceCriteriaRepository,
            PerformanceLevelRepository performanceLevelRepository) {
        this.sysLogger = sysLogger;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.performanceEvaluationRepository = performanceEvaluationRepository;
        this.performanceCriteriaRepository = performanceCriteriaRepository;
        this.performanceLevelRepository = performanceLevelRepository;
    }
    
    @Override
    public Result<?> getEmployeeEvaluations(String employeeId, String year, String quarter, Integer page, Integer pageSize) {
        log.info("获取员工绩效评估列表，employeeId={}，year={}，quarter={}", employeeId, year, quarter);
        try {
            // 处理年份格式，将ISO日期格式转换为简单年份格式
            if (StringUtils.hasText(year) && year.contains("-")) {
                try {
                    // 尝试从ISO日期字符串中提取年份
                    year = year.substring(0, 4);
                    log.debug("转换后的年份格式: {}", year);
                } catch (Exception e) {
                    log.warn("年份格式转换失败: {}", year, e);
                }
            }
            
            // 处理季度格式
            Integer quarterNum = null;
            if (StringUtils.hasText(quarter)) {
                try {
                    // 将季度字符串转换为Integer类型
                    quarterNum = Integer.parseInt(quarter.trim());
                    log.debug("转换后的季度数字: {}", quarterNum);
                } catch (NumberFormatException e) {
                    log.warn("季度转换为数字失败: {}", quarter, e);
                    return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "季度参数格式错误");
                }
            }
            
            List<PerformanceEvaluation> evaluations;
            long totalElements = 0;
            
            // 根据参数进行查询
            if (StringUtils.hasText(employeeId)) {
                Optional<Employee> employee = employeeRepository.findById(Integer.parseInt(employeeId));
                if (employee.isEmpty()) {
                    return Result.failure(ErrorCode.NOT_FOUND, "员工不存在");
                }
                
                if (StringUtils.hasText(year) && quarterNum != null) {
                    evaluations = performanceEvaluationRepository.findByEmployeeAndYearAndQuarter(
                            employee.get(), year, quarterNum);
                } else if (StringUtils.hasText(year)) {
                    evaluations = performanceEvaluationRepository.findByEmployeeAndYear(
                            employee.get(), year);
                } else {
                    evaluations = performanceEvaluationRepository.findByEmployee(employee.get());
                }
                totalElements = evaluations.size();
                
                // 手动分页
                int start = page == null ? 0 : page * (pageSize == null ? 10 : pageSize);
                int end = Math.min(evaluations.size(), start + (pageSize == null ? 10 : pageSize));
                evaluations = start < end ? evaluations.subList(start, end) : new ArrayList<>();
            } else {
                // 如果没有指定员工，则分页查询所有
                PageRequest pageRequest = PageRequest.of(
                        page == null ? 0 : Math.max(0, page - 1),
                        pageSize == null ? 10 : pageSize,
                        Sort.by(Sort.Direction.DESC, "evaluationDate"));
                
                Page<PerformanceEvaluation> evaluationPage;
                if (StringUtils.hasText(year) && quarterNum != null) {
                    evaluationPage = performanceEvaluationRepository.findByYearAndQuarter(
                            year, quarterNum, pageRequest);
                } else if (StringUtils.hasText(year)) {
                    evaluationPage = performanceEvaluationRepository.findByYear(year, pageRequest);
                } else {
                    evaluationPage = performanceEvaluationRepository.findAll(pageRequest);
                }
                
                evaluations = evaluationPage.getContent();
                totalElements = evaluationPage.getTotalElements();
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("evaluations", convertEvaluationsToMapList(evaluations));
            result.put("total", totalElements);
            
            // 添加汇总信息
            Map<String, Object> summary = new HashMap<>();
            if (!evaluations.isEmpty()) {
                // 计算平均分
                double avgScore = evaluations.stream()
                    .mapToDouble(PerformanceEvaluation::getScore)
                    .average()
                    .orElse(0);
                summary.put("avgScore", Math.round(avgScore * 10) / 10.0); // 保留一位小数
                
                // 计算等级分布
                Map<String, Long> levelDistribution = evaluations.stream()
                    .collect(Collectors.groupingBy(PerformanceEvaluation::getLevel, Collectors.counting()));
                summary.put("levelDistribution", levelDistribution);
            } else {
                // 即使没有数据也提供空的汇总信息结构
                summary.put("avgScore", 0.0);
                summary.put("levelDistribution", new HashMap<String, Long>());
            }
            result.put("summary", summary);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取员工绩效评估列表异常", e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "获取员工绩效评估列表失败");
        }
    }
    
    /**
     * 将PerformanceEvaluation对象转换为Map列表
     */
    private List<Map<String, Object>> convertEvaluationsToMapList(List<PerformanceEvaluation> evaluations) {
        return evaluations.stream()
                .map(this::convertEvaluationToMap)
                .collect(Collectors.toList());
    }
    
    /**
     * 将PerformanceEvaluation对象转换为Map
     */
    private Map<String, Object> convertEvaluationToMap(PerformanceEvaluation evaluation) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", evaluation.getEvaluationCode());
        map.put("employeeId", evaluation.getEmployee().getEmployeeId());
        map.put("employeeName", evaluation.getEmployee().getFirstName() + " " + evaluation.getEmployee().getLastName());
        map.put("department", evaluation.getEmployee().getDepartment().getName());
        map.put("position", evaluation.getEmployee().getPosition().getTitle());
        map.put("year", evaluation.getYear());
        map.put("quarter", evaluation.getQuarter());
        map.put("evaluationPeriod", evaluation.getEvaluationPeriod());
        map.put("score", evaluation.getScore());
        map.put("level", evaluation.getLevel());
        map.put("status", evaluation.getStatus());
        map.put("evaluator", evaluation.getEvaluator());
        map.put("evaluationDate", evaluation.getEvaluationDate().toString());
        if (evaluation.getConfirmDate() != null) {
            map.put("confirmDate", evaluation.getConfirmDate().toString());
        }
        map.put("comments", evaluation.getManagerComments());
        return map;
    }
    
    @Override
    public Result<?> getEvaluationDetail(String id) {
        log.info("获取评估详情，id={}", id);
        try {
            if (!StringUtils.hasText(id)) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "评估ID不能为空");
            }
            
            Optional<PerformanceEvaluation> evaluation = performanceEvaluationRepository.findByEvaluationCode(id);
            if (evaluation.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "未找到指定的绩效评估");
            }
            
            // 转换为前端需要的格式
            PerformanceEvaluation eval = evaluation.get();
            Map<String, Object> result = new HashMap<>();
            result.put("id", eval.getEvaluationCode());
            result.put("employeeId", eval.getEmployee().getEmployeeId());
            result.put("employeeName", eval.getEmployee().getFirstName() + " " + eval.getEmployee().getLastName());
            result.put("department", eval.getEmployee().getDepartment().getName());
            result.put("position", eval.getEmployee().getPosition().getTitle());
            result.put("year", eval.getYear());
            result.put("quarter", eval.getQuarter());
            result.put("evaluationPeriod", eval.getEvaluationPeriod());
            result.put("score", eval.getScore());
            result.put("level", eval.getLevel());
            result.put("status", eval.getStatus());
            result.put("evaluator", eval.getEvaluator());
            result.put("evaluationDate", eval.getEvaluationDate().toString());
            if (eval.getConfirmDate() != null) {
                result.put("confirmDate", eval.getConfirmDate().toString());
            }
            result.put("managerComments", eval.getManagerComments());
            result.put("employeeComments", eval.getEmployeeComments());
            result.put("improvementPlan", eval.getImprovementPlan());
            
            // 评估详情和下一阶段目标，直接返回JSON字符串，前端自行解析
            result.put("evaluationDetails", eval.getEvaluationDetails());
            result.put("nextGoals", eval.getNextGoals());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取评估详情异常", e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "获取评估详情失败");
        }
    }
    
    @Override
    public Result<?> getDepartmentSummary(String year, String quarter, String department) {
        log.info("获取部门绩效汇总，year={}，quarter={}，department={}", year, quarter, department);
        try {
            // 转换季度参数
            Integer quarterNum = null;
            if (StringUtils.hasText(quarter)) {
                try {
                    quarterNum = Integer.parseInt(quarter.trim());
                    log.debug("转换后的季度数字: {}", quarterNum);
                } catch (NumberFormatException e) {
                    log.warn("季度转换为数字失败: {}", quarter, e);
                    return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "季度参数格式错误");
                }
            }
            
            // 查询所有评估
            List<PerformanceEvaluation> allEvaluations = performanceEvaluationRepository.findAll();
            
            // 将quarterNum赋值给final变量以便在Lambda表达式中使用
            final Integer finalQuarterNum = quarterNum;
            
            // 按部门分组
            Map<String, List<PerformanceEvaluation>> departmentEvaluations = allEvaluations.stream()
                    .filter(e -> (year == null || e.getYear().equals(year)) &&
                                (finalQuarterNum == null || e.getQuarter().equals(finalQuarterNum)))
                    .collect(Collectors.groupingBy(e -> e.getEmployee().getDepartment().getName()));
            
            // 如果指定了部门，则只返回该部门的汇总
            if (StringUtils.hasText(department)) {
                if (!departmentEvaluations.containsKey(department)) {
                    return Result.failure(ErrorCode.NOT_FOUND, "未找到指定部门的绩效数据");
                }
                
                List<Map<String, Object>> summary = new ArrayList<>();
                summary.add(createDepartmentSummary(department, departmentEvaluations.get(department)));
                return Result.success(summary);
            }
            
            // 返回所有部门的汇总
            List<Map<String, Object>> summaries = new ArrayList<>();
            for (Map.Entry<String, List<PerformanceEvaluation>> entry : departmentEvaluations.entrySet()) {
                summaries.add(createDepartmentSummary(entry.getKey(), entry.getValue()));
            }
            
            return Result.success(summaries);
        } catch (Exception e) {
            log.error("获取部门绩效汇总异常", e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "获取部门绩效汇总失败");
        }
    }
    
    /**
     * 创建部门绩效汇总
     */
    private Map<String, Object> createDepartmentSummary(String department, List<PerformanceEvaluation> evaluations) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("department", department);
        summary.put("employeeCount", evaluations.size());
        
        // 评估完成情况
        Map<String, Object> evaluationStats = new HashMap<>();
        long completed = evaluations.stream()
                .filter(e -> "已确认".equals(e.getStatus()))
                .count();
        evaluationStats.put("completed", completed);
        evaluationStats.put("pending", evaluations.size() - completed);
        evaluationStats.put("completionRate", evaluations.size() > 0 ? 
                (double) completed / evaluations.size() : 0);
        summary.put("evaluationStats", evaluationStats);
        
        // 等级分布
        Map<String, Long> levelDistribution = evaluations.stream()
                .collect(Collectors.groupingBy(PerformanceEvaluation::getLevel, Collectors.counting()));
        summary.put("levelDistribution", levelDistribution);
        
        // 平均分
        double averageScore = evaluations.stream()
                .mapToDouble(PerformanceEvaluation::getScore)
                .average()
                .orElse(0);
        summary.put("averageScore", averageScore);
        
        // 优秀员工
        List<Map<String, Object>> topPerformers = evaluations.stream()
                .filter(e -> "A".equals(e.getLevel()))
                .sorted((e1, e2) -> Double.compare(e2.getScore(), e1.getScore()))
                .limit(2)
                .map(e -> {
                    Map<String, Object> performer = new HashMap<>();
                    performer.put("employeeId", e.getEmployee().getEmployeeId());
                    performer.put("employeeName", e.getEmployee().getFirstName() + " " + e.getEmployee().getLastName());
                    performer.put("score", e.getScore());
                    performer.put("level", e.getLevel());
                    return performer;
                })
                .collect(Collectors.toList());
        summary.put("topPerformers", topPerformers);
        
        return summary;
    }
    
    @Override
    public Result<?> getPerformanceCriteria(String departmentId, String position) {
        log.info("获取绩效评估标准，departmentId={}，position={}", departmentId, position);
        try {
            List<PerformanceCriteria> commonCriterias = performanceCriteriaRepository.findByDepartmentIsNullAndPositionIsNull();
            
            // 获取部门特定标准
            List<PerformanceCriteria> departmentCriterias = new ArrayList<>();
            if (StringUtils.hasText(departmentId)) {
                departmentCriterias = performanceCriteriaRepository.findByDepartment(departmentId);
            }
            
            // 获取职位特定标准
            List<PerformanceCriteria> positionCriterias = new ArrayList<>();
            if (StringUtils.hasText(position)) {
                positionCriterias = performanceCriteriaRepository.findByPosition(position);
            }
            
            // 获取所有绩效等级
            List<PerformanceLevel> performanceLevels = performanceLevelRepository.findAll();
            
            // 转换为前端需要的格式
            Map<String, Object> result = new HashMap<>();
            
            // 通用标准
            result.put("commonCriteria", convertCriteriasToMapList(commonCriterias));
            
            // 特定标准
            Map<String, List<Map<String, Object>>> specificCriteria = new HashMap<>();
            if (!departmentCriterias.isEmpty()) {
                specificCriteria.put(departmentId, convertCriteriasToMapList(departmentCriterias));
            }
            result.put("specificCriteria", specificCriteria);
            
            // 绩效等级
            result.put("performanceLevels", convertLevelsToMapList(performanceLevels));
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取绩效评估标准异常", e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "获取绩效评估标准失败");
        }
    }
    
    /**
     * 将PerformanceCriteria对象转换为Map列表
     */
    private List<Map<String, Object>> convertCriteriasToMapList(List<PerformanceCriteria> criterias) {
        return criterias.stream()
                .map(this::convertCriteriaToMap)
                .collect(Collectors.toList());
    }
    
    /**
     * 将PerformanceCriteria对象转换为Map
     */
    private Map<String, Object> convertCriteriaToMap(PerformanceCriteria criteria) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", criteria.getName());
        map.put("weight", criteria.getWeight());
        map.put("items", criteria.getItems());
        map.put("description", criteria.getDescription());
        return map;
    }
    
    /**
     * 将PerformanceLevel对象转换为Map列表
     */
    private List<Map<String, Object>> convertLevelsToMapList(List<PerformanceLevel> levels) {
        return levels.stream()
                .map(this::convertLevelToMap)
                .collect(Collectors.toList());
    }
    
    /**
     * 将PerformanceLevel对象转换为Map
     */
    private Map<String, Object> convertLevelToMap(PerformanceLevel level) {
        Map<String, Object> map = new HashMap<>();
        map.put("level", level.getLevel());
        map.put("name", level.getName());
        map.put("scoreRange", List.of(level.getMinScore(), level.getMaxScore()));
        map.put("description", level.getDescription());
        map.put("bonusRatio", level.getBonusRatio());
        return map;
    }
} 