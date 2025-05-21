//package com.asaki0019.enterprisemanagementsb.config;
//
//import com.asaki0019.enterprisemanagementsb.entities.attendance.AttendanceRecord;
//import com.asaki0019.enterprisemanagementsb.entities.attendance.LeaveRecord;
//import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
//import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
//import com.asaki0019.enterprisemanagementsb.entities.employee.Position;
//import com.asaki0019.enterprisemanagementsb.entities.log.Log;
//import com.asaki0019.enterprisemanagementsb.entities.performance.PerformanceCriteria;
//import com.asaki0019.enterprisemanagementsb.entities.performance.PerformanceEvaluation;
//import com.asaki0019.enterprisemanagementsb.entities.performance.PerformanceLevel;
//import com.asaki0019.enterprisemanagementsb.entities.permission.Permission;
//import com.asaki0019.enterprisemanagementsb.entities.permission.Role;
//import com.asaki0019.enterprisemanagementsb.entities.permission.User;
//import com.asaki0019.enterprisemanagementsb.entities.salary.EmployeeSalary;
//import com.asaki0019.enterprisemanagementsb.entities.salary.Payslip;
//import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryAdjustRule;
//import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryChangeHistory;
//import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryGrade;
//import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryRecord;
//import com.asaki0019.enterprisemanagementsb.entities.salary.SalaryItem;
//import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
//import com.asaki0019.enterprisemanagementsb.enums.AttendanceStatus;
//import com.asaki0019.enterprisemanagementsb.enums.ItemType;
//import com.asaki0019.enterprisemanagementsb.enums.LeaveType;
//import com.asaki0019.enterprisemanagementsb.enums.PaymentStatus;
//import com.asaki0019.enterprisemanagementsb.repositories.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.YearMonth;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.Arrays;
//import java.util.ArrayList;
//
///**
// * 数据库初始化类
// * 用于在应用启动时插入基础数据
// */
//// @Configuration  // 注释掉配置注解以禁用这个初始化类
//@RequiredArgsConstructor
//@Slf4j
//public class DataInitializer {
//
//    private final DepartmentRepository departmentRepository;
//    private final PositionRepository positionRepository;
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final EmployeeRepository employeeRepository;
//    private final EmployeeSalaryRepository employeeSalaryRepository;
//
//    private final SalaryGradeRepository salaryGradeRepository;
//    private final SalaryAdjustRuleRepository salaryAdjustRuleRepository;
//    private final PerformanceEvaluationRepository performanceEvaluationRepository;
//    private final PerformanceCriteriaRepository performanceCriteriaRepository;
//    private final PerformanceLevelRepository performanceLevelRepository;
//    private final PayslipRepository payslipRepository;
//    private final SalaryChangeHistoryRepository salaryChangeHistoryRepository;
//    private final PermissionRepository permissionRepository;
//
//    // 以前被注释掉的Repository，现在启用它们
//    private final AttendanceRecordRepository attendanceRecordRepository;
//    private final LeaveRecordRepository leaveRecordRepository;
//    private final SalaryRecordRepository salaryRecordRepository;
//
//    @Bean
//    public CommandLineRunner initData() {
//        return args -> {
//            try {
//                initializeData();
//                log.info("=== 数据初始化完成 ===");
//            } catch (Exception e) {
//                log.error("数据初始化失败", e);
//            }
//        };
//    }
//
//    @Transactional
//    public void initializeData() {
//        // 检查是否已有数据
//        if (userRepository.count() > 0 || departmentRepository.count() > 0 || roleRepository.count() > 0) {
//            log.info("数据库中已存在数据，跳过初始化");
//            return;
//        }
//
//        // 1. 创建角色
//        log.info("初始化角色数据...");
//        // 添加SQL脚本中指定的权限
//        log.info("初始化权限数据...");
//        Permission userReadPermission = createPermission("user:read", "允许查看基础信息");
//        Permission employeeReadPermission = createPermission("employee:read", "允许员工权限读");
//        Permission employeeWritePermission = createPermission("employee:write", "允许员工权限写");
//        Permission adminAllPermission = createPermission("admin:all", "允许系统权限操作");
//
//        // 添加SQL脚本中指定的角色
//        Role userRole = createRole("USER", "默认角色", "基本用户");
//        Role employeeRole = createRole("EMPLOYEE", "员工", "普通员工角色");
//        Role adminRole = createRole("ADMIN", "管理员", "系统管理员角色");
//
//        // 设置角色-权限关联
//        // USER角色 - 只有基础查看权限
//        Set<Permission> userRolePermissions = new HashSet<>();
//        userRolePermissions.add(userReadPermission);
//        userRole.setPermissions(userRolePermissions);
//
//        // EMPLOYEE角色 - 基础查看+员工查看+员工写入权限
//        Set<Permission> employeeRolePermissions = new HashSet<>();
//        employeeRolePermissions.add(userReadPermission);
//        employeeRolePermissions.add(employeeReadPermission);
//        employeeRolePermissions.add(employeeWritePermission);
//        employeeRole.setPermissions(employeeRolePermissions);
//
//        // ADMIN角色 - 所有权限
//        Set<Permission> adminRolePermissions = new HashSet<>();
//        adminRolePermissions.add(userReadPermission);
//        adminRolePermissions.add(employeeReadPermission);
//        adminRolePermissions.add(employeeWritePermission);
//        adminRolePermissions.add(adminAllPermission);
//        adminRole.setPermissions(adminRolePermissions);
//
//        // 保存更新后的角色
//        roleRepository.save(userRole);
//        roleRepository.save(employeeRole);
//        roleRepository.save(adminRole);
//
//        // 2. 创建管理员用户
//        log.info("创建管理员用户...");
//        User adminUser = createUser("admin", "管理员", "admin123", adminRole);
//
//        // 3. 创建部门
//        log.info("初始化部门数据...");
//        Department hrDept = createDepartment("人力资源部");
//        Department itDept = createDepartment("信息技术部");
//        Department financeDept = createDepartment("财务部");
//        Department marketingDept = createDepartment("市场部");
//        Department salesDept = createDepartment("销售部");
//
//        // 4. 创建职位
//        log.info("初始化职位数据...");
//        Position hrManager = createPosition("人力资源经理", 15000.0, hrDept);
//        Position hrSpecialist = createPosition("人力资源专员", 8000.0, hrDept);
//
//        Position itManager = createPosition("IT经理", 18000.0, itDept);
//        Position seniorDeveloper = createPosition("高级开发工程师", 15000.0, itDept);
//        Position developer = createPosition("开发工程师", 10000.0, itDept);
//
//        Position financeManager = createPosition("财务经理", 16000.0, financeDept);
//        Position accountant = createPosition("会计", 9000.0, financeDept);
//
//        Position marketingManager = createPosition("市场经理", 14000.0, marketingDept);
//        Position marketingSpecialist = createPosition("市场专员", 8000.0, marketingDept);
//
//        Position salesManager = createPosition("销售经理", 12000.0, salesDept);
//        Position salesRepresentative = createPosition("销售代表", 6000.0, salesDept);
//
//        // 5. 创建员工
//        log.info("初始化员工数据...");
//        User hrManagerUser = createUser("hrmanager", "人力经理", "hr123", employeeRole);
//        Employee hrManagerEmp = createEmployee("张", "伟", "男", LocalDate.of(1985, 5, 15),
//                LocalDate.of(2018, 1, 10), "110101198505150011", hrDept, hrManager, hrManagerUser);
//
//        User itManagerUser = createUser("itmanager", "IT经理", "it123", employeeRole);
//        Employee itManagerEmp = createEmployee("李", "强", "男", LocalDate.of(1980, 7, 22),
//                LocalDate.of(2017, 3, 15), "110101198007220033", itDept, itManager, itManagerUser);
//
//        User devUser = createUser("developer", "开发者", "dev123", employeeRole);
//        Employee developer1Emp = createEmployee("王", "芳", "女", LocalDate.of(1990, 3, 8),
//                LocalDate.of(2019, 6, 1), "110101199003080022", itDept, developer, devUser);
//
//        User financeUser = createUser("finance", "财务", "fin123", employeeRole);
//        Employee financeEmp = createEmployee("赵", "敏", "女", LocalDate.of(1988, 11, 12),
//                LocalDate.of(2018, 9, 1), "110101198811120044", financeDept, accountant, financeUser);
//
//        // 6. 创建薪资档案
//        log.info("初始化薪资档案...");
//        EmployeeSalary hrSalary = createEmployeeSalary(hrManagerEmp, 15000.0, 3000.0, 1500.0, 0.0,
//                LocalDate.of(2022, 1, 1), true);
//
//        EmployeeSalary itSalary = createEmployeeSalary(itManagerEmp, 18000.0, 4000.0, 2000.0, 0.0,
//                LocalDate.of(2022, 1, 1), true);
//
//        EmployeeSalary devSalary = createEmployeeSalary(developer1Emp, 10000.0, 2000.0, 1000.0, 0.0,
//                LocalDate.of(2022, 1, 1), true);
//
//        EmployeeSalary finSalary = createEmployeeSalary(financeEmp, 9000.0, 1500.0, 800.0, 0.0,
//                LocalDate.of(2022, 1, 1), true);
//
//        // 7. 创建薪资等级
//        log.info("初始化薪资等级...");
//        createSalaryGrade("G001", "P1", 1, 8000.0, 12000.0, "技术部", "初级工程师薪资等级");
//        createSalaryGrade("G002", "P2", 2, 12000.0, 18000.0, "技术部", "中级工程师薪资等级");
//        createSalaryGrade("G003", "P3", 3, 18000.0, 25000.0, "技术部", "高级工程师薪资等级");
//
//        createSalaryGrade("G004", "M1", 1, 10000.0, 15000.0, "销售部", "初级销售薪资等级");
//        createSalaryGrade("G005", "M2", 2, 15000.0, 20000.0, "销售部", "中级销售薪资等级");
//        createSalaryGrade("G006", "M3", 3, 20000.0, 30000.0, "销售部", "高级销售薪资等级");
//
//        // 8. 创建薪资调整规则
//        log.info("初始化薪资调整规则...");
//        createSalaryAdjustRule("AR001", "年度绩效调薪", "绩效调薪", "全员",
//                "[{\"level\":\"A\",\"ratio\":0.1},{\"level\":\"B\",\"ratio\":0.05},{\"level\":\"C\",\"ratio\":0.02},{\"level\":\"D\",\"ratio\":0}]",
//                "range", LocalDate.of(2023, 4, 1), "根据年度绩效考核结果调整基本工资");
//
//        createSalaryAdjustRule("AR002", "职级晋升调薪", "晋升调薪", "全员",
//                "0.15", "fixed", LocalDate.of(2023, 1, 1), "员工职级晋升后调整基本工资");
//
//        // 9. 创建绩效等级
//        log.info("初始化绩效等级...");
//        createPerformanceLevel("A", "卓越", 90, 100, 0.2, "表现卓越，超出期望");
//        createPerformanceLevel("B", "优秀", 80, 89, 0.1, "表现优秀，达到期望");
//        createPerformanceLevel("C", "良好", 70, 79, 0.05, "表现良好，基本达到期望");
//        createPerformanceLevel("D", "需改进", 60, 69, 0.0, "表现一般，需要改进");
//        createPerformanceLevel("E", "不合格", 0, 59, -0.1, "表现不合格，未达到基本期望");
//
//        // 10. 创建绩效评估标准
//        log.info("初始化绩效评估标准...");
//        createPerformanceCriteria("工作表现", 0.4, null, null,
//                "[{\"name\":\"工作质量\",\"weight\":0.3,\"description\":\"工作成果的质量、准确性和完整性\"},{\"name\":\"工作效率\",\"weight\":0.3,\"description\":\"任务完成的速度和资源利用效率\"},{\"name\":\"专业能力\",\"weight\":0.4,\"description\":\"专业知识的应用和解决问题的能力\"}]",
//                "评估员工工作表现的标准");
//
//        createPerformanceCriteria("目标达成", 0.3, null, null,
//                "[{\"name\":\"季度KPI\",\"weight\":0.6,\"description\":\"季度KPI的完成情况\"},{\"name\":\"项目完成情况\",\"weight\":0.4,\"description\":\"负责项目的进度、质量和成果\"}]",
//                "评估员工目标达成的标准");
//
//        createPerformanceCriteria("团队协作", 0.2, null, null,
//                "[{\"name\":\"沟通能力\",\"weight\":0.5,\"description\":\"沟通顺畅，及时反馈\"},{\"name\":\"协作意识\",\"weight\":0.5,\"description\":\"积极配合团队工作\"}]",
//                "评估员工团队协作的标准");
//
//        createPerformanceCriteria("创新与成长", 0.1, null, null,
//                "[{\"name\":\"学习能力\",\"weight\":0.4,\"description\":\"积极学习新技术\"},{\"name\":\"创新思维\",\"weight\":0.6,\"description\":\"提出多项改进建议\"}]",
//                "评估员工创新与成长的标准");
//
//        createPerformanceCriteria("技术创新", 0.15, "技术部", null,
//                "[{\"name\":\"技术方案创新\",\"weight\":0.6,\"description\":\"提出创新的技术方案和解决思路\"},{\"name\":\"技术分享\",\"weight\":0.4,\"description\":\"积极参与技术分享和知识传播\"}]",
//                "技术部门特有的评估标准");
//
//        createPerformanceCriteria("销售业绩", 0.2, "销售部", null,
//                "[{\"name\":\"销售额达成率\",\"weight\":0.7,\"description\":\"销售目标的实际达成比例\"},{\"name\":\"新客户开发\",\"weight\":0.3,\"description\":\"新客户的开发数量和质量\"}]",
//                "销售部门特有的评估标准");
//
//        // 11. 创建员工绩效评估
//        log.info("初始化员工绩效评估...");
//        String evaluationDetails = "{\"categories\":[{\"name\":\"工作表现\",\"weight\":0.4,\"items\":[{\"name\":\"工作质量\",\"score\":90,\"weight\":0.3,\"comment\":\"代码质量高，测试覆盖率达标\"},{\"name\":\"工作效率\",\"score\":85,\"weight\":0.3,\"comment\":\"任务完成及时，效率较高\"},{\"name\":\"专业能力\",\"score\":88,\"weight\":0.4,\"comment\":\"技术能力强，解决问题能力突出\"}],\"categoryScore\":87.7},{\"name\":\"目标达成\",\"weight\":0.3,\"items\":[{\"name\":\"季度KPI\",\"score\":85,\"weight\":0.6,\"comment\":\"主要KPI均已达成\"},{\"name\":\"项目完成情况\",\"score\":80,\"weight\":0.4,\"comment\":\"项目按时交付，质量良好\"}],\"categoryScore\":83},{\"name\":\"团队协作\",\"weight\":0.2,\"items\":[{\"name\":\"沟通能力\",\"score\":82,\"weight\":0.5,\"comment\":\"沟通顺畅，及时反馈\"},{\"name\":\"协作意识\",\"score\":85,\"weight\":0.5,\"comment\":\"积极配合团队工作\"}],\"categoryScore\":83.5},{\"name\":\"创新与成长\",\"weight\":0.1,\"items\":[{\"name\":\"学习能力\",\"score\":90,\"weight\":0.4,\"comment\":\"积极学习新技术\"},{\"name\":\"创新思维\",\"score\":85,\"weight\":0.6,\"comment\":\"提出多项改进建议\"}],\"categoryScore\":87}]}";
//        String nextGoals = "[\"提高代码覆盖率至95%\",\"推动团队技术分享活动\",\"完成新系统架构设计\"]";
//
//        createPerformanceEvaluation("PE000123", itManagerEmp, "2023", "Q2", "2023年第二季度",
//                85.0, "A", "已确认", "李总", LocalDate.of(2023, 7, 5), LocalDate.of(2023, 7, 10),
//                "总体表现优秀，建议继续保持", "感谢领导的肯定，会继续努力", "建议进一步加强项目管理能力",
//                nextGoals, evaluationDetails);
//
//        createPerformanceEvaluation("PE000124", developer1Emp, "2023", "Q2", "2023年第二季度",
//                90.0, "A", "已确认", "李强", LocalDate.of(2023, 7, 6), LocalDate.of(2023, 7, 11),
//                "表现非常出色，特别是在技术创新方面", "感谢领导认可，会继续努力", "建议参与更多团队协作项目",
//                "[\"学习新的框架技术\",\"参与开源项目\",\"提升团队协作能力\"]", evaluationDetails);
//
//        // 12. 创建薪资变更历史
//        log.info("初始化薪资变更历史...");
//        createSalaryChangeHistory(hrManagerEmp, "基本工资", "13000", "15000", "年度调薪", "admin", LocalDateTime.of(2022, 1, 1, 9, 0));
//        createSalaryChangeHistory(itManagerEmp, "绩效奖金", "3000", "4000", "绩效提升", "admin", LocalDateTime.of(2022, 3, 15, 10, 30));
//
//        // 13. 创建工资单
//        log.info("初始化工资单数据...");
//        // 创建2023年6月的工资单
//        String june2023 = "2023-06";
//        String july2023 = "2023-07";
//        String august2023 = "2023-08";
//
//        // HR经理的6月工资单
//        createPayslip(hrManagerEmp, hrSalary, june2023, "distributed",
//                LocalDateTime.of(2023, 6, 28, 15, 0),
//                LocalDateTime.of(2023, 7, 5, 10, 0));
//
//        // HR经理的7月工资单
//        createPayslip(hrManagerEmp, hrSalary, july2023, "generated",
//                LocalDateTime.of(2023, 7, 28, 15, 0), null);
//
//        // 创建8月工资单（添加新的月份）
//        createPayslip(hrManagerEmp, hrSalary, august2023, "generated",
//                LocalDateTime.of(2023, 8, 28, 15, 0), null);
//
//        // IT经理的6月工资单
//        createPayslip(itManagerEmp, itSalary, june2023, "distributed",
//                LocalDateTime.of(2023, 6, 28, 15, 0),
//                LocalDateTime.of(2023, 7, 5, 10, 0));
//
//        // IT经理的7月工资单
//        createPayslip(itManagerEmp, itSalary, july2023, "generated",
//                LocalDateTime.of(2023, 7, 28, 15, 0), null);
//
//        // IT经理的8月工资单（添加新的）
//        createPayslip(itManagerEmp, itSalary, august2023, "generated",
//                LocalDateTime.of(2023, 8, 28, 15, 0), null);
//
//        // 开发人员的6月工资单
//        createPayslip(developer1Emp, devSalary, june2023, "distributed",
//                LocalDateTime.of(2023, 6, 28, 15, 0),
//                LocalDateTime.of(2023, 7, 5, 10, 0));
//
//        // 开发人员的7月工资单
//        createPayslip(developer1Emp, devSalary, july2023, "generated",
//                LocalDateTime.of(2023, 7, 28, 15, 0), null);
//
//        // 开发人员的8月工资单（添加新的）
//        createPayslip(developer1Emp, devSalary, august2023, "generated",
//                LocalDateTime.of(2023, 8, 28, 15, 0), null);
//
//        // 财务人员的6月工资单
//        createPayslip(financeEmp, finSalary, june2023, "distributed",
//                LocalDateTime.of(2023, 6, 28, 15, 0),
//                LocalDateTime.of(2023, 7, 5, 10, 0));
//
//        // 财务人员的7月工资单
//        createPayslip(financeEmp, finSalary, july2023, "generated",
//                LocalDateTime.of(2023, 7, 28, 15, 0), null);
//
//        // 财务人员的8月工资单（添加新的）
//        createPayslip(financeEmp, finSalary, august2023, "generated",
//                LocalDateTime.of(2023, 8, 28, 15, 0), null);
//
//        // 17. 创建薪资记录（新增）
//        log.info("初始化薪资记录数据...");
//
//        // 创建薪资记录项目
//        List<SalaryItem> hrItems = new ArrayList<>();
//        hrItems.add(createSalaryItem("基本工资", ItemType.EARNING, 15000.0));
//        hrItems.add(createSalaryItem("绩效奖金", ItemType.EARNING, 3000.0));
//        hrItems.add(createSalaryItem("津贴", ItemType.EARNING, 1500.0));
//        hrItems.add(createSalaryItem("社会保险", ItemType.DEDUCTION, 1500.0));
//        hrItems.add(createSalaryItem("个人所得税", ItemType.DEDUCTION, 1800.0));
//
//        List<SalaryItem> itItems = new ArrayList<>();
//        itItems.add(createSalaryItem("基本工资", ItemType.EARNING, 18000.0));
//        itItems.add(createSalaryItem("绩效奖金", ItemType.EARNING, 4000.0));
//        itItems.add(createSalaryItem("津贴", ItemType.EARNING, 2000.0));
//        itItems.add(createSalaryItem("社会保险", ItemType.DEDUCTION, 1800.0));
//        itItems.add(createSalaryItem("个人所得税", ItemType.DEDUCTION, 2200.0));
//
//        List<SalaryItem> devItems = new ArrayList<>();
//        devItems.add(createSalaryItem("基本工资", ItemType.EARNING, 10000.0));
//        devItems.add(createSalaryItem("绩效奖金", ItemType.EARNING, 2000.0));
//        devItems.add(createSalaryItem("津贴", ItemType.EARNING, 1000.0));
//        devItems.add(createSalaryItem("社会保险", ItemType.DEDUCTION, 1000.0));
//        devItems.add(createSalaryItem("个人所得税", ItemType.DEDUCTION, 1200.0));
//
//        List<SalaryItem> finItems = new ArrayList<>();
//        finItems.add(createSalaryItem("基本工资", ItemType.EARNING, 9000.0));
//        finItems.add(createSalaryItem("绩效奖金", ItemType.EARNING, 1500.0));
//        finItems.add(createSalaryItem("津贴", ItemType.EARNING, 800.0));
//        finItems.add(createSalaryItem("社会保险", ItemType.DEDUCTION, 900.0));
//        finItems.add(createSalaryItem("个人所得税", ItemType.DEDUCTION, 1000.0));
//
//        // 创建薪资记录
//        createSalaryRecord(hrManagerEmp, june2023, 19500.0, 3300.0, 16200.0, PaymentStatus.PAID, hrItems);
//        createSalaryRecord(hrManagerEmp, july2023, 19500.0, 3300.0, 16200.0, PaymentStatus.PENDING, hrItems);
//        createSalaryRecord(hrManagerEmp, august2023, 19500.0, 3300.0, 16200.0, PaymentStatus.PENDING, hrItems);
//
//        createSalaryRecord(itManagerEmp, june2023, 24000.0, 4000.0, 20000.0, PaymentStatus.PAID, itItems);
//        createSalaryRecord(itManagerEmp, july2023, 24000.0, 4000.0, 20000.0, PaymentStatus.PENDING, itItems);
//        createSalaryRecord(itManagerEmp, august2023, 24000.0, 4000.0, 20000.0, PaymentStatus.PENDING, itItems);
//
//        createSalaryRecord(developer1Emp, june2023, 13000.0, 2200.0, 10800.0, PaymentStatus.PAID, devItems);
//        createSalaryRecord(developer1Emp, july2023, 13000.0, 2200.0, 10800.0, PaymentStatus.PENDING, devItems);
//        createSalaryRecord(developer1Emp, august2023, 13000.0, 2200.0, 10800.0, PaymentStatus.PENDING, devItems);
//
//        createSalaryRecord(financeEmp, june2023, 11300.0, 1900.0, 9400.0, PaymentStatus.PAID, finItems);
//        createSalaryRecord(financeEmp, july2023, 11300.0, 1900.0, 9400.0, PaymentStatus.PENDING, finItems);
//        createSalaryRecord(financeEmp, august2023, 11300.0, 1900.0, 9400.0, PaymentStatus.PENDING, finItems);
//
//        // 15. 创建考勤记录（更新此部分）
//        log.info("初始化考勤记录数据...");
//        LocalDate today = LocalDate.now();
//
//        // 当月上半月数据
//        for (int i = 1; i <= 15; i++) {
//            LocalDate date = today.withDayOfMonth(i);
//            // 跳过周末
//            if (date.getDayOfWeek().getValue() >= 6) {
//                continue;
//            }
//
//            // HR经理考勤
//            createAttendanceRecord(hrManagerEmp, date, LocalTime.of(8, 30), LocalTime.of(18, 0), AttendanceStatus.NORMAL);
//
//            // IT经理考勤
//            if (i == 3) {
//                createAttendanceRecord(itManagerEmp, date, LocalTime.of(9, 15), LocalTime.of(18, 0), AttendanceStatus.LATE);
//            } else if (i == 7) {
//                createAttendanceRecord(itManagerEmp, date, LocalTime.of(8, 45), LocalTime.of(17, 30), AttendanceStatus.EARLY_LEAVE);
//            } else {
//                createAttendanceRecord(itManagerEmp, date, LocalTime.of(8, 45), LocalTime.of(18, 15), AttendanceStatus.NORMAL);
//            }
//
//            // 开发人员考勤
//            if (i == 2) {
//                createAttendanceRecord(developer1Emp, date, null, null, AttendanceStatus.ABSENT);
//            } else if (i == 5) {
//                createAttendanceRecord(developer1Emp, date, LocalTime.of(9, 20), LocalTime.of(18, 10), AttendanceStatus.LATE);
//            } else if (i == 10) {
//                createAttendanceRecord(developer1Emp, date, LocalTime.of(8, 50), LocalTime.of(17, 30), AttendanceStatus.EARLY_LEAVE);
//            } else {
//                createAttendanceRecord(developer1Emp, date, LocalTime.of(8, 50), LocalTime.of(18, 10), AttendanceStatus.NORMAL);
//            }
//
//            // 财务人员考勤
//            if (i == 8) {
//                createAttendanceRecord(financeEmp, date, LocalTime.of(9, 20), LocalTime.of(18, 0), AttendanceStatus.LATE);
//            } else {
//                createAttendanceRecord(financeEmp, date, LocalTime.of(8, 45), LocalTime.of(18, 0), AttendanceStatus.NORMAL);
//            }
//        }
//
//        // 16. 创建请假记录（更新此部分）
//        log.info("初始化请假记录数据...");
//        // 当月请假记录
//        createLeaveRecord(developer1Emp, LeaveType.ANNUAL_LEAVE, today.plusDays(5), today.plusDays(7), ApprovalStatus.PENDING, itManagerEmp);
//        createLeaveRecord(financeEmp, LeaveType.SICK_LEAVE, today.minusDays(10), today.minusDays(8), ApprovalStatus.APPROVED, hrManagerEmp);
//        createLeaveRecord(hrManagerEmp, LeaveType.PERSONAL_LEAVE, today.plusDays(15), today.plusDays(15), ApprovalStatus.PENDING, null);
//        createLeaveRecord(itManagerEmp, LeaveType.ANNUAL_LEAVE, today.minusDays(20), today.minusDays(15), ApprovalStatus.APPROVED, null);
//
//        // 历史请假记录
//        createLeaveRecord(developer1Emp, LeaveType.SICK_LEAVE, LocalDate.of(2023, 6, 5), LocalDate.of(2023, 6, 6), ApprovalStatus.APPROVED, itManagerEmp);
//        createLeaveRecord(financeEmp, LeaveType.ANNUAL_LEAVE, LocalDate.of(2023, 5, 15), LocalDate.of(2023, 5, 19), ApprovalStatus.APPROVED, hrManagerEmp);
//        createLeaveRecord(hrManagerEmp, LeaveType.COMPENSATORY_LEAVE, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 1), ApprovalStatus.APPROVED, null);
//        createLeaveRecord(itManagerEmp, LeaveType.MARRIAGE_LEAVE, LocalDate.of(2023, 4, 10), LocalDate.of(2023, 4, 17), ApprovalStatus.APPROVED, null);
//
//        // 未来请假记录
//        createLeaveRecord(developer1Emp, LeaveType.ANNUAL_LEAVE, LocalDate.of(2023, 9, 10), LocalDate.of(2023, 9, 15), ApprovalStatus.PENDING, itManagerEmp);
//        createLeaveRecord(financeEmp, LeaveType.PERSONAL_LEAVE, LocalDate.of(2023, 9, 20), LocalDate.of(2023, 9, 20), ApprovalStatus.PENDING, hrManagerEmp);
//    }
//
//    private Role createRole(String roleName, String displayName, String description) {
//        try {
//            Role role = new Role();
//            role.setRoleName(roleName);
//            role.setDisplayName(displayName);
//            role.setDescription(description);
//            return roleRepository.save(role);
//        } catch (Exception e) {
//            log.error("创建角色失败: " + roleName, e);
//            throw e;
//        }
//    }
//
//    private User createUser(String username, String nickname, String password, Role... roles) {
//        try {
//            User user = new User();
//            user.setUsername(username);
//            user.setNickname(nickname);
//            user.setPassword(password);
//            user.setEnabled(true);
//
//            // 设置唯一邮箱，确保不会有重复
//            user.setEmail(username + "@example.com");
//
//            Set<Role> roleSet = new HashSet<>();
//            for (Role role : roles) {
//                roleSet.add(role);
//            }
//            user.setRoles(roleSet);
//
//            return userRepository.save(user);
//        } catch (Exception e) {
//            log.error("创建用户失败: " + username, e);
//            throw e;
//        }
//    }
//
//    private Department createDepartment(String name) {
//        try {
//            Department department = new Department();
//            department.setName(name);
//            department.setDescription("部门：" + name);
//            return departmentRepository.save(department);
//        } catch (Exception e) {
//            log.error("创建部门失败: " + name, e);
//            throw e;
//        }
//    }
//
//    private Position createPosition(String title, Double baseSalary, Department department) {
//        try {
//            Position position = new Position();
//            position.setTitle(title);
//            position.setBaseSalary(baseSalary);
//            position.setDepartment(department);
//            return positionRepository.save(position);
//        } catch (Exception e) {
//            log.error("创建职位失败: " + title, e);
//            throw e;
//        }
//    }
//
//    private Employee createEmployee(String firstName, String lastName, String gender,
//                                   LocalDate birthDate, LocalDate hireDate, String idNumber,
//                                   Department department, Position position, User user) {
//        try {
//            Employee employee = new Employee();
//            employee.setFirstName(firstName);
//            employee.setLastName(lastName);
//            employee.setGender(gender);
//            employee.setBirthDate(birthDate);
//            employee.setHireDate(hireDate);
//            employee.setIdNumber(idNumber);
//            employee.setDepartment(department);
//            employee.setPosition(position);
//            employee.setUser(user);
//            return employeeRepository.save(employee);
//        } catch (Exception e) {
//            log.error("创建员工失败: " + firstName + " " + lastName, e);
//            throw e;
//        }
//    }
//
//    private EmployeeSalary createEmployeeSalary(Employee employee, Double baseSalary,
//                                              Double performanceBonus, Double allowance,
//                                              Double socialInsurance, LocalDate effectiveDate,
//                                              Boolean isActive) {
//        try {
//            EmployeeSalary salary = new EmployeeSalary();
//            salary.setEmployee(employee);
//            salary.setBaseSalary(baseSalary);
//            salary.setPerformanceBonus(performanceBonus);
//            salary.setAllowance(allowance);
//            salary.setSocialInsurance(socialInsurance);
//            salary.setEffectiveDate(effectiveDate);
//            salary.setActive(isActive);
//            return employeeSalaryRepository.save(salary);
//        } catch (Exception e) {
//            log.error("创建薪资档案失败: " + employee.getFirstName() + " " + employee.getLastName(), e);
//            throw e;
//        }
//    }
//
//    private SalaryGrade createSalaryGrade(String gradeCode, String gradeName, Integer level,
//                                        Double minSalary, Double maxSalary, String department,
//                                        String description) {
//        try {
//            SalaryGrade grade = new SalaryGrade();
//            grade.setGradeCode(gradeCode);
//            grade.setGradeName(gradeName);
//            grade.setLevel(level);
//            grade.setMinSalary(minSalary);
//            grade.setMaxSalary(maxSalary);
//            grade.setDepartment(department);
//            grade.setDescription(description);
//            return salaryGradeRepository.save(grade);
//        } catch (Exception e) {
//            log.error("创建薪资等级失败: " + gradeName, e);
//            throw e;
//        }
//    }
//
//    private SalaryAdjustRule createSalaryAdjustRule(String ruleCode, String ruleName, String ruleType,
//                                                 String applyTo, String adjustRatio, String ratioType,
//                                                 LocalDate effectiveDate, String description) {
//        try {
//            SalaryAdjustRule rule = new SalaryAdjustRule();
//            rule.setRuleCode(ruleCode);
//            rule.setRuleName(ruleName);
//            rule.setRuleType(ruleType);
//            rule.setApplyTo(applyTo);
//            rule.setAdjustRatio(adjustRatio);
//            rule.setRatioType(ratioType);
//            rule.setEffectiveDate(effectiveDate);
//            rule.setDescription(description);
//            return salaryAdjustRuleRepository.save(rule);
//        } catch (Exception e) {
//            log.error("创建薪资调整规则失败: " + ruleName, e);
//            throw e;
//        }
//    }
//
//    private PerformanceLevel createPerformanceLevel(String level, String name, Integer minScore,
//                                                  Integer maxScore, Double bonusRatio, String description) {
//        try {
//            PerformanceLevel performanceLevel = new PerformanceLevel();
//            performanceLevel.setLevel(level);
//            performanceLevel.setName(name);
//            performanceLevel.setMinScore(minScore);
//            performanceLevel.setMaxScore(maxScore);
//            performanceLevel.setBonusRatio(bonusRatio);
//            performanceLevel.setDescription(description);
//            return performanceLevelRepository.save(performanceLevel);
//        } catch (Exception e) {
//            log.error("创建绩效等级失败: " + level, e);
//            throw e;
//        }
//    }
//
//    private PerformanceCriteria createPerformanceCriteria(String name, Double weight, String department,
//                                                        String position, String items, String description) {
//        try {
//            PerformanceCriteria criteria = new PerformanceCriteria();
//            criteria.setName(name);
//            criteria.setWeight(weight);
//            criteria.setDepartment(department);
//            criteria.setPosition(position);
//            criteria.setItems(items);
//            criteria.setDescription(description);
//            return performanceCriteriaRepository.save(criteria);
//        } catch (Exception e) {
//            log.error("创建绩效评估标准失败: " + name, e);
//            throw e;
//        }
//    }
//
//    private PerformanceEvaluation createPerformanceEvaluation(String evaluationCode, Employee employee,
//                                                            String year, String quarter, String evaluationPeriod,
//                                                            Double score, String level, String status,
//                                                            String evaluator, LocalDate evaluationDate,
//                                                            LocalDate confirmDate, String managerComments,
//                                                            String employeeComments, String improvementPlan,
//                                                            String nextGoals, String evaluationDetails) {
//        try {
//            PerformanceEvaluation evaluation = new PerformanceEvaluation();
//            evaluation.setEvaluationCode(evaluationCode);
//            evaluation.setEmployee(employee);
//            evaluation.setYear(year);
//            evaluation.setQuarter(quarter);
//            evaluation.setEvaluationPeriod(evaluationPeriod);
//            evaluation.setScore(score);
//            evaluation.setLevel(level);
//            evaluation.setStatus(status);
//            evaluation.setEvaluator(evaluator);
//            evaluation.setEvaluationDate(evaluationDate);
//            evaluation.setConfirmDate(confirmDate);
//            evaluation.setManagerComments(managerComments);
//            evaluation.setEmployeeComments(employeeComments);
//            evaluation.setImprovementPlan(improvementPlan);
//            evaluation.setNextGoals(nextGoals);
//            evaluation.setEvaluationDetails(evaluationDetails);
//            return performanceEvaluationRepository.save(evaluation);
//        } catch (Exception e) {
//            log.error("创建绩效评估失败: " + employee.getFirstName() + " " + employee.getLastName(), e);
//            throw e;
//        }
//    }
//
//    private SalaryChangeHistory createSalaryChangeHistory(Employee employee, String changedField,
//                                                        String oldValue, String newValue,
//                                                        String reason, String operator,
//                                                        LocalDateTime changeDate) {
//        try {
//            SalaryChangeHistory history = new SalaryChangeHistory();
//            history.setEmployee(employee);
//            history.setChangedField(changedField);
//            history.setOldValue(oldValue);
//            history.setNewValue(newValue);
//            history.setReason(reason);
//            history.setOperator(operator);
//            history.setChangeDate(changeDate);
//            return salaryChangeHistoryRepository.save(history);
//        } catch (Exception e) {
//            log.error("创建薪资变更历史失败: " + employee.getFirstName() + " " + employee.getLastName(), e);
//            throw e;
//        }
//    }
//
//    private Payslip createPayslip(Employee employee, EmployeeSalary salary, String month,
//                                String status, LocalDateTime createTime, LocalDateTime distributeTime) {
//        try {
//            Payslip payslip = new Payslip();
//            payslip.setEmployee(employee);
//            payslip.setMonth(month);
//
//            // 设置薪资相关数据
//            payslip.setBaseSalary(salary.getBaseSalary());
//            payslip.setPerformanceSalary(salary.getPerformanceBonus());
//
//            // 假设加班费为0，实际项目中可能从考勤系统获取
//            payslip.setOvertimePay(0.0);
//
//            // 津贴
//            payslip.setOtherBonus(salary.getAllowance());
//
//            // 计算总工资
//            double totalSalary = salary.getBaseSalary() + salary.getPerformanceBonus() +
//                    payslip.getOvertimePay() + salary.getAllowance();
//            payslip.setTotalSalary(totalSalary);
//
//            // 计算社保（假设为基本工资的10%）
//            double socialInsurance = salary.getBaseSalary() * 0.1;
//            payslip.setSocialInsurance(socialInsurance);
//
//            // 计算住房公积金（假设为基本工资的7%）
//            double housingFund = salary.getBaseSalary() * 0.07;
//            payslip.setHousingFund(housingFund);
//
//            // 计算个人所得税（简化计算，实际应根据税法）
//            double taxableIncome = totalSalary - socialInsurance - housingFund - 5000; // 5000为起征点
//            double incomeTax = taxableIncome > 0 ? calculateTax(taxableIncome) : 0;
//            payslip.setIncomeTax(incomeTax);
//
//            // 其他扣除
//            payslip.setOtherDeduction(0.0);
//
//            // 计算总扣除和实发工资
//            double totalDeduction = socialInsurance + housingFund + incomeTax + payslip.getOtherDeduction();
//            payslip.setTotalDeduction(totalDeduction);
//            payslip.setActualSalary(totalSalary - totalDeduction);
//
//            // 设置状态和时间
//            payslip.setStatus(status);
//            payslip.setCreateTime(createTime);
//            payslip.setDistributeTime(distributeTime);
//
//            return payslipRepository.save(payslip);
//        } catch (Exception e) {
//            log.error("创建工资单失败: " + employee.getFirstName() + " " + employee.getLastName() + ", 月份: " + month, e);
//            throw e;
//        }
//    }
//
//    /**
//     * 计算个人所得税（简化计算，实际应根据税法）
//     */
//    private double calculateTax(double taxableIncome) {
//        // 简化计算，实际应按照累进税率计算
//        if (taxableIncome <= 3000) {
//            return taxableIncome * 0.03;
//        } else if (taxableIncome <= 12000) {
//            return taxableIncome * 0.1 - 210;
//        } else if (taxableIncome <= 25000) {
//            return taxableIncome * 0.2 - 1410;
//        } else if (taxableIncome <= 35000) {
//            return taxableIncome * 0.25 - 2660;
//        } else if (taxableIncome <= 55000) {
//            return taxableIncome * 0.3 - 4410;
//        } else if (taxableIncome <= 80000) {
//            return taxableIncome * 0.35 - 7160;
//        } else {
//            return taxableIncome * 0.45 - 15160;
//        }
//    }
//
//    private Permission createPermission(String permissionCode, String description) {
//        try {
//            Permission permission = new Permission();
//            permission.setPermissionCode(permissionCode);
//            permission.setDescription(description);
//            return permissionRepository.save(permission);
//        } catch (Exception e) {
//            log.error("创建权限失败: " + permissionCode, e);
//            throw e;
//        }
//    }
//
//    private AttendanceRecord createAttendanceRecord(Employee employee, LocalDate date,
//                                                   LocalTime clockInTime, LocalTime clockOutTime,
//                                                   AttendanceStatus status) {
//        try {
//            AttendanceRecord record = new AttendanceRecord();
//            record.setEmployee(employee);
//
//            if (clockInTime != null) {
//                record.setClockIn(LocalDateTime.of(date, clockInTime));
//            }
//
//            if (clockOutTime != null) {
//                record.setClockOut(LocalDateTime.of(date, clockOutTime));
//            }
//
//            record.setStatus(status);
//
//            // 现在使用repository保存考勤记录
//            return attendanceRecordRepository.save(record);
//        } catch (Exception e) {
//            log.error("创建考勤记录失败: " + employee.getFirstName() + " " + employee.getLastName(), e);
//            throw e;
//        }
//    }
//
//    private LeaveRecord createLeaveRecord(Employee employee, LeaveType leaveType,
//                                         LocalDate startDate, LocalDate endDate,
//                                         ApprovalStatus status, Employee approver) {
//        try {
//            LeaveRecord record = new LeaveRecord();
//            record.setEmployee(employee);
//            record.setLeaveType(leaveType);
//            record.setStartDate(startDate);
//            record.setEndDate(endDate);
//            record.setStatus(status);
//            record.setApprover(approver);
//
//            // 现在使用repository保存请假记录
//            return leaveRecordRepository.save(record);
//        } catch (Exception e) {
//            log.error("创建请假记录失败: " + employee.getFirstName() + " " + employee.getLastName(), e);
//            throw e;
//        }
//    }
//
//    private SalaryItem createSalaryItem(String itemName, ItemType itemType, Double amount) {
//        SalaryItem item = new SalaryItem();
//        item.setItemName(itemName);
//        item.setItemType(itemType);
//        item.setAmount(BigDecimal.valueOf(amount));
//        return item;
//    }
//
//    private SalaryRecord createSalaryRecord(Employee employee, String payPeriod, Double totalGross,
//                                           Double totalDeductions, Double netPay,
//                                           PaymentStatus paymentStatus, List<SalaryItem> items) {
//        try {
//            SalaryRecord record = new SalaryRecord();
//            record.setEmployee(employee);
//            record.setPayPeriod(payPeriod);
//            record.setTotalGross(totalGross);
//            record.setTotalDeductions(totalDeductions);
//            record.setNetPay(netPay);
//            record.setPaymentStatus(paymentStatus);
//            record.setItems(items);
//
//            // 现在使用repository保存薪资记录
//            return salaryRecordRepository.save(record);
//        } catch (Exception e) {
//            log.error("创建薪资记录失败: " + employee.getFirstName() + " " + employee.getLastName(), e);
//            throw e;
//        }
//    }
//}