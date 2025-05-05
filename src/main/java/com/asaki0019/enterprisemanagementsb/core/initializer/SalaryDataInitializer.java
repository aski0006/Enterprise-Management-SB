package com.asaki0019.enterprisemanagementsb.core.initializer;

import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.entities.salary.*;
import com.asaki0019.enterprisemanagementsb.mapper.salary.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class SalaryDataInitializer implements CommandLineRunner {

    private final SalaryGradeRepository salaryGradeRepository;
    private final AdjustRuleRepository adjustRuleRepository;
    private final BonusSchemeRepository bonusSchemeRepository;
    private final SalaryCalculationRepository salaryCalculationRepository;
    private final SalaryPayslipRepository salaryPayslipRepository;
    private final SysLogger sysLogger;

    @Autowired
    public SalaryDataInitializer(
            SalaryGradeRepository salaryGradeRepository,
            AdjustRuleRepository adjustRuleRepository, 
            BonusSchemeRepository bonusSchemeRepository,
            SalaryCalculationRepository salaryCalculationRepository,
            SalaryPayslipRepository salaryPayslipRepository,
            SysLogger sysLogger) {
        this.salaryGradeRepository = salaryGradeRepository;
        this.adjustRuleRepository = adjustRuleRepository;
        this.bonusSchemeRepository = bonusSchemeRepository;
        this.salaryCalculationRepository = salaryCalculationRepository;
        this.salaryPayslipRepository = salaryPayslipRepository;
        this.sysLogger = sysLogger;
    }

    @Override
    public void run(String... args) {
        try {
            // 检查数据库中是否已有数据
            if (salaryGradeRepository.count() > 0) {
                sysLogger.info("数据库中已有薪酬数据，跳过初始化");
                return;
            }

            sysLogger.info("开始初始化薪酬管理示例数据");
            
            initSalaryGrades();
            initAdjustRules();
            initBonusSchemes();
            initSalaryCalculations();
            initSalaryPayslips();
            
            sysLogger.info("薪酬管理示例数据初始化完成");
        } catch (Exception e) {
            sysLogger.error("初始化薪酬管理示例数据失败", e);
        }
    }

    private void initSalaryGrades() {
        List<SalaryGrade> grades = new ArrayList<>();
        
        String[] departments = {"全部部门", "技术部", "人力资源部", "财务部", "市场部", "销售部"};
        
        for (int i = 1; i <= 30; i++) {
            int grade = (i - 1) / 3 + 1;  // 每3个一个等级
            int level = i % 3 == 0 ? 3 : i % 3;  // 1, 2, 3的循环
            
            BigDecimal minSalary = new BigDecimal(5000 + 1000 * grade);
            BigDecimal maxSalary = minSalary.add(new BigDecimal(2000 + (level * 500)));
            BigDecimal midSalary = minSalary.add(maxSalary).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            
            SalaryGrade salaryGrade = new SalaryGrade();
            salaryGrade.setGradeName(grade + "级");
            salaryGrade.setLevel(level + "档");
            salaryGrade.setTitle(grade + "级" + level + "档");
            salaryGrade.setDepartment(departments[grade % departments.length]);
            
            String positionType;
            if (grade <= 2) positionType = "初级";
            else if (grade <= 4) positionType = "中级";
            else if (grade <= 6) positionType = "高级";
            else positionType = "专家";
            salaryGrade.setPositionTypes(positionType);
            
            salaryGrade.setMinSalary(minSalary);
            salaryGrade.setMaxSalary(maxSalary);
            salaryGrade.setMidSalary(midSalary);
            
            // 基本工资比例随等级增加而下降，绩效比例随等级增加而上升
            salaryGrade.setBaseSalaryRatio(BigDecimal.valueOf(0.7 - 0.01 * grade).setScale(2, RoundingMode.HALF_UP));
            salaryGrade.setPerformanceRatio(BigDecimal.valueOf(0.2 + 0.01 * grade).setScale(2, RoundingMode.HALF_UP));
            
            salaryGrade.setCreatedTime(LocalDateTime.now());
            salaryGrade.setUpdatedTime(LocalDateTime.now());
            
            grades.add(salaryGrade);
        }
        
        salaryGradeRepository.saveAll(grades);
        sysLogger.info("初始化了 " + grades.size() + " 条薪资等级数据");
    }

    private void initAdjustRules() {
        List<AdjustRule> rules = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 年度调薪规则
        AdjustRule rule1 = new AdjustRule();
        rule1.setRuleName("年度调薪规则");
        rule1.setRuleType("定期调薪");
        rule1.setApplyTo("全部员工");
        rule1.setEffectiveDate(LocalDate.parse("2023-01-01", formatter));
        rule1.setExpireDate(LocalDate.parse("2023-12-31", formatter));
        rule1.setAdjustRatio("{\"excellent\":0.15,\"good\":0.10,\"average\":0.05,\"poor\":0}");
        rule1.setApprovalFlow("[\"部门经理\",\"人力资源总监\",\"财务总监\",\"CEO\"]");
        rule1.setStatus("生效中");
        rule1.setCreatedBy("管理员");
        rule1.setCreatedTime(LocalDateTime.parse("2022-12-01T10:00:00"));
        rule1.setDescription("根据年度绩效评估结果进行对应比例的调薪");
        rules.add(rule1);
        
        // 技术部晋升调薪规则
        AdjustRule rule2 = new AdjustRule();
        rule2.setRuleName("技术部晋升调薪规则");
        rule2.setRuleType("晋升调薪");
        rule2.setApplyTo("技术部");
        rule2.setEffectiveDate(LocalDate.parse("2023-02-01", formatter));
        rule2.setExpireDate(LocalDate.parse("2024-01-31", formatter));
        rule2.setAdjustRatio("{\"oneLevelUp\":0.20,\"twoLevelUp\":0.35,\"threeLevelUp\":0.50}");
        rule2.setApprovalFlow("[\"技术总监\",\"人力资源总监\",\"CTO\",\"CEO\"]");
        rule2.setStatus("生效中");
        rule2.setCreatedBy("管理员");
        rule2.setCreatedTime(LocalDateTime.parse("2023-01-15T10:00:00"));
        rule2.setDescription("根据晋升级别差距确定调薪比例");
        rules.add(rule2);
        
        // 销售部业绩调薪规则
        AdjustRule rule3 = new AdjustRule();
        rule3.setRuleName("销售部业绩调薪规则");
        rule3.setRuleType("绩效调薪");
        rule3.setApplyTo("销售部");
        rule3.setEffectiveDate(LocalDate.parse("2023-03-01", formatter));
        rule3.setExpireDate(LocalDate.parse("2023-12-31", formatter));
        rule3.setAdjustRatio("{\"overTarget200\":0.25,\"overTarget150\":0.15,\"overTarget120\":0.10,\"overTarget100\":0.05}");
        rule3.setApprovalFlow("[\"销售经理\",\"销售总监\",\"人力资源总监\",\"CEO\"]");
        rule3.setStatus("生效中");
        rule3.setCreatedBy("管理员");
        rule3.setCreatedTime(LocalDateTime.parse("2023-02-20T10:00:00"));
        rule3.setDescription("根据销售业绩完成目标比例进行调薪");
        rules.add(rule3);
        
        // 试用期转正调薪规则
        AdjustRule rule4 = new AdjustRule();
        rule4.setRuleName("试用期转正调薪规则");
        rule4.setRuleType("转正调薪");
        rule4.setApplyTo("全部员工");
        rule4.setEffectiveDate(LocalDate.parse("2023-01-01", formatter));
        rule4.setExpireDate(LocalDate.parse("2025-12-31", formatter));
        rule4.setAdjustRatio("{\"excellent\":0.20,\"good\":0.15,\"average\":0.10,\"poor\":0}");
        rule4.setApprovalFlow("[\"部门经理\",\"人力资源经理\"]");
        rule4.setStatus("生效中");
        rule4.setCreatedBy("管理员");
        rule4.setCreatedTime(LocalDateTime.parse("2022-12-15T10:00:00"));
        rule4.setDescription("根据试用期表现确定转正调薪比例");
        rules.add(rule4);
        
        // 季度绩效调薪规则
        AdjustRule rule5 = new AdjustRule();
        rule5.setRuleName("季度绩效调薪规则");
        rule5.setRuleType("绩效调薪");
        rule5.setApplyTo("市场部");
        rule5.setEffectiveDate(LocalDate.parse("2023-04-01", formatter));
        rule5.setExpireDate(LocalDate.parse("2023-12-31", formatter));
        rule5.setAdjustRatio("{\"excellent\":0.08,\"good\":0.05,\"average\":0.02,\"poor\":0}");
        rule5.setApprovalFlow("[\"市场经理\",\"人力资源经理\",\"市场总监\"]");
        rule5.setStatus("草稿");
        rule5.setCreatedBy("管理员");
        rule5.setCreatedTime(LocalDateTime.parse("2023-03-25T10:00:00"));
        rule5.setDescription("根据季度绩效评估进行调薪");
        rules.add(rule5);
        
        adjustRuleRepository.saveAll(rules);
        sysLogger.info("初始化了 " + rules.size() + " 条调薪规则数据");
    }

    private void initBonusSchemes() {
        List<BonusScheme> schemes = new ArrayList<>();
        
        // 年终奖金方案
        BonusScheme scheme1 = new BonusScheme();
        scheme1.setSchemeName("年终奖金方案");
        scheme1.setDepartment("全部部门");
        scheme1.setYear("2023");
        scheme1.setFormulaType("基础工资倍数");
        scheme1.setFormulaDetail("{\"excellent\":4,\"good\":3,\"average\":2,\"poor\":1}");
        scheme1.setTotalBudget(new BigDecimal("4500000"));
        scheme1.setDistributionMethod("绩效比例分配");
        scheme1.setApprovalStatus("已批准");
        scheme1.setPaymentDate("2024-01-20");
        scheme1.setCreatedBy("管理员");
        scheme1.setCreatedTime(LocalDateTime.parse("2023-11-15T10:00:00"));
        scheme1.setDescription("根据年度绩效评估结果确定年终奖金倍数");
        schemes.add(scheme1);
        
        // 销售季度奖金方案
        BonusScheme scheme2 = new BonusScheme();
        scheme2.setSchemeName("销售季度奖金方案");
        scheme2.setDepartment("销售部");
        scheme2.setYear("2023");
        scheme2.setFormulaType("销售业绩比例");
        scheme2.setFormulaDetail("{\"overTarget150\":0.15,\"overTarget120\":0.10,\"overTarget100\":0.05,\"belowTarget\":0}");
        scheme2.setTotalBudget(new BigDecimal("800000"));
        scheme2.setDistributionMethod("业绩目标达成比例");
        scheme2.setApprovalStatus("已批准");
        scheme2.setPaymentDate("2023-04-15,2023-07-15,2023-10-15,2024-01-15");
        scheme2.setCreatedBy("管理员");
        scheme2.setCreatedTime(LocalDateTime.parse("2023-01-10T10:00:00"));
        scheme2.setDescription("根据季度销售业绩完成比例发放季度奖金");
        schemes.add(scheme2);
        
        // 技术创新奖金方案
        BonusScheme scheme3 = new BonusScheme();
        scheme3.setSchemeName("技术创新奖金方案");
        scheme3.setDepartment("技术部");
        scheme3.setYear("2023");
        scheme3.setFormulaType("固定金额+项目贡献");
        scheme3.setFormulaDetail("{\"basePart\":5000,\"projectContribution\":\"根据项目价值评估\"}");
        scheme3.setTotalBudget(new BigDecimal("600000"));
        scheme3.setDistributionMethod("项目价值评估");
        scheme3.setApprovalStatus("已批准");
        scheme3.setPaymentDate("2023-06-15,2023-12-15");
        scheme3.setCreatedBy("管理员");
        scheme3.setCreatedTime(LocalDateTime.parse("2023-01-20T10:00:00"));
        scheme3.setDescription("奖励技术创新和重要项目贡献");
        schemes.add(scheme3);
        
        // 财务部绩效奖金方案
        BonusScheme scheme4 = new BonusScheme();
        scheme4.setSchemeName("财务部绩效奖金方案");
        scheme4.setDepartment("财务部");
        scheme4.setYear("2023");
        scheme4.setFormulaType("固定金额");
        scheme4.setFormulaDetail("{\"excellent\":10000,\"good\":8000,\"average\":5000,\"poor\":2000}");
        scheme4.setTotalBudget(new BigDecimal("300000"));
        scheme4.setDistributionMethod("绩效评级分配");
        scheme4.setApprovalStatus("已批准");
        scheme4.setPaymentDate("2023-12-20");
        scheme4.setCreatedBy("管理员");
        scheme4.setCreatedTime(LocalDateTime.parse("2023-01-25T10:00:00"));
        scheme4.setDescription("根据年度绩效评估发放固定金额奖金");
        schemes.add(scheme4);
        
        // 市场部项目奖金方案
        BonusScheme scheme5 = new BonusScheme();
        scheme5.setSchemeName("市场部项目奖金方案");
        scheme5.setDepartment("市场部");
        scheme5.setYear("2023");
        scheme5.setFormulaType("项目提成比例");
        scheme5.setFormulaDetail("{\"successfulCampaign\":\"项目预算的5%\",\"normalCampaign\":\"项目预算的3%\"}");
        scheme5.setTotalBudget(new BigDecimal("450000"));
        scheme5.setDistributionMethod("项目成功度评估");
        scheme5.setApprovalStatus("审批中");
        scheme5.setPaymentDate("项目结束后15天内");
        scheme5.setCreatedBy("管理员");
        scheme5.setCreatedTime(LocalDateTime.parse("2023-02-05T10:00:00"));
        scheme5.setDescription("根据市场活动成功程度发放项目奖金");
        schemes.add(scheme5);
        
        bonusSchemeRepository.saveAll(schemes);
        sysLogger.info("初始化了 " + schemes.size() + " 条奖金方案数据");
    }

    private void initSalaryCalculations() {
        List<SalaryCalculation> calculations = new ArrayList<>();
        String[] departments = {"技术部", "人力资源部", "财务部", "市场部", "销售部"};
        String currentPeriod = "2023-11"; // 当前周期
        
        // 为每个部门生成员工薪资数据
        for (int deptIndex = 0; deptIndex < departments.length; deptIndex++) {
            String department = departments[deptIndex];
            
            // 每个部门生成10个员工
            for (int i = 1; i <= 10; i++) {
                String employeeId = "E" + String.format("%05d", deptIndex * 10 + i);
                BigDecimal baseSalary = new BigDecimal(8000 + (i % 10) * 500);
                BigDecimal performanceBonus = new BigDecimal(2000 + (i % 5) * 500);
                BigDecimal overtimePay = new BigDecimal((i % 4) * 200).setScale(2, RoundingMode.HALF_UP);
                BigDecimal allowance = new BigDecimal((i % 3) * 300).setScale(2, RoundingMode.HALF_UP);
                
                // 计算扣除项
                BigDecimal insurance = baseSalary.multiply(new BigDecimal("0.11")).setScale(2, RoundingMode.HALF_UP);
                BigDecimal totalSalary = baseSalary.add(performanceBonus).add(overtimePay).add(allowance);
                
                // 简化计算个税
                BigDecimal taxableIncome = totalSalary.subtract(insurance).subtract(new BigDecimal(5000));
                BigDecimal tax = taxableIncome.compareTo(BigDecimal.ZERO) > 0 ? 
                        calculateTax(taxableIncome).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                
                BigDecimal deduction = new BigDecimal((i % 2) * 100).setScale(2, RoundingMode.HALF_UP);
                BigDecimal netSalary = totalSalary.subtract(insurance).subtract(tax).subtract(deduction);
                
                SalaryCalculation calculation = new SalaryCalculation();
                calculation.setEmployeeId(employeeId);
                calculation.setName(department + "员工" + i);
                calculation.setDepartment(department);
                calculation.setPosition(department + "专员" + (i > 5 ? "二级" : "一级"));
                calculation.setPeriod(currentPeriod);
                calculation.setBaseSalary(baseSalary);
                calculation.setPerformanceBonus(performanceBonus);
                calculation.setOvertimePay(overtimePay);
                calculation.setAllowance(allowance);
                calculation.setInsurance(insurance);
                calculation.setTax(tax);
                calculation.setDeduction(deduction);
                calculation.setTotalSalary(totalSalary);
                calculation.setNetSalary(netSalary);
                calculation.setStatus(i % 3 == 0 ? "confirmed" : "pending"); // 部分设为已确认状态
                calculation.setCreateTime(LocalDateTime.now());
                
                if (i % 3 == 0) {
                    calculation.setConfirmTime(LocalDateTime.now().minusDays(1));
                    calculation.setConfirmedBy("系统管理员");
                }
                
                calculations.add(calculation);
            }
        }
        
        salaryCalculationRepository.saveAll(calculations);
        sysLogger.info("初始化了 " + calculations.size() + " 条薪资核算数据");
    }

    private void initSalaryPayslips() {
        List<SalaryPayslip> payslips = new ArrayList<>();
        String currentMonth = "2023-11"; // 当前月份
        
        // 获取已确认的薪资核算数据
        List<SalaryCalculation> confirmedCalculations = salaryCalculationRepository.findByPeriodAndStatus(currentMonth, "confirmed");
        
        for (SalaryCalculation calc : confirmedCalculations) {
            SalaryPayslip payslip = new SalaryPayslip();
            payslip.setEmployeeId(calc.getEmployeeId());
            payslip.setName(calc.getName());
            payslip.setDepartment(calc.getDepartment());
            payslip.setPosition(calc.getPosition());
            payslip.setMonth(currentMonth);
            
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
            
            BigDecimal totalDeduction = socialInsurance.add(housingFund).add(calc.getTax()).add(calc.getDeduction());
            
            payslip.setTotalDeduction(totalDeduction);
            payslip.setActualSalary(calc.getTotalSalary().subtract(totalDeduction));
            
            // 随机设置一些工资单为已发放状态
            boolean isDistributed = calc.getEmployeeId().hashCode() % 3 == 0;
            payslip.setStatus(isDistributed ? "distributed" : "generated");
            payslip.setCreateTime(LocalDateTime.now().minusDays(5));
            
            if (isDistributed) {
                payslip.setDistributeTime(LocalDateTime.now().minusDays(1));
            }
            
            payslips.add(payslip);
        }
        
        salaryPayslipRepository.saveAll(payslips);
        sysLogger.info("初始化了 " + payslips.size() + " 条工资单数据");
    }
    
    // 简化的个税计算函数
    private BigDecimal calculateTax(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 个税速算表（简化）
        if (taxableIncome.compareTo(new BigDecimal(3000)) <= 0) {
            return taxableIncome.multiply(new BigDecimal("0.03"));
        } else if (taxableIncome.compareTo(new BigDecimal(12000)) <= 0) {
            return taxableIncome.multiply(new BigDecimal("0.1")).subtract(new BigDecimal(210));
        } else if (taxableIncome.compareTo(new BigDecimal(25000)) <= 0) {
            return taxableIncome.multiply(new BigDecimal("0.2")).subtract(new BigDecimal(1410));
        } else if (taxableIncome.compareTo(new BigDecimal(35000)) <= 0) {
            return taxableIncome.multiply(new BigDecimal("0.25")).subtract(new BigDecimal(2660));
        } else if (taxableIncome.compareTo(new BigDecimal(55000)) <= 0) {
            return taxableIncome.multiply(new BigDecimal("0.3")).subtract(new BigDecimal(4410));
        } else if (taxableIncome.compareTo(new BigDecimal(80000)) <= 0) {
            return taxableIncome.multiply(new BigDecimal("0.35")).subtract(new BigDecimal(7160));
        } else {
            return taxableIncome.multiply(new BigDecimal("0.45")).subtract(new BigDecimal(15160));
        }
    }
} 