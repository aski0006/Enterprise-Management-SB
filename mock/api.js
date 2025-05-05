// 薪资管理系统 - 统一API模拟文件

/**
 * 本文件整合所有模拟API，提供统一的接口
 * 按照业务模块对API进行分组，便于前端开发人员查阅使用
 */

// 引入用户认证相关API
import loginApi from './login-api'            // 登录API
import registerApi from './register-api'      // 注册API

// 引入薪资管理相关API
import salaryArchiveApi from './salary-archive-api'        // 薪资档案API
import salaryCalculationApi from './salary-calculation-api' // 薪资计算API
import salaryPayslipApi from './salary-payslip-api'        // 工资单API
import salaryStructureApi from './salary-structure-api'    // 薪资结构API
import salaryBudgetApi from './salary-budget-api'          // 薪资预算API
import salaryReportsApi from './salary-reports-api'        // 薪资报表API

// 引入绩效与考勤相关API
import performanceManagementApi from './performance-management-api'  // 绩效管理API
import attendanceManagementApi from './attendance-management-api'    // 考勤管理API
import overtimeManagementApi from './overtime-management-api'        // 加班管理API

// 引入福利与税务相关API
import benefitsManagementApi from './benefits-management-api'  // 福利管理API
import taxManagementApi from './tax-management-api'           // 税务管理API

// 合并所有API导出
export default [
  // 用户认证
  ...loginApi,
  ...registerApi,
  
  // 薪资管理
  ...salaryArchiveApi,
  ...salaryCalculationApi,
  ...salaryPayslipApi,
  ...salaryStructureApi,
  ...salaryBudgetApi,
  ...salaryReportsApi,
  
  // 绩效与考勤
  ...performanceManagementApi,
  ...attendanceManagementApi,
  ...overtimeManagementApi,
  
  // 福利与税务
  ...benefitsManagementApi,
  ...taxManagementApi
]

/**
 * API分组参考
 * 用于前端开发人员快速查找和理解可用API接口
 * 包含接口路径、请求方法、参数说明和返回值描述
 */
