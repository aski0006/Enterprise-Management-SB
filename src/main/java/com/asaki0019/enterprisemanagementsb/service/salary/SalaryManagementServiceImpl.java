package com.asaki0019.enterprisemanagementsb.service.salary;

import com.asaki0019.enterprisemanagementsb.core.authContext.AuthContext;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.employee.Position;
import com.asaki0019.enterprisemanagementsb.entities.salary.EmployeeSalary;
import com.asaki0019.enterprisemanagementsb.entities.salary.Payslip;
import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryChangeHistory;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.repositories.DepartmentRepository;
import com.asaki0019.enterprisemanagementsb.repositories.EmployeeRepository;
import com.asaki0019.enterprisemanagementsb.repositories.EmployeeSalaryRepository;
import com.asaki0019.enterprisemanagementsb.repositories.PayslipRepository;
import com.asaki0019.enterprisemanagementsb.repositories.SalaryChangeHistoryRepository;
import com.asaki0019.enterprisemanagementsb.request.salary.EmployeeSalaryRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.PayslipDistributeOneRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.PayslipDistributeRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.PayslipGenerateRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.PayslipRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.SalaryAdjustRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.SalaryCalculationRequest;
import com.asaki0019.enterprisemanagementsb.request.salary.SalaryUpdateRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;
import java.text.SimpleDateFormat;

/**
 * 薪资管理服务实现类
 */
@Service
public class SalaryManagementServiceImpl implements SalaryManagementService {
    
    private final SysLogger sysLogger;
    private final EmployeeRepository employeeRepository;
    private final EmployeeSalaryRepository employeeSalaryRepository;
    private final SalaryChangeHistoryRepository salaryChangeHistoryRepository;
    private final PayslipRepository payslipRepository;
    private final DepartmentRepository departmentRepository;
    
    private static final Logger log = LoggerFactory.getLogger(SalaryManagementServiceImpl.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    public SalaryManagementServiceImpl(
            SysLogger sysLogger,
            EmployeeRepository employeeRepository,
            EmployeeSalaryRepository employeeSalaryRepository,
            SalaryChangeHistoryRepository salaryChangeHistoryRepository,
            PayslipRepository payslipRepository,
            DepartmentRepository departmentRepository) {
        this.sysLogger = sysLogger;
        this.employeeRepository = employeeRepository;
        this.employeeSalaryRepository = employeeSalaryRepository;
        this.salaryChangeHistoryRepository = salaryChangeHistoryRepository;
        this.payslipRepository = payslipRepository;
        this.departmentRepository = departmentRepository;
    }
    
    @Override
    public Result<?> getEmployeeSalaries(EmployeeSalaryRequest request) {
        try {
            // 记录日志
            String requestParams = MessageConstructor.constructPlainMessage("查询员工薪资档案列表", 
                    "name", request.getName(),
                    "departmentId", request.getDepartmentId(),
                    "positionId", request.getPositionId(),
                    "salaryMin", request.getSalaryMin(),
                    "salaryMax", request.getSalaryMax(),
                    "page", request.getPage(),
                    "pageSize", request.getPageSize());
            
            sysLogger.info("SalaryManagement", "getEmployeeSalaries", requestParams);
            
            // 分页参数处理 - 确保默认从第0页开始
            int page = request.getPage() != null ? request.getPage() : 0;
            int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
            
            log.debug("分页参数: page={}, pageSize={}", page, pageSize);
            
            // 构建查询规范
            Specification<Employee> specification = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 添加查询条件: 1=1
                predicates.add(criteriaBuilder.conjunction());
                
                // 根据姓名查询
                if (StringUtils.hasText(request.getName())) {
                    Predicate firstNamePredicate = criteriaBuilder.like(
                            root.get("firstName"), "%" + request.getName() + "%");
                    Predicate lastNamePredicate = criteriaBuilder.like(
                            root.get("lastName"), "%" + request.getName() + "%");
                    predicates.add(criteriaBuilder.or(firstNamePredicate, lastNamePredicate));
                }
                
                // 根据部门查询
                if (StringUtils.hasText(request.getDepartmentId())) {
                    try {
                        Integer departmentId = Integer.parseInt(request.getDepartmentId());
                        Join<Employee, Department> departmentJoin = root.join("department");
                        predicates.add(criteriaBuilder.equal(departmentJoin.get("departmentId"), departmentId));
                    } catch (NumberFormatException e) {
                        sysLogger.warn("SalaryManagement", "getEmployeeSalaries", 
                                MessageConstructor.constructPlainMessage("部门ID格式不正确", "departmentId", request.getDepartmentId()));
                    }
                }
                
                // 根据职位查询
                if (StringUtils.hasText(request.getPositionId())) {
                    try {
                        Integer positionId = Integer.parseInt(request.getPositionId());
                        Join<Employee, Position> positionJoin = root.join("position");
                        predicates.add(criteriaBuilder.equal(positionJoin.get("positionId"), positionId));
                    } catch (NumberFormatException e) {
                        sysLogger.warn("SalaryManagement", "getEmployeeSalaries", 
                                MessageConstructor.constructPlainMessage("职位ID格式不正确", "positionId", request.getPositionId()));
                    }
                }
                
                // 根据最低薪资查询
                if (request.getSalaryMin() != null) {
                    Join<Employee, Position> positionJoin = root.join("position");
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(positionJoin.get("baseSalary"), request.getSalaryMin()));
                }
                
                // 根据最高薪资查询
                if (request.getSalaryMax() != null) {
                    Join<Employee, Position> positionJoin = root.join("position");
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(positionJoin.get("baseSalary"), request.getSalaryMax()));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            
            // 执行分页查询
            PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "hireDate"));
            Page<Employee> employeePage = employeeRepository.findAll(specification, pageRequest);
            
            log.debug("查询到员工总数: {}", employeePage.getTotalElements());
            log.debug("当前页员工数量: {}", employeePage.getContent().size());
            
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Employee employee : employeePage.getContent()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", employee.getEmployeeId());
                
                // 处理潜在的空值问题
                String firstName = employee.getFirstName() != null ? employee.getFirstName() : "";
                String lastName = employee.getLastName() != null ? employee.getLastName() : "";
                map.put("name", firstName + " " + lastName);
                
                // 安全地获取部门信息
                if (employee.getDepartment() != null) {
                    map.put("departmentId", employee.getDepartment().getDepartmentId());
                    map.put("departmentName", employee.getDepartment().getName());
                } else {
                    map.put("departmentId", null);
                    map.put("departmentName", "");
                }
                
                // 安全地获取职位信息
                if (employee.getPosition() != null) {
                    map.put("positionId", employee.getPosition().getPositionId());
                    map.put("positionName", employee.getPosition().getTitle());
                    
                    // 获取基本工资
                    Double baseSalary = employee.getPosition().getBaseSalary();
                    map.put("baseSalary", baseSalary != null ? baseSalary : 0.0);
                } else {
                    map.put("positionId", null);
                    map.put("positionName", "");
                    map.put("baseSalary", 0.0);
                }
                
                // 查询员工当前有效的薪资记录
                Optional<EmployeeSalary> salaryOpt = employeeSalaryRepository.findByEmployeeAndIsActiveTrue(employee);
                if (salaryOpt.isPresent()) {
                    EmployeeSalary salary = salaryOpt.get();
                    map.put("salaryId", salary.getId());
                    map.put("performanceBonus", salary.getPerformanceBonus());
                    map.put("allowance", salary.getAllowance());
                    map.put("socialInsurance", salary.getSocialInsurance());
                    map.put("effectiveDate", salary.getEffectiveDate());
                    map.put("totalSalary", calculateTotalSalary(salary));
                } else {
                    // 如果没有薪资记录，设置默认值
                    map.put("salaryId", null);
                    map.put("performanceBonus", 0.0);
                    map.put("allowance", 0.0);
                    map.put("socialInsurance", 0.0);
                    map.put("effectiveDate", null);
                    map.put("totalSalary", map.get("baseSalary"));
                }
                
