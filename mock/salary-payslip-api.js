export default [
  // 获取工资单列表
  {
    url: '/api/payslip/list',
    method: 'get',
    response: ({ query }) => {
      const { month, department, status, page = 1, pageSize = 10 } = query;
      
      // 生成模拟工资单数据
      let payslips = [];
      const departmentsList = ['技术部', '人力资源部', '财务部', '市场部', '销售部'];
      
      // 如果指定了部门，只生成该部门的数据
      const deptList = department ? [department] : departmentsList;
      
      // 为每个部门生成员工工资单
      deptList.forEach((dept, deptIndex) => {
        // 每个部门生成10个员工
        for (let i = 1; i <= 10; i++) {
          const employeeId = `E${String(deptIndex * 10 + i).padStart(5, '0')}`;
          const baseSalary = 8000 + (i % 10) * 500;
          const performanceSalary = 2000 + (i % 5) * 500;
          const overtimePay = (i % 3) * 800;
          const otherBonus = (i % 4) * 200;
          const totalSalary = baseSalary + performanceSalary + overtimePay + otherBonus;
          
          // 扣除项
          const socialInsurance = Math.round(baseSalary * 0.08 * 100) / 100;
          const housingFund = Math.round(baseSalary * 0.07 * 100) / 100;
          const incomeTax = Math.round((totalSalary - socialInsurance - housingFund - 5000) * 0.1 * 100) / 100;
          const otherDeduction = (i % 2) * 100;
          const totalDeduction = socialInsurance + housingFund + incomeTax + otherDeduction;
          
          // 实发工资
          const actualSalary = totalSalary - totalDeduction;
          
          // 工资单状态
          let payslipStatus = 'generated';
          let distributeTime = null;
          
          // 如果单号能被3整除，则设为已发放状态
          if (i % 3 === 0) {
            payslipStatus = 'distributed';
            distributeTime = `2023-${month.split('-')[1]}-15 10:30:00`;
          }
          
          // 如果指定了状态筛选，则只返回匹配的状态
          if (status && status !== payslipStatus) {
            continue;
          }
          
          payslips.push({
            id: employeeId,
            name: `${dept}员工${i}`,
            department: dept,
            position: `${dept}专员`,
            month,
            baseSalary,
            performanceSalary,
            overtimePay,
            otherBonus,
            totalSalary,
            socialInsurance,
            housingFund,
            incomeTax,
            otherDeduction,
            totalDeduction,
            actualSalary,
            status: payslipStatus,
            createTime: `2023-${month.split('-')[1]}-05 09:30:00`,
            distributeTime
          });
        }
      });
      
      // 计算分页
      const total = payslips.length;
      const startIndex = (page - 1) * pageSize;
      const paginatedData = payslips.slice(startIndex, startIndex + parseInt(pageSize));
      
      return {
        code: 200,
        data: {
          payslips: paginatedData,
          total
        },
        message: '获取工资单列表成功'
      };
    }
  },
  
  // 获取工资单详情
  {
    url: '/api/payslip/detail',
    method: 'get',
    response: ({ query }) => {
      const { id, month } = query;
      
      if (!id || !month) {
        return {
          code: 400,
          message: '缺少必要参数'
        };
      }
      
      // 生成工资单详情
      const idNum = parseInt(id.replace('E', ''));
      const deptIndex = Math.floor(idNum / 10);
      const i = idNum % 10 || 10;
      const dept = ['技术部', '人力资源部', '财务部', '市场部', '销售部'][deptIndex % 5];
      
      const baseSalary = 8000 + (i % 10) * 500;
      const performanceSalary = 2000 + (i % 5) * 500;
      const overtimePay = (i % 3) * 800;
      const otherBonus = (i % 4) * 200;
      const totalSalary = baseSalary + performanceSalary + overtimePay + otherBonus;
      
      // 扣除项
      const socialInsurance = Math.round(baseSalary * 0.08 * 100) / 100;
      const housingFund = Math.round(baseSalary * 0.07 * 100) / 100;
      const incomeTax = Math.round((totalSalary - socialInsurance - housingFund - 5000) * 0.1 * 100) / 100;
      const otherDeduction = (i % 2) * 100;
      const totalDeduction = socialInsurance + housingFund + incomeTax + otherDeduction;
      
      // 实发工资
      const actualSalary = totalSalary - totalDeduction;
      
      // 工资单状态
      let payslipStatus = 'generated';
      let distributeTime = null;
      
      // 如果单号能被3整除，则设为已发放状态
      if (i % 3 === 0) {
        payslipStatus = 'distributed';
        distributeTime = `2023-${month.split('-')[1]}-15 10:30:00`;
      }
      
      return {
        code: 200,
        data: {
          id,
          name: `${dept}员工${i}`,
          department: dept,
          position: `${dept}专员`,
          month,
          baseSalary,
          performanceSalary,
          overtimePay,
          otherBonus,
          totalSalary,
          socialInsurance,
          housingFund,
          incomeTax,
          otherDeduction,
          totalDeduction,
          actualSalary,
          status: payslipStatus,
          createTime: `2023-${month.split('-')[1]}-05 09:30:00`,
          distributeTime
        },
        message: '获取工资单详情成功'
      };
    }
  },
  
  // 生成工资单
  {
    url: '/api/payslip/generate',
    method: 'post',
    response: ({ body }) => {
      const { month, department } = body;
      
      if (!month) {
        return {
          code: 400,
          message: '月份不能为空'
        };
      }
      
      return {
        code: 200,
        data: {
          month,
          department,
          generateCount: department ? 10 : 50, // 如果指定部门，则生成10条，否则生成50条
          generateTime: new Date().toISOString()
        },
        message: '工资单生成成功'
      };
    }
  },
  
  // 发放工资单（批量）
  {
    url: '/api/payslip/distribute',
    method: 'post',
    response: ({ body }) => {
      const { month, department } = body;
      
      if (!month) {
        return {
          code: 400,
          message: '月份不能为空'
        };
      }
      
      return {
        code: 200,
        data: {
          month,
          department,
          distributeCount: department ? 10 : 50,
          distributeTime: new Date().toISOString()
        },
        message: '工资单发放成功'
      };
    }
  },
  
  // 发放单个工资单
  {
    url: '/api/payslip/distribute-one',
    method: 'post',
    response: ({ body }) => {
      const { id, month } = body;
      
      if (!id || !month) {
        return {
          code: 400,
          message: '缺少必要参数'
        };
      }
      
      return {
        code: 200,
        data: {
          id,
          month,
          status: 'distributed',
          distributeTime: new Date().toISOString()
        },
        message: '工资单发放成功'
      };
    }
  }
]; 