const apiGroups = {
  // 用户认证模块
  auth: {
    // 登录接口
    login: {
      url: '/api/login',
      method: 'POST',
      description: '用户登录认证',
      params: '{ username, password }',
      returns: '{ code, token, userInfo }'
    },
    // 注册接口
    register: {
      url: '/api/register',
      method: 'POST',
      description: '用户注册',
      params: '{ username, password, email, role }',
      returns: '{ code, message }'
    }
  },
  
  // 薪资档案管理模块
  salaryArchive: {
    // 获取员工薪资档案
    getArchives: {
      url: '/api/salary/archives',
      method: 'GET',
      description: '获取员工薪资档案列表',
      params: '{ employeeId, department, page, pageSize }',
      returns: '{ code, data: { archives, total }, message }'
    },
    // 获取档案详情
    getArchiveDetail: {
      url: '/api/salary/archive/detail',
      method: 'GET',
      description: '获取员工薪资档案详情',
      params: '{ id }',
      returns: '{ code, data, message }'
    },
    // 更新薪资档案
    updateArchive: {
      url: '/api/salary/archive/update',
      method: 'POST',
      description: '更新员工薪资档案',
      params: '{ id, ...archiveData }',
      returns: '{ code, data, message }'
    },
    // 获取历史变更记录
    getHistoryRecords: {
      url: '/api/salary/archive/history',
      method: 'GET',
      description: '获取薪资档案历史变更记录',
      params: '{ employeeId }',
      returns: '{ code, data: { records }, message }'
    }
  },
  
  // 薪酬核算模块
  salaryCalculation: {
    // 获取员工薪资计算数据
    getSalaryData: {
      url: '/api/salary/calculation/data',
      method: 'GET',
      description: '获取员工薪资计算所需数据',
      params: '{ employeeId, year, month }',
      returns: '{ code, data, message }'
    },
    // 计算薪资
    calculateSalary: {
      url: '/api/salary/calculate',
      method: 'POST',
      description: '计算员工薪资',
      params: '{ employeeId, year, month, ...calculationParams }',
      returns: '{ code, data: { salaryDetails }, message }'
    },
    // 确认薪资计算结果
    confirmCalculation: {
      url: '/api/salary/calculation/confirm',
      method: 'POST',
      description: '确认薪资计算结果',
      params: '{ id, status, remarks }',
      returns: '{ code, message }'
    }
  },
  
  // 工资单管理模块
  payslip: {
    // 获取工资单列表
    getPayslips: {
      url: '/api/payslip/list',
      method: 'GET',
      description: '获取工资单列表',
      params: '{ month, department, status, page, pageSize }',
      returns: '{ code, data: { payslips, total }, message }'
    },
    // 获取工资单详情
    getPayslipDetail: {
      url: '/api/payslip/detail',
      method: 'GET',
      description: '获取工资单详情',
      params: '{ id, month }',
      returns: '{ code, data, message }'
    },
    // 生成工资单
    generatePayslips: {
      url: '/api/payslip/generate',
      method: 'POST',
      description: '生成工资单',
      params: '{ month, department }',
      returns: '{ code, data, message }'
    },
    // 发放工资单
    distributePayslips: {
      url: '/api/payslip/distribute',
      method: 'POST',
      description: '批量发放工资单',
      params: '{ month, department }',
      returns: '{ code, data, message }'
    },
    // 发放单个工资单
    distributeOnePayslip: {
      url: '/api/payslip/distribute-one',
      method: 'POST',
      description: '发放单个工资单',
      params: '{ id, month }',
      returns: '{ code, data, message }'
    }
  },
  
  // 薪资结构管理模块
  salaryStructure: {
    // 获取薪资等级列表
    getSalaryGrades: {
      url: '/api/salary/structure/grades',
      method: 'GET',
      description: '获取薪资等级列表',
      params: '{ department }',
      returns: '{ code, data: { grades }, message }'
    },
    // 保存薪资等级
    saveSalaryGrade: {
      url: '/api/salary/structure/grade/save',
      method: 'POST',
      description: '保存薪资等级',
      params: '{ id, ...gradeData }',
      returns: '{ code, data, message }'
    },
    // 获取调薪规则
    getAdjustmentRules: {
      url: '/api/salary/structure/adjustment-rules',
      method: 'GET',
      description: '获取调薪规则',
      params: '{ type }',
      returns: '{ code, data: { rules }, message }'
    },
    // 获取奖金方案
    getBonusSchemes: {
      url: '/api/salary/structure/bonus-schemes',
      method: 'GET',
      description: '获取奖金方案',
      params: '{ department }',
      returns: '{ code, data: { schemes }, message }'
    }
  },
  
  // 薪资预算模块
  salaryBudget: {
    // 部门预算查询
    getDepartmentBudget: {
      url: '/api/salary/budget/department',
      method: 'GET',
      description: '获取部门薪资预算',
      params: '{ year, department, quarter }',
      returns: '{ code, data, message }'
    },
    // 生成预算预测
    generateForecast: {
      url: '/api/salary/budget/forecast',
      method: 'GET',
      description: '生成薪资预算预测',
      params: '{ year, growthRate, parameters }',
      returns: '{ code, data: { forecast }, message }'
    },
    // 获取预算调整历史
    getAdjustmentHistory: {
      url: '/api/salary/budget/adjustment-history',
      method: 'GET',
      description: '获取预算调整历史',
      params: '{ year, department }',
      returns: '{ code, data: { history }, message }'
    },
    // 获取预算执行分析
    getExecutionAnalysis: {
      url: '/api/salary/budget/execution-analysis',
      method: 'GET',
      description: '获取预算执行分析',
      params: '{ year, department, quarter }',
      returns: '{ code, data, message }'
    }
  },
  
  // 薪资报表模块
  salaryReports: {
    // 员工薪资趋势
    getEmployeeSalaryTrend: {
      url: '/api/salary/reports/employee-trend',
      method: 'GET',
      description: '获取员工薪资趋势',
      params: '{ employeeId, startYear, endYear }',
      returns: '{ code, data: { trend }, message }'
    },
    // 部门薪资对比
    getDepartmentComparison: {
      url: '/api/salary/reports/department-comparison',
      method: 'GET',
      description: '获取部门薪资对比',
      params: '{ year, quarter }',
      returns: '{ code, data: { comparison }, message }'
    },
    // 薪资分布统计
    getSalaryDistribution: {
      url: '/api/salary/reports/salary-distribution',
      method: 'GET',
      description: '获取薪资分布统计',
      params: '{ department, year }',
      returns: '{ code, data: { distribution }, message }'
    },
    // 加薪分析
    getRaiseAnalysis: {
      url: '/api/salary/reports/raise-analysis',
      method: 'GET',
      description: '获取加薪分析',
      params: '{ year, department }',
      returns: '{ code, data: { analysis }, message }'
    },
    // 薪酬总成本报告
    getCompensationSummary: {
      url: '/api/salary/reports/compensation-summary',
      method: 'GET',
      description: '获取薪酬总成本报告',
      params: '{ year, breakdown }',
      returns: '{ code, data: { summary }, message }'
    }
  },
  
  // 绩效管理模块
  performance: {
    // 员工绩效评估列表
    getEmployeeEvaluations: {
      url: '/api/performance/employee-evaluations',
      method: 'GET',
      description: '获取员工绩效评估列表',
      params: '{ employeeId, year, quarter, page, pageSize }',
      returns: '{ code, data: { evaluations, total }, message }'
    },
    // 评估详情
    getEvaluationDetail: {
      url: '/api/performance/evaluation-detail',
      method: 'GET',
      description: '获取评估详情',
      params: '{ id }',
      returns: '{ code, data, message }'
    },
    // 部门绩效汇总
    getDepartmentSummary: {
      url: '/api/performance/department-summary',
      method: 'GET',
      description: '获取部门绩效汇总',
      params: '{ year, quarter, department }',
      returns: '{ code, data, message }'
    },
    // 绩效评估标准
    getCriteria: {
      url: '/api/performance/criteria',
      method: 'GET',
      description: '获取绩效评估标准',
      params: '{ departmentId, position }',
      returns: '{ code, data: { commonCriteria, specificCriteria, performanceLevels }, message }'
    }
  },
  
  // 考勤管理模块
  attendance: {
    // 考勤记录
    getAttendanceRecords: {
      url: '/api/attendance/records',
      method: 'GET',
      description: '获取考勤记录',
      params: '{ employeeId, startDate, endDate, status, page, pageSize }',
      returns: '{ code, data: { records, total, summary }, message }'
    },
    // 月度考勤统计
    getMonthlyStatistics: {
      url: '/api/attendance/monthly-statistics',
      method: 'GET',
      description: '获取月度考勤统计',
      params: '{ year, month, department }',
      returns: '{ code, data: { statistics, departmentSummary }, message }'
    },
    // 加班记录
    getOvertimeRecords: {
      url: '/api/attendance/overtime-records',
      method: 'GET',
      description: '获取加班记录',
      params: '{ employeeId, startDate, endDate, page, pageSize }',
      returns: '{ code, data: { records, total, summary }, message }'
    },
    // 请假记录
    getLeaveRecords: {
      url: '/api/attendance/leave-records',
      method: 'GET',
      description: '获取请假记录',
      params: '{ employeeId, startDate, endDate, type, page, pageSize }',
      returns: '{ code, data: { records, total, summary }, message }'
    }
  },
  
  // 加班管理模块
  overtime: {
    // 加班记录查询
    getOvertimeRecords: {
      url: '/api/overtime/records',
      method: 'GET',
      description: '获取加班记录',
      params: '{ employeeId, month, status, page, pageSize }',
      returns: '{ code, data: { records, total }, message }'
    },
    // 提交加班申请
    submitOvertime: {
      url: '/api/overtime/submit',
      method: 'POST',
      description: '提交加班申请',
      params: '{ employeeId, date, startTime, endTime, reason, type }',
      returns: '{ code, data, message }'
    },
    // 审批加班申请
    approveOvertime: {
      url: '/api/overtime/approve',
      method: 'POST',
      description: '审批加班申请',
      params: '{ id, status, remarks }',
      returns: '{ code, message }'
    },
    // 加班统计
    getOvertimeStatistics: {
      url: '/api/overtime/statistics',
      method: 'GET',
      description: '获取加班统计',
      params: '{ department, year, month }',
      returns: '{ code, data: { statistics, departmentData, monthlyData }, message }'
    },
    // 加班规则
    getOvertimeRules: {
      url: '/api/overtime/rules',
      method: 'GET',
      description: '获取加班规则',
      params: '{}',
      returns: '{ code, data: { rules, compensations, limits }, message }'
    }
  },
  
  // 福利管理模块
  benefits: {
    // 员工福利
    getEmployeeBenefits: {
      url: '/api/benefits/employee-benefits',
      method: 'GET',
      description: '获取员工福利',
      params: '{ employeeId, page, pageSize }',
      returns: '{ code, data: { benefits, summary }, message }'
    },
    // 可用福利计划
    getAvailablePlans: {
      url: '/api/benefits/available-plans',
      method: 'GET',
      description: '获取可用福利计划',
      params: '{ benefitType, department }',
      returns: '{ code, data: { plans }, message }'
    },
    // 福利报销记录
    getClaimRecords: {
      url: '/api/benefits/claim-records',
      method: 'GET',
      description: '获取福利报销记录',
      params: '{ employeeId, benefitType, status, startDate, endDate, page, pageSize }',
      returns: '{ code, data: { records, summary }, message }'
    },
    // 福利使用统计
    getUsageStatistics: {
      url: '/api/benefits/usage-statistics',
      method: 'GET',
      description: '获取福利使用统计',
      params: '{ year, department }',
      returns: '{ code, data: { statistics, departmentData, monthlyData }, message }'
    }
  },
  
  // 税务管理模块
  tax: {
    // 员工税务数据
    getEmployeeTaxData: {
      url: '/api/tax/employee-data',
      method: 'GET',
      description: '获取员工税务数据',
      params: '{ employeeId, year, month }',
      returns: '{ code, data, message }'
    },
    // 税收配置
    getTaxConfig: {
      url: '/api/tax/config',
      method: 'GET',
      description: '获取税收配置',
      params: '{ year }',
      returns: '{ code, data: { taxBrackets, socialSecurityRates, specialDeductions }, message }'
    },
    // 年度税务报告
    getAnnualTaxReport: {
      url: '/api/tax/annual-report',
      method: 'GET',
      description: '获取年度税务报告',
      params: '{ employeeId, year }',
      returns: '{ code, data: { monthlyData, summary }, message }'
    }
  }
}

// 导出API分组，方便前端引用
export { apiGroups } 