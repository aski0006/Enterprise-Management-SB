export default [
  // 获取薪资核算数据
  {
    url: '/api/salary/calculation',
    method: 'get',
    response: ({ query }) => {
      const { period, department } = query;
      
      // 生成模拟员工数据
      let employees = [];
      const departmentsList = ['技术部', '人力资源部', '财务部', '市场部', '销售部'];
      
      // 如果指定了部门，只生成该部门的数据
      const deptList = department ? [department] : departmentsList;
      
      // 为每个部门生成员工
      deptList.forEach((dept, deptIndex) => {
        // 每个部门生成10个员工
        for (let i = 1; i <= 10; i++) {
          const employeeId = `E${String(deptIndex * 10 + i).padStart(5, '0')}`;
          const baseSalary = 8000 + (i % 10) * 500;
          const performanceBonus = 2000 + (i % 5) * 500;
          const overtimePay = Math.round((i % 4) * 200 * 100) / 100;
          const allowance = Math.round((i % 3) * 300 * 100) / 100;
          
          // 计算扣除项
          const insurance = Math.round(baseSalary * 0.11 * 100) / 100;
          const totalSalary = baseSalary + performanceBonus + overtimePay + allowance;
          
          // 简化计算个税
          const taxableIncome = totalSalary - insurance - 5000; // 5000是起征点
          const tax = taxableIncome > 0 ? Math.round(calculateTax(taxableIncome) * 100) / 100 : 0;
          
          const deduction = Math.round((i % 2) * 100 * 100) / 100;
          const netSalary = totalSalary - insurance - tax - deduction;
          
          employees.push({
            id: employeeId,
            name: `${dept}员工${i}`,
            department: dept,
            position: `${dept}专员${i > 5 ? '二级' : '一级'}`,
            baseSalary,
            performanceBonus,
            overtimePay,
            allowance,
            insurance,
            tax,
            deduction,
            totalSalary,
            netSalary
          });
        }
      });
      
      // 计算汇总数据
      const totalEmployees = employees.length;
      const totalSalary = employees.reduce((sum, emp) => sum + emp.totalSalary, 0);
      const totalInsurance = employees.reduce((sum, emp) => sum + emp.insurance, 0);
      const totalTax = employees.reduce((sum, emp) => sum + emp.tax, 0);
      
      return {
        code: 200,
        data: {
          totalEmployees,
          totalSalary,
          totalInsurance,
          totalTax,
          employees
        },
        message: '获取薪资核算数据成功'
      };
    }
  },
  
  // 确认薪资数据
  {
    url: '/api/salary/calculation/confirm',
    method: 'post',
    response: ({ body }) => {
      const { period, department } = body;
      
      return {
        code: 200,
        data: {
          period,
          department,
          confirmTime: new Date().toISOString(),
          status: 'confirmed'
        },
        message: '薪资数据确认成功'
      };
    }
  },
  
  // 调整薪资
  {
    url: '/api/salary/calculation/adjust',
    method: 'post',
    response: ({ body }) => {
      const { employeeId, item, value, reason } = body;
      
      // 校验必填项
      if (!employeeId || !item || !reason) {
        return {
          code: 400,
          message: '缺少必要参数'
        };
      }
      
      return {
        code: 200,
        data: {
          employeeId,
          item,
          value,
          adjustTime: new Date().toISOString(),
          operator: '当前用户'
        },
        message: '薪资调整成功'
      };
    }
  },
  
  // 生成工资条
  {
    url: '/api/salary/payslips/generate',
    method: 'post',
    response: ({ body }) => {
      const { period, department } = body;
      
      return {
        code: 200,
        data: {
          period,
          department,
          generatedCount: department ? 10 : 50, // 如果指定部门，则生成10条，否则生成50条
          generateTime: new Date().toISOString()
        },
        message: '工资条生成成功'
      };
    }
  }
];

// 简化的个税计算函数
function calculateTax(taxableIncome) {
  if (taxableIncome <= 0) return 0;
  
  // 个税速算表（简化）
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