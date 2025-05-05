export default [
  // 获取税务计算数据
  {
    url: '/api/tax/calculate',
    method: 'get',
    response: ({ query }) => {
      const { month, department, page = 1, pageSize = 10, socialMax, socialMin, housingMax, pensionRate, medicalRate, unemploymentRate, housingRate } = query;
      
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
          const overtimePay = (i % 3) * 800;
          const salary = baseSalary + performanceBonus + overtimePay;
          
          // 社保基数（在上下限范围内）
          const socialBase = Math.min(Math.max(salary, Number(socialMin) || 5080), Number(socialMax) || 25401);
          
          // 社保个人缴纳
          const pension = socialBase * (Number(pensionRate) || 0.08);
          const medical = socialBase * (Number(medicalRate) || 0.02);
          const unemployment = socialBase * (Number(unemploymentRate) || 0.005);
          const socialInsurance = pension + medical + unemployment;
          
          // 公积金个人缴纳
          const housingBase = Math.min(Math.max(salary, Number(socialMin) || 5080), Number(housingMax) || 25401);
          const housingFund = housingBase * (Number(housingRate) || 0.07);
          
          // 个税计算
          const specialDeduction = 5000; // 起征点
          const taxBase = salary - socialInsurance - housingFund - specialDeduction;
          const incomeTax = taxBase > 0 ? calculateIncomeTax(taxBase) : 0;
          
          // 总扣除
          const totalDeduction = socialInsurance + housingFund + incomeTax;
          
          employees.push({
            id: employeeId,
            name: `${dept}员工${i}`,
            department: dept,
            position: `${dept}专员`,
            salary,
            taxBase: taxBase > 0 ? taxBase : 0,
            incomeTax,
            socialBase,
            socialInsurance,
            housingFund,
            totalDeduction
          });
        }
      });
      
      // 筛选和分页
      const total = employees.length;
      const startIndex = (page - 1) * pageSize;
      const paginatedData = employees.slice(startIndex, startIndex + parseInt(pageSize));
      
      // 计算汇总数据
      const summary = {
        totalEmployees: total,
        totalTax: employees.reduce((sum, item) => sum + item.incomeTax, 0),
        totalSocialInsurance: employees.reduce((sum, item) => sum + item.socialInsurance, 0),
        totalHousingFund: employees.reduce((sum, item) => sum + item.housingFund, 0),
        totalDeduction: employees.reduce((sum, item) => sum + item.totalDeduction, 0)
      };
      
      return {
        code: 200,
        data: {
          employees: paginatedData,
          total,
          summary
        },
        message: '获取税务计算数据成功'
      };
    }
  },
  
  // 获取社保公积金配置
  {
    url: '/api/tax/insurance-config',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: {
          socialMax: 25401, // 社保缴纳基数上限
          socialMin: 5080,  // 社保缴纳基数下限
          housingMax: 25401, // 公积金缴纳基数上限
          pensionRate: 0.08, // 养老保险个人比例
          medicalRate: 0.02, // 医疗保险个人比例
          unemploymentRate: 0.005, // 失业保险个人比例
          housingRate: 0.07  // 公积金个人比例
        },
        message: '获取社保配置成功'
      };
    }
  },
  
  // 保存社保公积金配置
  {
    url: '/api/tax/insurance-config',
    method: 'post',
    response: ({ body }) => {
      return {
        code: 200,
        data: body,
        message: '社保配置保存成功'
      };
    }
  },
  
  // 调整个税
  {
    url: '/api/tax/adjust',
    method: 'post',
    response: ({ body }) => {
      const { id, reason } = body;
      
      if (!id || !reason) {
        return {
          code: 400,
          message: '缺少必要参数'
        };
      }
      
      return {
        code: 200,
        data: {
          id,
          adjustTime: new Date().toISOString(),
          operator: '当前用户'
        },
        message: '税务数据调整成功'
      };
    }
  },
  
  // 确认税务数据
  {
    url: '/api/tax/confirm',
    method: 'post',
    response: ({ body }) => {
      const { month, department } = body;
      
      return {
        code: 200,
        data: {
          month,
          department,
          confirmTime: new Date().toISOString(),
          status: 'confirmed'
        },
        message: '税务数据确认成功'
      };
    }
  }
];

// 简化的个税计算函数
function calculateIncomeTax(taxableIncome) {
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