export default [
  // 获取员工薪资档案列表
  {
    url: '/api/salary/employees',
    method: 'get',
    response: ({ query }) => {
      const { keyword, department, page = 1, pageSize = 10 } = query;
      
      // 生成模拟员工数据
      const totalCount = 100;
      let employees = [];
      
      // 简化的部门数据
      const departments = ['技术部', '人力资源部', '财务部', '市场部', '销售部'];
      
      // 生成基础数据
      for (let i = 1; i <= totalCount; i++) {
        const deptIndex = i % departments.length;
        employees.push({
          id: `E${String(i).padStart(5, '0')}`,
          name: `员工${i}`,
          department: departments[deptIndex],
          position: `${departments[deptIndex]}专员`,
          hireDate: `2023-${String(i % 12 + 1).padStart(2, '0')}-${String(i % 28 + 1).padStart(2, '0')}`,
          baseSalary: 8000 + (i % 10) * 500,
          performanceBonus: 2000 + (i % 5) * 500,
          socialInsurance: 10000.00
        });
      }
      
      // 根据关键词筛选
      if (keyword) {
        const kw = keyword.toLowerCase();
        employees = employees.filter(emp => 
          emp.name.toLowerCase().includes(kw) || 
          emp.id.toLowerCase().includes(kw)
        );
      }
      
      // 根据部门筛选
      if (department) {
        employees = employees.filter(emp => emp.department === department);
      }
      
      // 计算分页
      const startIndex = (page - 1) * pageSize;
      const paginatedData = employees.slice(startIndex, startIndex + parseInt(pageSize));
      
      return {
        code: 200,
        data: {
          employees: paginatedData,
          total: employees.length
        },
        message: '获取员工数据成功'
      };
    }
  },
  
  // 更新员工薪资档案
  {
    url: '/api/salary/employee/update',
    method: 'post',
    response: ({ body }) => {
      const { id, changeReason } = body;
      
      // 验证必填字段
      if (!changeReason) {
        return {
          code: 400,
          message: '变更原因不能为空'
        };
      }
      
      return {
        code: 200,
        data: {
          id,
          timestamp: new Date().getTime()
        },
        message: '员工薪资档案更新成功'
      };
    }
  },
  
  // 获取员工薪资变更历史
  {
    url: '/api/salary/employee/history',
    method: 'get',
    response: ({ query }) => {
      const { id } = query;
      
      // 模拟历史数据
      const history = [
        {
          changeDate: '2023-11-01 14:30:25',
          changes: {
            baseSalary: { from: 8000, to: 8500 },
            performanceBonus: { from: 2000, to: 2500 }
          },
          reason: '年度调薪',
          operator: 'HR专员张三'
        },
        {
          changeDate: '2023-05-12 10:15:42',
          changes: {
            position: { from: '技术部初级工程师', to: '技术部中级工程师' },
            baseSalary: { from: 7000, to: 8000 }
          },
          reason: '职位晋升',
          operator: 'HR专员李四'
        }
      ];
      
      return {
        code: 200,
        data: history,
        message: '获取历史数据成功'
      };
    }
  },
  
  // 获取部门列表
  {
    url: '/api/departments',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: [
          { value: '技术部', label: '技术部' },
          { value: '人力资源部', label: '人力资源部' },
          { value: '财务部', label: '财务部' },
          { value: '市场部', label: '市场部' },
          { value: '销售部', label: '销售部' }
        ],
        message: '获取部门列表成功'
      };
    }
  }
]; 