                map.put("hireDate", employee.getHireDate());
                resultList.add(map);
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("records", resultList);
            result.put("total", employeePage.getTotalElements());
            result.put("page", employeePage.getNumber());
            result.put("pageSize", employeePage.getSize());
            
            return Result.success(result);
        } catch (Exception e) {
            String errorMsg = MessageConstructor.constructPlainMessage("查询员工薪资档案列表失败", "错误", e.getMessage());
            sysLogger.error("SalaryManagement", "getEmployeeSalaries", errorMsg, e);
            log.error("查询员工薪资档案列表失败", e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "查询员工薪资档案列表失败");
        }
    }
    
    // 辅助方法：计算总薪资
    private Double calculateTotalSalary(EmployeeSalary salary) {
        double total = 0.0;
        if (salary.getBaseSalary() != null) total += salary.getBaseSalary();
        if (salary.getPerformanceBonus() != null) total += salary.getPerformanceBonus();
        if (salary.getAllowance() != null) total += salary.getAllowance();
        return total;
    }
    
    @Override
    public Result<?> getEmployeeSalaryDetail(String employeeId) {
        try {
            // 记录日志，使用MessageConstructor构造消息
            String logMessage = MessageConstructor.constructPlainMessage("查询员工薪资详情", "employeeId", employeeId);
            sysLogger.info("SalaryManagement", "getEmployeeSalaryDetail", logMessage);
            
            // 检查是否请求的是工资变更历史记录
            if ("history".equalsIgnoreCase(employeeId)) {
                return handleSalaryHistory();
            }
            
            // 解析ID参数
            Integer id;
            try {
                id = Integer.valueOf(employeeId);
            } catch (NumberFormatException e) {
                String errorMsg = MessageConstructor.constructPlainMessage("员工ID格式无效", "employeeId", employeeId);
                sysLogger.error("SalaryManagement", "getEmployeeSalaryDetail", errorMsg, e);
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "员工ID格式无效");
            }
            
            // 查询员工信息
            Optional<Employee> employeeOpt = employeeRepository.findById(id);
            if (employeeOpt.isEmpty()) {
                String errorMsg = MessageConstructor.constructPlainMessage("员工不存在", "employeeId", id);
                sysLogger.warn("SalaryManagement", "getEmployeeSalaryDetail", errorMsg);
                return Result.failure(ErrorCode.NOT_FOUND, "未找到员工信息");
            }
            
            Employee employee = employeeOpt.get();
            
            // 查询员工当前有效的薪资档案
            Optional<EmployeeSalary> salaryOpt = employeeSalaryRepository.findByEmployeeAndIsActiveTrue(employee);
            if (salaryOpt.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "薪资档案不存在");
            }
            
            EmployeeSalary salary = salaryOpt.get();
            
            // 封装员工薪资档案详情
            Map<String, Object> detail = new HashMap<>();
            detail.put("id", salary.getId());
            detail.put("employeeId", employee.getEmployeeId());
            detail.put("name", employee.getFirstName() + " " + employee.getLastName());
            detail.put("department", employee.getDepartment().getName());
            detail.put("position", employee.getPosition().getTitle());
            detail.put("baseSalary", salary.getBaseSalary());
            detail.put("performanceBonus", salary.getPerformanceBonus());
            detail.put("allowance", salary.getAllowance());
            detail.put("socialInsurance", salary.getSocialInsurance());
            detail.put("effectiveDate", salary.getEffectiveDate());
            
            // 查询员工薪资变更历史
            List<SalaryChangeHistory> historyList = salaryChangeHistoryRepository.findByEmployeeOrderByChangeDateDesc(employee);
            List<Map<String, Object>> historyDtoList = new ArrayList<>();
            
            for (SalaryChangeHistory history : historyList) {
                Map<String, Object> historyDto = new HashMap<>();
                historyDto.put("date", history.getChangeDate());
                historyDto.put("field", history.getChangedField());
                historyDto.put("oldValue", history.getOldValue());
                historyDto.put("newValue", history.getNewValue());
                historyDto.put("reason", history.getReason());
                historyDto.put("operator", history.getOperator());
                historyDtoList.add(historyDto);
            }
            
            detail.put("history", historyDtoList);
            
            return Result.success(detail);
        } catch (Exception e) {
            String errorMsg = MessageConstructor.constructPlainMessage("查询失败", "error", e.getMessage());
            sysLogger.error("SalaryManagement", "getEmployeeSalaryDetail", errorMsg, e);
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR, "查询员工薪资详情失败");
        }
    }
    
    /**
     * 处理薪资历史查询请求
     * 当用户传入"history"参数时调用此方法
     */
    private Result<?> handleSalaryHistory() {
        try {
            // 创建分页请求，获取所有记录
            PageRequest pageRequest = PageRequest.of(0, 1000);
            
            // 获取所有薪资变更历史记录
            Page<SalaryChangeHistory> historyPage = salaryChangeHistoryRepository.findAllByOrderByChangeDateDesc(pageRequest);
            List<SalaryChangeHistory> allHistory = historyPage.getContent();
            
            // 如果没有记录，返回空列表
            if (allHistory.isEmpty()) {
                Map<String, Object> emptyResult = new HashMap<>();
                emptyResult.put("total", 0);
                emptyResult.put("history", new ArrayList<>());
                return Result.success(emptyResult);
            }
            
            // 转换为DTO
            List<Map<String, Object>> historyDtoList = new ArrayList<>();
            for (SalaryChangeHistory history : allHistory) {
                Map<String, Object> historyDto = new HashMap<>();
                historyDto.put("id", history.getId());
                historyDto.put("date", history.getChangeDate());
                historyDto.put("employeeId", history.getEmployee().getEmployeeId());
                historyDto.put("employeeName", history.getEmployee().getFirstName() + " " + history.getEmployee().getLastName());
                historyDto.put("department", history.getEmployee().getDepartment().getName());
                historyDto.put("field", history.getChangedField());
                historyDto.put("oldValue", history.getOldValue());
                historyDto.put("newValue", history.getNewValue());
                historyDto.put("reason", history.getReason());
                historyDto.put("operator", history.getOperator());
                historyDtoList.add(historyDto);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", historyPage.getTotalElements());  // 使用总记录数
            result.put("history", historyDtoList);
            
            return Result.success(result);
        } catch (Exception e) {
            sysLogger.error("SalaryManagement", "handleSalaryHistory", 
                    "查询薪资历史失败: " + e.getMessage(), e);
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        }
    }
    
    @Override
    @Transactional
    public Result<?> updateEmployeeSalary(SalaryUpdateRequest request) {
        try {
            // 记录日志
            sysLogger.info("SalaryManagement", "updateEmployeeSalary", 
                    MessageConstructor.constructPlainMessage("更新员工薪资", 
                            "employeeId", request.getEmployeeId(),
                            "name", request.getName(),
                            "changeReason", request.getChangeReason()));
            
            // 参数校验
            if (request.getEmployeeId() == null || request.getEmployeeId().isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "员工ID不能为空");
            }
            
            // 查询员工信息
            Optional<Employee> employeeOpt = employeeRepository.findById(Integer.parseInt(request.getEmployeeId()));
            if (employeeOpt.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "员工不存在");
            }
            
            Employee employee = employeeOpt.get();
            
            // 查询员工当前有效的薪资档案
            Optional<EmployeeSalary> salaryOpt = employeeSalaryRepository.findByEmployeeAndIsActiveTrue(employee);
            if (salaryOpt.isEmpty()) {
                // 如果不存在，创建新的薪资档案
                EmployeeSalary newSalary = new EmployeeSalary();
                newSalary.setEmployee(employee);
                newSalary.setBaseSalary(request.getBaseSalary());
                newSalary.setPerformanceBonus(request.getPerformanceBonus());
                newSalary.setAllowance(request.getAllowance());
                newSalary.setEffectiveDate(request.getEffectiveDate());
                newSalary.setActive(true);
                
                employeeSalaryRepository.save(newSalary);
                
                // 记录历史
                SalaryChangeHistory history = new SalaryChangeHistory();
                history.setEmployee(employee);
                history.setChangeDate(LocalDateTime.now());
                history.setChangedField("新建薪资档案");
                history.setOldValue("");
                history.setNewValue("基本工资: " + request.getBaseSalary() + ", 绩效奖金: " + request.getPerformanceBonus());
                history.setReason(request.getChangeReason());
                history.setOperator(AuthContext.getUserId());
                
                salaryChangeHistoryRepository.save(history);
            } else {
                // 更新现有薪资档案
                EmployeeSalary currentSalary = salaryOpt.get();
                
                // 记录变更历史
                List<SalaryChangeHistory> historyList = new ArrayList<>();
                
                // 检查基本工资是否变更
                if (request.getBaseSalary() != null && !request.getBaseSalary().equals(currentSalary.getBaseSalary())) {
                    SalaryChangeHistory history = new SalaryChangeHistory();
                    history.setEmployee(employee);
                    history.setChangeDate(LocalDateTime.now());
                    history.setChangedField("基本工资");
                    history.setOldValue(String.valueOf(currentSalary.getBaseSalary()));
                    history.setNewValue(String.valueOf(request.getBaseSalary()));
                    history.setReason(request.getChangeReason());
                    history.setOperator(AuthContext.getUserId());
                    historyList.add(history);
                    
                    currentSalary.setBaseSalary(request.getBaseSalary());
                }
                
                // 检查绩效奖金是否变更
                if (request.getPerformanceBonus() != null && !request.getPerformanceBonus().equals(currentSalary.getPerformanceBonus())) {
                    SalaryChangeHistory history = new SalaryChangeHistory();
                    history.setEmployee(employee);
                    history.setChangeDate(LocalDateTime.now());
                    history.setChangedField("绩效奖金");
                    history.setOldValue(String.valueOf(currentSalary.getPerformanceBonus()));
                    history.setNewValue(String.valueOf(request.getPerformanceBonus()));
                    history.setReason(request.getChangeReason());
                    history.setOperator(AuthContext.getUserId());
                    historyList.add(history);
                    
                    currentSalary.setPerformanceBonus(request.getPerformanceBonus());
                }
                
                // 检查津贴是否变更
                if (request.getAllowance() != null && !request.getAllowance().equals(currentSalary.getAllowance())) {
                    SalaryChangeHistory history = new SalaryChangeHistory();
                    history.setEmployee(employee);
                    history.setChangeDate(LocalDateTime.now());
                    history.setChangedField("津贴");
                    history.setOldValue(String.valueOf(currentSalary.getAllowance()));
                    history.setNewValue(String.valueOf(request.getAllowance()));
                    history.setReason(request.getChangeReason());
                    history.setOperator(AuthContext.getUserId());
                    historyList.add(history);
                    
                    currentSalary.setAllowance(request.getAllowance());
                }
                
                // 更新生效日期
                if (request.getEffectiveDate() != null) {
                    currentSalary.setEffectiveDate(request.getEffectiveDate());
                }
                
                // 保存更新后的薪资档案
                employeeSalaryRepository.save(currentSalary);
                
                // 保存变更历史
                if (!historyList.isEmpty()) {
                    salaryChangeHistoryRepository.saveAll(historyList);
                }
            }
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "薪资更新成功");
            
            return Result.success(result);
        } catch (Exception e) {
            sysLogger.error("SalaryManagement", "updateEmployeeSalary", 
                    "更新员工薪资失败: " + e.getMessage(), e);
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        }
    }
    
    @Override
    @Transactional
    public Result<?> calculateSalaries(SalaryCalculationRequest request) {
        try {
            // 记录日志
            sysLogger.info("SalaryManagement", "calculateSalaries", 
                    MessageConstructor.constructPlainMessage("薪资核算", 
                            "period", request.getPeriod(),
                            "department", request.getDepartment()));
            
            // 参数校验
            if (request.getPeriod() == null || request.getPeriod().isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "核算期间不能为空");
            }
            
            // 解析核算期间（格式：YYYY-MM）
            YearMonth yearMonth;
            try {
                yearMonth = YearMonth.parse(request.getPeriod());
            } catch (Exception e) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "核算期间格式错误，应为YYYY-MM格式");
            }
            
            // 查询需要核算的员工列表
            List<Employee> employees;
            if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
                // 按部门查询员工
                employees = employeeRepository.findByDepartmentName(request.getDepartment());
            } else {
                // 查询所有员工
                employees = employeeRepository.findAll();
            }
            
            if (employees.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "未找到需要核算的员工");
            }
            
            // 核算薪资并生成工资单
            List<Payslip> payslips = new ArrayList<>();
            double totalAmount = 0;
            
            for (Employee employee : employees) {
                // 查询员工当前有效的薪资档案
                Optional<EmployeeSalary> salaryOpt = employeeSalaryRepository.findByEmployeeAndIsActiveTrue(employee);
                if (salaryOpt.isEmpty()) {
                    continue; // 无薪资档案的员工跳过
                }
                
                EmployeeSalary salary = salaryOpt.get();
                
                // 检查是否已经存在该月工资单
                Payslip existingPayslip = payslipRepository.findByEmployeeAndMonth(employee, request.getPeriod());
                if (existingPayslip != null) {
                    // 已存在工资单，更新数据
                    updatePayslip(existingPayslip, salary);
                    payslips.add(existingPayslip);
                } else {
                    // 生成新工资单
                    Payslip payslip = createPayslip(employee, salary, request.getPeriod());
                    payslips.add(payslip);
                }
                
                // 累计总金额
                totalAmount += payslips.get(payslips.size() - 1).getTotalSalary();
            }
            
            // 保存工资单
            payslipRepository.saveAll(payslips);
            
            // 返回核算结果
            Map<String, Object> result = new HashMap<>();
            result.put("total", employees.size());
            result.put("processed", payslips.size());
            result.put("period", request.getPeriod());
            result.put("department", request.getDepartment() != null ? request.getDepartment() : "全公司");
            result.put("totalAmount", totalAmount);
            result.put("status", "completed");
            result.put("timestamp", LocalDateTime.now());
            
            return Result.success(result);
        } catch (Exception e) {
            sysLogger.error("SalaryManagement", "calculateSalaries", 
                    "薪资核算失败: " + e.getMessage(), e);
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        }
    }
    
    /**
     * 创建新工资单
     */
    private Payslip createPayslip(Employee employee, EmployeeSalary salary, String period) {
        Payslip payslip = new Payslip();
        payslip.setEmployee(employee);
        payslip.setMonth(period);
        
        // 设置薪资相关数据
        payslip.setBaseSalary(salary.getBaseSalary());
        payslip.setPerformanceSalary(salary.getPerformanceBonus());
        
        // 假设加班费为0，实际项目中可能从考勤系统获取
        payslip.setOvertimePay(0.0);
        
        // 津贴
        payslip.setOtherBonus(salary.getAllowance());
        
        // 计算总工资
        double totalSalary = salary.getBaseSalary() + salary.getPerformanceBonus() + 
                payslip.getOvertimePay() + salary.getAllowance();
        payslip.setTotalSalary(totalSalary);
        
        // 计算社保（假设为基本工资的10%）
        double socialInsurance = salary.getBaseSalary() * 0.1;
        payslip.setSocialInsurance(socialInsurance);
        
        // 计算住房公积金（假设为基本工资的7%）
        double housingFund = salary.getBaseSalary() * 0.07;
        payslip.setHousingFund(housingFund);
        
        // 计算个人所得税（简化计算，实际应根据税法）
        double taxableIncome = totalSalary - socialInsurance - housingFund - 5000; // 5000为起征点
        double incomeTax = taxableIncome > 0 ? calculateTax(taxableIncome) : 0;
        payslip.setIncomeTax(incomeTax);
        
        // 其他扣除
        payslip.setOtherDeduction(0.0);
        
        // 计算总扣除和实发工资
        double totalDeduction = socialInsurance + housingFund + incomeTax + payslip.getOtherDeduction();
        payslip.setTotalDeduction(totalDeduction);
        payslip.setActualSalary(totalSalary - totalDeduction);
        
        // 设置状态和时间
        payslip.setStatus("generated");
        payslip.setCreateTime(LocalDateTime.now());
        
        return payslip;
    }
    
    /**
     * 更新已有工资单
     */
    private void updatePayslip(Payslip payslip, EmployeeSalary salary) {
        // 更新基本数据
        payslip.setBaseSalary(salary.getBaseSalary());
        payslip.setPerformanceSalary(salary.getPerformanceBonus());
        payslip.setOtherBonus(salary.getAllowance());
        
        // 重新计算总工资
        double totalSalary = salary.getBaseSalary() + salary.getPerformanceBonus() + 
                payslip.getOvertimePay() + salary.getAllowance();
        payslip.setTotalSalary(totalSalary);
        
        // 重新计算社保和公积金
        double socialInsurance = salary.getBaseSalary() * 0.1;
        payslip.setSocialInsurance(socialInsurance);
        
        double housingFund = salary.getBaseSalary() * 0.07;
        payslip.setHousingFund(housingFund);
        
        // 重新计算个税
        double taxableIncome = totalSalary - socialInsurance - housingFund - 5000;
        double incomeTax = taxableIncome > 0 ? calculateTax(taxableIncome) : 0;
        payslip.setIncomeTax(incomeTax);
        
        // 重新计算总扣除和实发工资
        double totalDeduction = socialInsurance + housingFund + incomeTax + payslip.getOtherDeduction();
        payslip.setTotalDeduction(totalDeduction);
        payslip.setActualSalary(totalSalary - totalDeduction);
    }
    
    /**
     * 计算个人所得税（简化计算，实际应根据税法）
     */
    private double calculateTax(double taxableIncome) {
        // 简化计算，实际应按照累进税率计算
        if (taxableIncome <= 3000) {
            return taxableIncome * 0.03;
        } else if (taxableIncome <= 12000) {
            return taxableIncome * 0.1 - 210;
        } else if (taxableIncome <= 25000) {
            return taxableIncome * 0.2 - 1410;
        } else if (taxableIncome <= 35000) {
            return taxableIncome * 0.25 - 2660;
        } else if (taxableIncome <= 55000) {
            return taxableIncome * 0.3 - 4410;
        } else if (taxableIncome <= 80000) {
            return taxableIncome * 0.35 - 7160;
        } else {
            return taxableIncome * 0.45 - 15160;
        }
    }
    
    @Override
    public Result<?> getPayslips(PayslipRequest request) {
        try {
            // 状态值兼容处理
            if (request.getStatus() != null) {
                // 如果前端传入的是数字型的字符串，进行转换
                if ("0".equals(request.getStatus())) {
                    request.setStatus("generated");
                    log.debug("状态值兼容处理: 将状态值'0'转换为'generated'");
                } else if ("1".equals(request.getStatus())) {
                    request.setStatus("distributed");
                    log.debug("状态值兼容处理: 将状态值'1'转换为'distributed'");
                }
            }
            
            // 记录日志
            sysLogger.info("SalaryManagement", "getPayslips", 
                    MessageConstructor.constructPlainMessage("查询工资单", 
                            "month", String.valueOf(request.getMonth()),
                            "year", String.valueOf(request.getYear()),
                            "department", request.getDepartmentId(),
                            "status", String.valueOf(request.getStatus())));
            
            log.debug("查询工资单参数: year={}, month={}, departmentId={}, status={}", 
                    request.getYear(), request.getMonth(), request.getDepartmentId(), request.getStatus());
            
            // 构建分页请求
            PageRequest pageRequest = PageRequest.of(
                    request.getPage() != null ? request.getPage() : 0, 
                    request.getPageSize() != null ? request.getPageSize() : 10,
                    Sort.by(Sort.Direction.DESC, "createTime"));
            
            // 构建月份字符串
            String monthStr = null;
            if (request.getYear() != null && request.getMonth() != null) {
                monthStr = String.format("%d-%02d", request.getYear(), request.getMonth());
                log.debug("构建月份字符串: {}", monthStr);
            }
            
            Page<Payslip> payslipPage;
            
            try {
                // 根据条件查询
                if (request.getDepartmentId() != null && !request.getDepartmentId().isEmpty() 
                        && monthStr != null) {
                    // 部门和月份查询
                    log.debug("执行部门和月份查询: department={}, month={}", request.getDepartmentId(), monthStr);
                    payslipPage = payslipRepository.findByDepartmentAndMonth(
                            request.getDepartmentId(), monthStr, pageRequest);
                } else if (monthStr != null) {
                    // 月份查询
                    log.debug("执行月份查询: month={}", monthStr);
                    payslipPage = payslipRepository.findByMonthOrderByEmployee(monthStr, pageRequest);
                } else if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                    // 按状态查询
                    log.debug("执行状态查询: status={}", request.getStatus());
                    payslipPage = payslipRepository.findByStatusOrderByMonth(
                            request.getStatus(), pageRequest);
                } else {
                    // 查询所有
                    log.debug("查询所有工资单，无条件限制");
                    payslipPage = payslipRepository.findAll(pageRequest);
                }
                
                log.debug("查询结果: 总数={}, 当前页数量={}", 
                        payslipPage.getTotalElements(), payslipPage.getContent().size());
            } catch (Exception e) {
                log.error("查询工资单时发生异常", e);
                throw e;
            }
            
            // 转换为DTO
            List<Map<String, Object>> payslips = new ArrayList<>();
            for (Payslip payslip : payslipPage.getContent()) {
                Map<String, Object> payslipDto = new HashMap<>();
                payslipDto.put("id", payslip.getId());
                payslipDto.put("employeeId", payslip.getEmployee().getEmployeeId());
                payslipDto.put("name", payslip.getEmployee().getFirstName() + " " + payslip.getEmployee().getLastName());
                payslipDto.put("department", payslip.getEmployee().getDepartment().getName());
                payslipDto.put("position", payslip.getEmployee().getPosition().getTitle());
                payslipDto.put("month", payslip.getMonth());
                
                payslipDto.put("baseSalary", payslip.getBaseSalary());
                payslipDto.put("performanceSalary", payslip.getPerformanceSalary());
                payslipDto.put("overtimePay", payslip.getOvertimePay());
                payslipDto.put("otherBonus", payslip.getOtherBonus());
                payslipDto.put("totalSalary", payslip.getTotalSalary());
                
                payslipDto.put("socialInsurance", payslip.getSocialInsurance());
                payslipDto.put("housingFund", payslip.getHousingFund());
                payslipDto.put("incomeTax", payslip.getIncomeTax());
                payslipDto.put("otherDeduction", payslip.getOtherDeduction());
                payslipDto.put("totalDeduction", payslip.getTotalDeduction());
                payslipDto.put("actualSalary", payslip.getActualSalary());
                
                payslipDto.put("status", payslip.getStatus());
                payslipDto.put("createTime", payslip.getCreateTime());
                payslipDto.put("distributeTime", payslip.getDistributeTime());
                
                payslips.add(payslipDto);
            }
            
            // 如果没有查到记录，尝试执行直接查询，以帮助调试
            if (payslips.isEmpty()) {
                log.debug("未查询到记录，执行调试查询");
                
                // 检查数据库中是否有工资单记录
                long totalRecords = payslipRepository.count();
                log.debug("数据库中工资单总记录数: {}", totalRecords);
                
                if (totalRecords > 0) {
                    // 如果有记录但查询不到，可能是查询条件问题，尝试不带条件查询
                    List<Payslip> allPayslips = payslipRepository.findAll(PageRequest.of(0, 1)).getContent();
                    if (!allPayslips.isEmpty()) {
                        Payslip firstPayslip = allPayslips.get(0);
                        log.debug("示例工资单记录: id={}, month={}, status={}", 
                                firstPayslip.getId(), firstPayslip.getMonth(), firstPayslip.getStatus());
                    }
                }
            }
            
            // 构建结果
            Map<String, Object> result = new HashMap<>();
            result.put("payslips", payslips);
            result.put("total", payslipPage.getTotalElements());
            
            return Result.success(result);
        } catch (Exception e) {
            sysLogger.error("SalaryManagement", "getPayslips", 
                    "查询工资单失败: " + e.getMessage(), e);
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        }
    }
    
    @Override
    public Result<?> getPayslipDetail(String id) {
        try {
            // 记录日志
            sysLogger.info("SalaryManagement", "getPayslipDetail", 
                    MessageConstructor.constructPlainMessage("查询工资单详情", 
                            "id", id));
            
            // 参数校验
            if (id == null || id.isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "工资单ID不能为空");
            }
            
            // 查询工资单
            Optional<Payslip> payslipOpt = payslipRepository.findById(Long.parseLong(id));
            if (payslipOpt.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "工资单不存在");
            }
            
            Payslip payslip = payslipOpt.get();
            
            // 转换为DTO
            Map<String, Object> detail = new HashMap<>();
            detail.put("id", payslip.getId());
            detail.put("employeeId", payslip.getEmployee().getEmployeeId());
            detail.put("name", payslip.getEmployee().getFirstName() + " " + payslip.getEmployee().getLastName());
            detail.put("department", payslip.getEmployee().getDepartment().getName());
            detail.put("position", payslip.getEmployee().getPosition().getTitle());
            detail.put("month", payslip.getMonth());
            
            detail.put("baseSalary", payslip.getBaseSalary());
            detail.put("performanceSalary", payslip.getPerformanceSalary());
            detail.put("overtimePay", payslip.getOvertimePay());
            detail.put("otherBonus", payslip.getOtherBonus());
            detail.put("totalSalary", payslip.getTotalSalary());
            
            detail.put("socialInsurance", payslip.getSocialInsurance());
            detail.put("housingFund", payslip.getHousingFund());
            detail.put("incomeTax", payslip.getIncomeTax());
            detail.put("otherDeduction", payslip.getOtherDeduction());
            detail.put("totalDeduction", payslip.getTotalDeduction());
            detail.put("actualSalary", payslip.getActualSalary());
            
            detail.put("status", payslip.getStatus());
            detail.put("createTime", payslip.getCreateTime());
            detail.put("distributeTime", payslip.getDistributeTime());
            
            return Result.success(detail);
        } catch (Exception e) {
            sysLogger.error("SalaryManagement", "getPayslipDetail", 
                    "查询工资单详情失败: " + e.getMessage(), e);
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        }
    }
    
    @Override
    @Transactional
    public Result<?> distributePayslips(PayslipDistributeRequest request) {
        try {
            // 构建月份字符串和明确的日志参数
            String monthStr = (request.getMonthStr() != null) ? request.getMonthStr() : 
                              ((request.getYear() != null && request.getMonth() != null) ? 
                               request.getYear() + "-" + String.format("%02d", request.getMonth()) : null);
            
            String departmentName = request.getDepartmentName();
            
            // 记录日志
            String logMessage = MessageConstructor.constructPlainMessage("批量发放工资单", 
                    "month", monthStr,
                    "department", departmentName);
            sysLogger.info("SalaryManagement", "distributePayslips", logMessage);
            
            // 参数校验
            if (request.getYear() == null || request.getMonth() == null) {
                if (request.getMonthStr() == null || request.getMonthStr().isEmpty()) {
                    String errorMsg = MessageConstructor.constructPlainMessage("参数校验失败", 
                            "error", "发放年月不能为空",
                            "year", String.valueOf(request.getYear()),
                            "month", String.valueOf(request.getMonth()),
                            "monthStr", request.getMonthStr());
                    sysLogger.error("SalaryManagement", "distributePayslips", errorMsg, new IllegalArgumentException("发放年月不能为空"));
                    return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "发放年月不能为空");
                } else {
                    // 尝试解析monthStr
                    try {
                        String[] parts = request.getMonthStr().split("-");
                        if (parts.length == 2) {
                            request.setYear(Integer.parseInt(parts[0]));
                            request.setMonth(Integer.parseInt(parts[1]));
                            sysLogger.info("SalaryManagement", "distributePayslips", 
                                    MessageConstructor.constructPlainMessage("从monthStr解析得到年月", 
                                            "monthStr", request.getMonthStr(),
                                            "year", String.valueOf(request.getYear()),
                                            "month", String.valueOf(request.getMonth())));
                        } else {
                            String errorMsg = MessageConstructor.constructPlainMessage("monthStr格式不正确", 
                                    "monthStr", request.getMonthStr());
                            sysLogger.error("SalaryManagement", "distributePayslips", errorMsg, new IllegalArgumentException("月份格式不正确"));
                            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "月份格式不正确，应为YYYY-MM");
                        }
                    } catch (Exception e) {
                        String errorMsg = MessageConstructor.constructPlainMessage("解析monthStr失败", 
                                "monthStr", request.getMonthStr(),
                                "error", e.getMessage());
                        sysLogger.error("SalaryManagement", "distributePayslips", errorMsg, e);
                        return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "月份格式不正确，应为YYYY-MM");
                    }
                }
            }
            
            // 构建月份字符串 (YYYY-MM格式)
            monthStr = String.format("%d-%02d", request.getYear(), request.getMonth());
            sysLogger.info("SalaryManagement", "distributePayslips", 
                    MessageConstructor.constructPlainMessage("使用月份字符串", "monthStr", monthStr));
            
            // 查询所有部门，确认部门名称是否存在
            List<String> allDepartments = departmentRepository.findAll().stream()
                    .map(dept -> dept.getName())
                    .collect(Collectors.toList());
            
            if (departmentName != null && !departmentName.isEmpty() && !allDepartments.contains(departmentName)) {
                sysLogger.warn("SalaryManagement", "distributePayslips", 
                        MessageConstructor.constructPlainMessage("指定的部门在系统中不存在", "department", departmentName));
            }
            
            // 查询需要发放的工资单
            List<Payslip> payslips;
            
            try {
                sysLogger.info("SalaryManagement", "distributePayslips", 
                        MessageConstructor.constructPlainMessage("开始查询工资单", 
                                "month", monthStr, 
                                "department", departmentName));
                
                if (departmentName != null && !departmentName.isEmpty()) {
                    // 使用Repository已有的方法查询指定部门的工资单
                    payslips = payslipRepository.findByDepartmentAndMonthAndStatus(
                            departmentName, monthStr, "generated");
                    
                    sysLogger.info("SalaryManagement", "distributePayslips", 
                            MessageConstructor.constructPlainMessage("查询结果", 
                                    "department", departmentName,
                                    "count", String.valueOf(payslips.size())));
                } else {
                    // 查询所有未发放工资单
                    payslips = payslipRepository.findByMonthAndStatus(monthStr, "generated");
                    sysLogger.info("SalaryManagement", "distributePayslips", 
                            MessageConstructor.constructPlainMessage("查询结果", 
                                    "department", "全公司",
                                    "count", String.valueOf(payslips.size())));
                }
                
                // 检查查询结果
                if (payslips.isEmpty()) {
                    String warnMsg = MessageConstructor.constructPlainMessage("未找到需要发放的工资单", 
                            "month", monthStr, 
                            "department", departmentName != null ? departmentName : "全公司");
                    sysLogger.warn("SalaryManagement", "distributePayslips", warnMsg);
                    return Result.failure(ErrorCode.BUSINESS_ERROR, "未找到可发放的工资单记录");
                }
            } catch (Exception e) {
                String errorMsg = MessageConstructor.constructPlainMessage("查询工资单失败", "error", e.getMessage());
                sysLogger.error("SalaryManagement", "distributePayslips", errorMsg, e);
                return Result.failure(ErrorCode.BUSINESS_ERROR, "查询工资单失败: " + e.getMessage());
            }
            
            // 批量更新工资单状态
            try {
                int distributeCount = 0;
                List<Payslip> updatedPayslips = new ArrayList<>();
                
                for (Payslip payslip : payslips) {
                    // 检查状态
                    if (!"generated".equals(payslip.getStatus())) {
                        String warnMsg = MessageConstructor.constructPlainMessage("工资单状态不是'generated'，跳过处理",
                                "id", String.valueOf(payslip.getId()),
                                "status", payslip.getStatus());
                        sysLogger.warn("SalaryManagement", "distributePayslips", warnMsg);
                        continue;
                    }
                    
                    // 更新状态
                    payslip.setStatus("distributed");
                    payslip.setDistributeTime(LocalDateTime.now());
                    updatedPayslips.add(payslip);
                    distributeCount++;
                }
                
                // 批量保存
                if (!updatedPayslips.isEmpty()) {
                    sysLogger.info("SalaryManagement", "distributePayslips", 
                            MessageConstructor.constructPlainMessage("批量保存更新的工资单", 
                                    "count", String.valueOf(updatedPayslips.size())));
                    payslipRepository.saveAll(updatedPayslips);
                    
                    // 发送通知
                    for (Payslip payslip : updatedPayslips) {
                        // TODO: 发送通知实现
                    }
                    
                    String distributeTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    
                    // 记录成功日志
                    sysLogger.info("SalaryManagement", "distributePayslips", 
                            MessageConstructor.constructPlainMessage("批量发放工资单成功", 
                                    "month", monthStr, 
                                    "department", departmentName != null ? departmentName : "全公司", 
                                    "count", String.valueOf(distributeCount)));
                    
                    // 返回结果
                    Map<String, Object> result = new HashMap<>();
                    result.put("month", monthStr);
                    result.put("department", departmentName != null ? departmentName : "全公司");
                    result.put("distributeCount", distributeCount);
                    result.put("distributeTime", distributeTime);
                    return Result.success(result);
                } else {
                    String warnMsg = MessageConstructor.constructPlainMessage("没有工资单需要更新状态");
                    sysLogger.warn("SalaryManagement", "distributePayslips", warnMsg);
                    return Result.failure(ErrorCode.BUSINESS_ERROR, "没有找到可发放的工资单");
                }
            } catch (Exception e) {
                String errorMsg = MessageConstructor.constructPlainMessage("更新工资单状态失败", "error", e.getMessage());
                sysLogger.error("SalaryManagement", "distributePayslips", errorMsg, e);
                return Result.failure(ErrorCode.BUSINESS_ERROR, "发放工资单失败: " + e.getMessage());
            }
        } catch (Exception e) {
            String errorMsg = MessageConstructor.constructPlainMessage("批量发放工资单异常", "error", e.getMessage());
            sysLogger.error("SalaryManagement", "distributePayslips", errorMsg, e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "系统异常: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<?> distributeOnePayslip(PayslipDistributeOneRequest request) {
        try {
            // 记录日志
            String logMessage = MessageConstructor.constructPlainMessage("发放单个工资单", 
                    "id", request.getId(),
                    "month", request.getMonthStr() != null ? request.getMonthStr() : 
                           (request.getYear() != null && request.getMonth() != null ? 
                           request.getYear() + "-" + String.format("%02d", request.getMonth()) : null));
            sysLogger.info("SalaryManagement", "distributeOnePayslip", logMessage);
            
            // 参数校验
            if (request.getId() == null || request.getId().isEmpty()) {
                String errorMsg = MessageConstructor.constructPlainMessage("参数校验失败", "error", "工资单ID不能为空");
                sysLogger.error("SalaryManagement", "distributeOnePayslip", errorMsg, new IllegalArgumentException("工资单ID不能为空"));
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "工资单ID不能为空");
            }
            
            // 查询工资单
            Payslip payslip = null;
            try {
                Long payslipId = Long.parseLong(request.getId());
                payslip = payslipRepository.findById(payslipId).orElse(null);
            } catch (NumberFormatException e) {
                String errorMsg = MessageConstructor.constructPlainMessage("工资单ID格式错误", "id", request.getId());
                sysLogger.error("SalaryManagement", "distributeOnePayslip", errorMsg, e);
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "工资单ID格式不正确");
            }
            
            if (payslip == null) {
                String warnMsg = MessageConstructor.constructPlainMessage("工资单不存在", "id", request.getId());
                sysLogger.warn("SalaryManagement", "distributeOnePayslip", warnMsg);
                return Result.failure(ErrorCode.NOT_FOUND, "工资单不存在");
            }
            
            sysLogger.info("SalaryManagement", "distributeOnePayslip", 
                    MessageConstructor.constructPlainMessage("查询到工资单", 
                            "id", String.valueOf(payslip.getId()),
                            "employeeId", payslip.getEmployee() != null ? 
                                    String.valueOf(payslip.getEmployee().getEmployeeId()) : "未知",
                            "month", payslip.getMonth(),
                            "status", payslip.getStatus()));
            
            // 检查状态
            if (!"generated".equals(payslip.getStatus())) {
                if ("distributed".equals(payslip.getStatus())) {
                    sysLogger.info("SalaryManagement", "distributeOnePayslip", 
                            MessageConstructor.constructPlainMessage("工资单已经是发放状态", 
                                    "id", String.valueOf(payslip.getId()),
                                    "month", payslip.getMonth()));
                    
                    // 构建结果，返回工资单信息，但提示已经发放过
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", payslip.getId().toString());
                    result.put("month", payslip.getMonth());
                    result.put("employee", payslip.getEmployee() != null ? 
                            payslip.getEmployee().getEmployeeId() : null);
                    result.put("distributeTime", payslip.getDistributeTime() != null ? 
                            payslip.getDistributeTime().toString() : null);
                    result.put("alreadyDistributed", true);
                    result.put("message", "该工资单已发放，无需重复操作");
                    
                    return Result.success(result);
                } else {
                    String warnMsg = MessageConstructor.constructPlainMessage("工资单状态异常", "status", payslip.getStatus());
                    sysLogger.warn("SalaryManagement", "distributeOnePayslip", warnMsg);
                    return Result.failure(ErrorCode.BUSINESS_ERROR, "工资单状态异常，只有已生成的工资单可以发放");
                }
            }
            
            // 更新状态
            payslip.setStatus("distributed");
            payslip.setDistributeTime(LocalDateTime.now());
            
            try {
                payslipRepository.save(payslip);
                sysLogger.info("SalaryManagement", "distributeOnePayslip", 
                        MessageConstructor.constructPlainMessage("工资单状态更新成功", 
                                "id", String.valueOf(payslip.getId()),
                                "status", "distributed"));
            } catch (Exception e) {
                String errorMsg = MessageConstructor.constructPlainMessage("保存工资单失败", "error", e.getMessage());
                sysLogger.error("SalaryManagement", "distributeOnePayslip", errorMsg, e);
                return Result.failure(ErrorCode.BUSINESS_ERROR, "保存工资单失败: " + e.getMessage());
            }
            
            // 构建结果
            Map<String, Object> result = new HashMap<>();
            result.put("id", payslip.getId().toString());
            result.put("month", payslip.getMonth());
            result.put("employee", payslip.getEmployee() != null ? 
                    payslip.getEmployee().getEmployeeId() : null);
            result.put("distributeTime", payslip.getDistributeTime() != null ? 
                    payslip.getDistributeTime().toString() : null);
            
            return Result.success(result);
        } catch (Exception e) {
            String errorMsg = MessageConstructor.constructPlainMessage("发放单个工资单异常", "error", e.getMessage());
            sysLogger.error("SalaryManagement", "distributeOnePayslip", errorMsg, e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "系统异常: " + e.getMessage());
        }
    }
    
    @Override
    public Result<?> getSalaryCalculation(SalaryCalculationRequest request) {
        try {
            // 记录日志
            sysLogger.info("SalaryManagement", "getSalaryCalculation", 
                    MessageConstructor.constructPlainMessage("获取薪资计算数据", 
                            "period", request.getPeriod(),
                            "department", request.getDepartment()));
            
            // 参数校验
            if (request.getPeriod() == null || request.getPeriod().isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "核算期间不能为空");
            }
            
            // 分页参数处理
            int page = 0;
            int pageSize = 10;
            
            // 查询需要核算的员工列表
            List<Employee> employees;
            if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
                // 按部门查询员工
                employees = employeeRepository.findByDepartmentName(request.getDepartment());
            } else {
                // 查询所有员工
                employees = employeeRepository.findAll();
            }
            
            if (employees.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "未找到需要核算的员工");
            }
            
            // 查询工资单数据
            List<Map<String, Object>> records = new ArrayList<>();
            double totalSalary = 0;
            double totalTax = 0;
            double totalSocialSecurity = 0;
            double totalHousingFund = 0;
            
            for (Employee employee : employees) {
                // 查询员工当前有效的薪资档案
                Optional<EmployeeSalary> salaryOpt = employeeSalaryRepository.findByEmployeeAndIsActiveTrue(employee);
                if (salaryOpt.isEmpty()) {
                    continue; // 无薪资档案的员工跳过
                }
                
                EmployeeSalary salary = salaryOpt.get();
                
                // 查询该月工资单
                Payslip payslip = payslipRepository.findByEmployeeAndMonth(employee, request.getPeriod());
                
                Map<String, Object> record = new HashMap<>();
                record.put("id", employee.getEmployeeId());
                record.put("name", employee.getFirstName() + " " + employee.getLastName());
                record.put("department", employee.getDepartment() != null ? employee.getDepartment().getName() : "");
                record.put("position", employee.getPosition() != null ? employee.getPosition().getTitle() : "");
                record.put("baseSalary", salary.getBaseSalary());
                record.put("performanceBonus", salary.getPerformanceBonus());
                
                double overtimePay = 0;
                if (payslip != null) {
                    overtimePay = payslip.getOvertimePay();
                }
                record.put("overtimePay", overtimePay);
                record.put("allowance", salary.getAllowance());
                
                // 社保计算
                double socialInsuranceRate = 0.1; // 社保比例，实际应从配置获取
                double socialInsurance = salary.getBaseSalary() * socialInsuranceRate;
                Map<String, Object> socialSecurityMap = new HashMap<>();
                socialSecurityMap.put("pensionInsurance", socialInsurance * 0.8); // 假设养老金占社保的80%
                socialSecurityMap.put("medicalInsurance", socialInsurance * 0.15); // 假设医疗保险占社保的15%
                socialSecurityMap.put("unemploymentInsurance", socialInsurance * 0.03); // 假设失业保险占社保的3%
                socialSecurityMap.put("workInjuryInsurance", socialInsurance * 0.01); // 假设工伤保险占社保的1%
                socialSecurityMap.put("maternityInsurance", socialInsurance * 0.01); // 假设生育保险占社保的1%
                socialSecurityMap.put("total", socialInsurance);
                record.put("socialSecurity", socialSecurityMap);
                
                // 公积金计算
                double housingFundRate = 0.07; // 公积金比例，实际应从配置获取
                double housingFund = salary.getBaseSalary() * housingFundRate;
                record.put("housingFund", housingFund);
                
                // 计算总工资
                double totalSalaryAmount = salary.getBaseSalary() + salary.getPerformanceBonus() + 
                        overtimePay + salary.getAllowance();
                record.put("totalSalary", totalSalaryAmount);
                
                // 计算个税
                double taxableIncome = totalSalaryAmount - socialInsurance - housingFund - 5000; // 5000为起征点
                double tax = taxableIncome > 0 ? calculateTax(taxableIncome) : 0;
                record.put("tax", tax);
                
                // 实发工资
                double netSalary = totalSalaryAmount - socialInsurance - housingFund - tax;
                record.put("deductions", 0.0); // 其他扣除，默认为0
                record.put("netSalary", netSalary);
                record.put("status", payslip != null ? payslip.getStatus() : "待计算");
                record.put("remark", "");
                
                records.add(record);
                
                // 累计汇总数据
                totalSalary += totalSalaryAmount;
                totalTax += tax;
                totalSocialSecurity += socialInsurance;
                totalHousingFund += housingFund;
            }
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("records", records);
            result.put("total", employees.size());
            
            // 汇总数据
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalHeadcount", employees.size());
            summary.put("totalSalary", totalSalary);
            summary.put("averageSalary", employees.size() > 0 ? totalSalary / employees.size() : 0);
            summary.put("totalTax", totalTax);
            summary.put("totalSocialSecurity", totalSocialSecurity);
            summary.put("totalHousingFund", totalHousingFund);
            result.put("summary", summary);
            
            return Result.success(result);
        } catch (Exception e) {
            sysLogger.error("SalaryManagement", "getSalaryCalculation", 
                    "获取薪资计算数据失败: " + e.getMessage(), e);
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        }
    }
    
    @Override
    @Transactional
    public Result<?> confirmSalaryCalculation(SalaryCalculationRequest request) {
        try {
            // 记录日志
            sysLogger.info("SalaryManagement", "confirmSalaryCalculation", 
                    MessageConstructor.constructPlainMessage("确认薪资计算结果", 
                            "period", request.getPeriod(),
                            "department", request.getDepartment()));
            
            // 参数校验
            if (request.getPeriod() == null || request.getPeriod().isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "核算期间不能为空");
            }
            
            if (request.getDepartment() == null || request.getDepartment().isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "部门不能为空");
            }
            
            // 查询需要核算的员工列表
            List<Employee> employees = employeeRepository.findByDepartmentName(request.getDepartment());
            
            if (employees.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "未找到需要核算的员工");
            }
            
            // 更新工资单状态为已确认
            for (Employee employee : employees) {
                Payslip payslip = payslipRepository.findByEmployeeAndMonth(employee, request.getPeriod());
                if (payslip != null) {
                    payslip.setStatus("confirmed");
                    payslipRepository.save(payslip);
                }
            }
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("period", request.getPeriod());
            result.put("department", request.getDepartment());
            result.put("confirmTime", LocalDateTime.now());
            result.put("status", "confirmed");
            
            return Result.success(result);
        } catch (Exception e) {
            sysLogger.error("SalaryManagement", "confirmSalaryCalculation", 
                    "确认薪资计算结果失败: " + e.getMessage(), e);
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        }
    }
    
    @Override
    @Transactional
    public Result<?> adjustSalaryCalculation(SalaryAdjustRequest request) {
        try {
            // 记录日志
            sysLogger.info("SalaryManagement", "adjustSalaryCalculation", 
                    MessageConstructor.constructPlainMessage("调整薪资数据", 
                            "employeeId", request.getEmployeeId(),
                            "item", request.getItem(),
                            "value", String.valueOf(request.getValue())));
            
            // 参数校验
            if (request.getEmployeeId() == null || request.getEmployeeId().isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "员工ID不能为空");
            }
            
            if (request.getItem() == null || request.getItem().isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "调整项目不能为空");
            }
            
            if (request.getValue() == null) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "调整值不能为空");
            }
            
            if (request.getReason() == null || request.getReason().isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "调整原因不能为空");
            }
            
            // 查询员工信息
            Optional<Employee> employeeOpt;
            try {
                Integer employeeId = Integer.parseInt(request.getEmployeeId());
                employeeOpt = employeeRepository.findById(employeeId);
            } catch (NumberFormatException e) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "员工ID必须为数字");
            }
            
            if (employeeOpt.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "未找到员工信息");
            }
            
            Employee employee = employeeOpt.get();
            
            // 查询员工当前有效的薪资档案
            Optional<EmployeeSalary> salaryOpt = employeeSalaryRepository.findByEmployeeAndIsActiveTrue(employee);
            if (salaryOpt.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "未找到员工薪资档案");
            }
            
            EmployeeSalary salary = salaryOpt.get();
            
            // 根据调整项目进行调整
            switch (request.getItem()) {
                case "baseSalary":
                    // 记录变更历史
                    SalaryChangeHistory history = new SalaryChangeHistory();
                    history.setEmployee(employee);
                    history.setChangeDate(LocalDateTime.now());
                    history.setChangedField("基本工资");
                    history.setOldValue(String.valueOf(salary.getBaseSalary()));
                    history.setNewValue(String.valueOf(request.getValue()));
                    history.setReason(request.getReason());
                    history.setOperator(AuthContext.getUserId());
                    salaryChangeHistoryRepository.save(history);
                    
                    // 更新基本工资
                    salary.setBaseSalary(request.getValue());
                    break;
                case "performanceBonus":
                    // 记录变更历史
                    SalaryChangeHistory perfHistory = new SalaryChangeHistory();
                    perfHistory.setEmployee(employee);
                    perfHistory.setChangeDate(LocalDateTime.now());
                    perfHistory.setChangedField("绩效奖金");
                    perfHistory.setOldValue(String.valueOf(salary.getPerformanceBonus()));
                    perfHistory.setNewValue(String.valueOf(request.getValue()));
                    perfHistory.setReason(request.getReason());
                    perfHistory.setOperator(AuthContext.getUserId());
                    salaryChangeHistoryRepository.save(perfHistory);
                    
                    // 更新绩效奖金
                    salary.setPerformanceBonus(request.getValue());
                    break;
                case "allowance":
                    // 记录变更历史
                    SalaryChangeHistory allowHistory = new SalaryChangeHistory();
                    allowHistory.setEmployee(employee);
                    allowHistory.setChangeDate(LocalDateTime.now());
                    allowHistory.setChangedField("津贴");
                    allowHistory.setOldValue(String.valueOf(salary.getAllowance()));
                    allowHistory.setNewValue(String.valueOf(request.getValue()));
                    allowHistory.setReason(request.getReason());
                    allowHistory.setOperator(AuthContext.getUserId());
                    salaryChangeHistoryRepository.save(allowHistory);
                    
                    // 更新津贴
                    salary.setAllowance(request.getValue());
                    break;
                default:
                    return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "不支持的调整项目: " + request.getItem());
            }
            
            // 保存更新后的薪资档案
            employeeSalaryRepository.save(salary);
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("id", request.getEmployeeId());
            result.put("adjustTime", LocalDateTime.now());
            result.put("operator", AuthContext.getUserId());
            
            return Result.success(result);
        } catch (Exception e) {
            sysLogger.error("SalaryManagement", "adjustSalaryCalculation", 
                    "调整薪资数据失败: " + e.getMessage(), e);
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        }
    }
    
    @Override
    @Transactional
    public Result<?> generatePayslips(PayslipGenerateRequest request) {
        try {
            // 记录日志
            sysLogger.info("SalaryManagement", "generatePayslips", 
                    MessageConstructor.constructPlainMessage("生成工资单", 
                            "month", request.getMonth(),
                            "department", request.getDepartment()));
            
            // 参数校验
            if (request.getMonth() == null || request.getMonth().isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "月份不能为空");
            }
            
            if (request.getDepartment() == null || request.getDepartment().isEmpty()) {
                return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "部门不能为空");
            }
            
            // 查询部门
            List<Employee> employees = employeeRepository.findByDepartmentName(request.getDepartment());
            
            if (employees.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "未找到该部门的员工");
            }
            
            // 生成工资单
            int generatedCount = 0;
            LocalDateTime now = LocalDateTime.now();
            List<Payslip> payslipsToSave = new ArrayList<>();
            
            for (Employee employee : employees) {
                // 查询员工当前有效的薪资档案
                Optional<EmployeeSalary> salaryOpt = employeeSalaryRepository.findByEmployeeAndIsActiveTrue(employee);
                if (salaryOpt.isEmpty()) {
                    continue; // 无薪资档案的员工跳过
                }
                
                EmployeeSalary salary = salaryOpt.get();
                
                // 检查是否已经存在该月工资单
                Payslip existingPayslip = payslipRepository.findByEmployeeAndMonth(employee, request.getMonth());
                if (existingPayslip != null) {
                    // 已存在工资单，更新数据
                    updatePayslip(existingPayslip, salary);
                    existingPayslip.setStatus("generated"); // 确保状态设置为已生成
                    payslipsToSave.add(existingPayslip);
                    generatedCount++;
                } else {
                    // 生成新工资单
                    Payslip payslip = createPayslip(employee, salary, request.getMonth());
                    payslip.setStatus("generated"); // 设置状态为已生成
                    payslip.setCreateTime(now); // 设置创建时间
                    payslipsToSave.add(payslip);
                    generatedCount++;
                }
            }
            
            // 批量保存工资单
            if (!payslipsToSave.isEmpty()) {
                payslipRepository.saveAll(payslipsToSave);
            }
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("month", request.getMonth());
            result.put("department", request.getDepartment());
            result.put("count", generatedCount);
            result.put("generateTime", now);
            
            return Result.success(result);
        } catch (Exception e) {
            sysLogger.error("SalaryManagement", "generatePayslips", 
                    "生成工资单失败: " + e.getMessage(), e);
            return Result.failure(ErrorCode.BUSINESS_ERROR);
        }
    }
}