export default [
  {
    url: '/api/salary/reports/employee-trends',
    method: 'get',
    response: ({ query }) => {
      const { employeeId, startDate, endDate } = query
      
      if (!employeeId) {
        return {
          code: 400,
          message: '员工ID不能为空'
        }
      }
      
      // 基本薪资数据
      const baseData = {
        employeeId,
        employeeName: '员工' + employeeId.replace(/\D/g, ''),
        department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][Math.floor(Math.random() * 5)],
        joinDate: '2020-06-01'
      }
      
      // 生成模拟薪资趋势数据
      const salaryRecords = []
      const startYear = startDate ? parseInt(startDate.substring(0, 4)) : 2020
      const startMonth = startDate ? parseInt(startDate.substring(5, 7)) : 6
      const endYear = endDate ? parseInt(endDate.substring(0, 4)) : 2023
      const endMonth = endDate ? parseInt(endDate.substring(5, 7)) : 12
      
      let baseSalary = 8000 + Math.floor(Math.random() * 4000)
      let currentYear = startYear
      let currentMonth = startMonth
      
      while (currentYear < endYear || (currentYear === endYear && currentMonth <= endMonth)) {
        // 每年4月和10月可能有加薪
        if ((currentMonth === 4 || currentMonth === 10) && Math.random() > 0.4) {
          baseSalary = Math.round(baseSalary * (1 + 0.05 + Math.random() * 0.1))
        }
        
        const performanceBonus = Math.round(baseSalary * (0.1 + Math.random() * 0.2) * 100) / 100
        const totalSalary = Math.round((baseSalary + performanceBonus) * 100) / 100
        
        salaryRecords.push({
          yearMonth: `${currentYear}-${String(currentMonth).padStart(2, '0')}`,
          baseSalary,
          performanceBonus,
          overtimePay: Math.round(Math.random() * 1000 * 100) / 100,
          totalSalary,
          taxAmount: Math.round(totalSalary * 0.08 * 100) / 100,
          netSalary: Math.round(totalSalary * 0.92 * 100) / 100,
          changeReason: (currentMonth === 4 || currentMonth === 10) && Math.random() > 0.4 ? 
            ['绩效加薪', '职位晋升', '定期调整', '技能提升'][Math.floor(Math.random() * 4)] : ''
        })
        
        currentMonth++
        if (currentMonth > 12) {
          currentMonth = 1
          currentYear++
        }
      }
      
      // 计算统计数据
      const growthRate = (salaryRecords[salaryRecords.length - 1].baseSalary / salaryRecords[0].baseSalary - 1) * 100
      
      return {
        code: 200,
        data: {
          employeeInfo: baseData,
          salaryRecords,
          statistics: {
            initialSalary: salaryRecords[0].baseSalary,
            currentSalary: salaryRecords[salaryRecords.length - 1].baseSalary,
            growthRate: Math.round(growthRate * 100) / 100,
            growthAmount: salaryRecords[salaryRecords.length - 1].baseSalary - salaryRecords[0].baseSalary,
            avgAnnualIncrease: Math.round(growthRate / (salaryRecords.length / 12) * 100) / 100,
            salaryChangeCount: salaryRecords.filter(r => r.changeReason).length
          }
        },
        message: '获取员工薪资趋势数据成功'
      }
    }
  },
  {
    url: '/api/salary/reports/department-comparison',
    method: 'get',
    response: ({ query }) => {
      const { year = '2023', quarter } = query
      
      // 生成模拟部门薪资对比数据
      const departmentData = [
        {
          department: '技术部',
          headcount: 38,
          totalSalary: 912000,
          averageSalary: 24000,
          medianSalary: 22500,
          minSalary: 12000,
          maxSalary: 45000,
          salaryRatio: 0.34,
          quarterlyData: {
            Q1: { averageSalary: 23500, headcount: 37 },
            Q2: { averageSalary: 23800, headcount: 38 },
            Q3: { averageSalary: 24000, headcount: 38 },
            Q4: { averageSalary: 24200, headcount: 38 }
          }
        },
        {
          department: '销售部',
          headcount: 31,
          totalSalary: 682000,
          averageSalary: 22000,
          medianSalary: 21000,
          minSalary: 10000,
          maxSalary: 40000,
          salaryRatio: 0.26,
          quarterlyData: {
            Q1: { averageSalary: 21500, headcount: 30 },
            Q2: { averageSalary: 21700, headcount: 30 },
            Q3: { averageSalary: 22000, headcount: 31 },
            Q4: { averageSalary: 22200, headcount: 31 }
          }
        },
        {
          department: '市场部',
          headcount: 19,
          totalSalary: 380000,
          averageSalary: 20000,
          medianSalary: 19000,
          minSalary: 11000,
          maxSalary: 35000,
          salaryRatio: 0.14,
          quarterlyData: {
            Q1: { averageSalary: 19500, headcount: 19 },
            Q2: { averageSalary: 19800, headcount: 19 },
            Q3: { averageSalary: 20000, headcount: 19 },
            Q4: { averageSalary: 20200, headcount: 19 }
          }
        },
        {
          department: '财务部',
          headcount: 15,
          totalSalary: 315000,
          averageSalary: 21000,
          medianSalary: 20000,
          minSalary: 12000,
          maxSalary: 38000,
          salaryRatio: 0.12,
          quarterlyData: {
            Q1: { averageSalary: 20500, headcount: 15 },
            Q2: { averageSalary: 20800, headcount: 15 },
            Q3: { averageSalary: 21000, headcount: 15 },
            Q4: { averageSalary: 21200, headcount: 15 }
          }
        },
        {
          department: '人力资源部',
          headcount: 10,
          totalSalary: 190000,
          averageSalary: 19000,
          medianSalary: 18000,
          minSalary: 10000,
          maxSalary: 35000,
          salaryRatio: 0.07,
          quarterlyData: {
            Q1: { averageSalary: 18500, headcount: 10 },
            Q2: { averageSalary: 18800, headcount: 10 },
            Q3: { averageSalary: 19000, headcount: 10 },
            Q4: { averageSalary: 19200, headcount: 10 }
          }
        }
      ]
      
      // 如果指定了季度，只返回该季度的数据
      if (quarter) {
        const quarterKey = `Q${quarter}`
        departmentData.forEach(dept => {
          dept.averageSalary = dept.quarterlyData[quarterKey].averageSalary
          dept.headcount = dept.quarterlyData[quarterKey].headcount
          // 删除季度数据以简化响应
          delete dept.quarterlyData
        })
      }
      
      // 计算公司整体数据
      const companyData = {
        totalHeadcount: departmentData.reduce((sum, dept) => sum + dept.headcount, 0),
        totalSalary: departmentData.reduce((sum, dept) => sum + dept.totalSalary, 0),
        averageSalary: Math.round(departmentData.reduce((sum, dept) => sum + dept.totalSalary, 0) / 
                      departmentData.reduce((sum, dept) => sum + dept.headcount, 0)),
        medianSalary: Math.round(departmentData.reduce((sum, dept) => sum + dept.medianSalary * dept.headcount, 0) / 
                     departmentData.reduce((sum, dept) => sum + dept.headcount, 0)),
        maxDepartmentSalary: Math.max(...departmentData.map(dept => dept.averageSalary)),
        minDepartmentSalary: Math.min(...departmentData.map(dept => dept.averageSalary)),
        salaryGap: Math.max(...departmentData.map(dept => dept.averageSalary)) - 
                  Math.min(...departmentData.map(dept => dept.averageSalary))
      }
      
      return {
        code: 200,
        data: {
          year,
          quarter: quarter ? `Q${quarter}` : '年度',
          departments: departmentData,
          company: companyData
        },
        message: '获取部门薪资对比数据成功'
      }
    }
  },
  {
    url: '/api/salary/reports/salary-distribution',
    method: 'get',
    response: ({ query }) => {
      const { department, year = '2023' } = query
      
      // 生成模拟薪资分布数据
      const salaryRanges = [
        { range: '5000以下', count: 3, percentage: 2 },
        { range: '5000-8000', count: 8, percentage: 7 },
        { range: '8000-10000', count: 15, percentage: 13 },
        { range: '10000-15000', count: 25, percentage: 22 },
        { range: '15000-20000', count: 32, percentage: 28 },
        { range: '20000-25000', count: 18, percentage: 16 },
        { range: '25000-30000', count: 10, percentage: 9 },
        { range: '30000-40000', count: 3, percentage: 2 },
        { range: '40000以上', count: 1, percentage: 1 }
      ]
      
      // 生成职级分布数据
      const gradeLevels = [
        { grade: '1级', count: 20, avgSalary: 8500 },
        { grade: '2级', count: 35, avgSalary: 12000 },
        { grade: '3级', count: 28, avgSalary: 18000 },
        { grade: '4级', count: 17, avgSalary: 25000 },
        { grade: '5级', count: 10, avgSalary: 35000 },
        { grade: '6级', count: 5, avgSalary: 45000 }
      ]
      
      // 按部门过滤 (如果指定了部门)
      if (department) {
        // 调整不同部门的数据分布
        if (department === '技术部') {
          salaryRanges[3].count += 5
          salaryRanges[4].count += 3
          salaryRanges[1].count -= 3
          salaryRanges[2].count -= 5
        } else if (department === '销售部') {
          salaryRanges[4].count += 2
          salaryRanges[5].count += 3
          salaryRanges[2].count -= 2
          salaryRanges[3].count -= 3
        }
        
        // 重新计算百分比
        const total = salaryRanges.reduce((sum, range) => sum + range.count, 0)
        salaryRanges.forEach(range => {
          range.percentage = Math.round(range.count / total * 100)
        })
      }
      
      return {
        code: 200,
        data: {
          year,
          department: department || '全公司',
          salaryRanges,
          gradeLevels,
          statistics: {
            totalCount: salaryRanges.reduce((sum, range) => sum + range.count, 0),
            avgSalary: 18500,
            medianSalary: 17800,
            salaryStandardDeviation: 6500,
            top10PercentAvg: 38500,
            bottom10PercentAvg: 6800
          },
          salaryFactors: [
            { factor: '职级', influence: 65 },
            { factor: '工作年限', influence: 15 },
            { factor: '绩效', influence: 12 },
            { factor: '技能认证', influence: 5 },
            { factor: '其他', influence: 3 }
          ]
        },
        message: '获取薪资分布数据成功'
      }
    }
  },
  {
    url: '/api/salary/reports/raise-analysis',
    method: 'get',
    response: ({ query }) => {
      const { year = '2023', department } = query
      
      // 生成模拟加薪分析数据
      const raiseData = {
        overallStatistics: {
          totalEmployees: 115,
          employeesWithRaise: 78,
          raisePercentage: 67.8,
          avgRaiseAmount: 1580,
          avgRaiseRatio: 8.2,
          totalRaiseAmount: 123240
        },
        departmentRaiseData: [
          {
            department: '技术部',
            totalEmployees: 38,
            employeesWithRaise: 28,
            raisePercentage: 73.7,
            avgRaiseAmount: 1850,
            avgRaiseRatio: 7.8,
            totalRaiseAmount: 51800
          },
          {
            department: '销售部',
            totalEmployees: 31,
            employeesWithRaise: 22,
            raisePercentage: 71.0,
            avgRaiseAmount: 1620,
            avgRaiseRatio: 7.5,
            totalRaiseAmount: 35640
          },
          {
            department: '市场部',
            totalEmployees: 19,
            employeesWithRaise: 12,
            raisePercentage: 63.2,
            avgRaiseAmount: 1350,
            avgRaiseRatio: 6.9,
            totalRaiseAmount: 16200
          },
          {
            department: '财务部',
            totalEmployees: 15,
            employeesWithRaise: 9,
            raisePercentage: 60.0,
            avgRaiseAmount: 1420,
            avgRaiseRatio: 6.8,
            totalRaiseAmount: 12780
          },
          {
            department: '人力资源部',
            totalEmployees: 12,
            employeesWithRaise: 7,
            raisePercentage: 58.3,
            avgRaiseAmount: 1260,
            avgRaiseRatio: 6.7,
            totalRaiseAmount: 8820
          }
        ],
        raiseReasons: [
          { reason: '绩效奖励', count: 35, percentage: 44.9 },
          { reason: '定期调薪', count: 20, percentage: 25.6 },
          { reason: '职位晋升', count: 15, percentage: 19.2 },
          { reason: '技能提升', count: 5, percentage: 6.4 },
          { reason: '特殊贡献', count: 3, percentage: 3.9 }
        ],
        raiseRangeDistribution: [
          { range: '5%以下', count: 18, percentage: 23.1 },
          { range: '5%-10%', count: 32, percentage: 41.0 },
          { range: '10%-15%', count: 20, percentage: 25.6 },
          { range: '15%-20%', count: 5, percentage: 6.4 },
          { range: '20%以上', count: 3, percentage: 3.9 }
        ]
      }
      
      // 如果指定了部门，只返回该部门的数据
      if (department) {
        const deptData = raiseData.departmentRaiseData.find(dept => dept.department === department)
        if (deptData) {
          return {
            code: 200,
            data: {
              year,
              department,
              departmentStatistics: deptData,
              raiseReasons: raiseData.raiseReasons,
              raiseRangeDistribution: raiseData.raiseRangeDistribution
            },
            message: '获取部门加薪分析数据成功'
          }
        } else {
          return {
            code: 400,
            message: '未找到该部门的加薪数据'
          }
        }
      }
      
      return {
        code: 200,
        data: {
          year,
          ...raiseData
        },
        message: '获取加薪分析数据成功'
      }
    }
  },
  {
    url: '/api/salary/reports/compensation-summary',
    method: 'get',
    response: ({ query }) => {
      const { year = '2023', type = 'monthly' } = query
      
      // 生成模拟综合薪酬报表数据
      const monthlySummary = []
      
      for (let month = 1; month <= 12; month++) {
        const baseSalary = 1800000 + Math.round(Math.random() * 50000)
        const performanceBonus = 450000 + Math.round(Math.random() * 100000)
        const overtimePay = 120000 + Math.round(Math.random() * 30000)
        const totalSalary = baseSalary + performanceBonus + overtimePay
        
        monthlySummary.push({
          month: `${year}-${String(month).padStart(2, '0')}`,
          employeeCount: 115 - (month === 1 ? 5 : (month === 12 ? 2 : 0)),
          baseSalary,
          performanceBonus,
          overtimePay,
          otherAllowance: 80000 + Math.round(Math.random() * 20000),
          totalSalary,
          socialInsurance: Math.round(totalSalary * 0.12),
          taxAmount: Math.round(totalSalary * 0.08),
          netPayout: Math.round(totalSalary * 0.8)
        })
      }
      
      // 生成季度和年度汇总
      const quarterlySummary = []
      for (let quarter = 1; quarter <= 4; quarter++) {
        const quarterMonths = monthlySummary.slice((quarter - 1) * 3, quarter * 3)
        quarterlySummary.push({
          quarter: `Q${quarter}`,
          employeeCount: Math.max(...quarterMonths.map(m => m.employeeCount)),
          baseSalary: quarterMonths.reduce((sum, m) => sum + m.baseSalary, 0),
          performanceBonus: quarterMonths.reduce((sum, m) => sum + m.performanceBonus, 0),
          overtimePay: quarterMonths.reduce((sum, m) => sum + m.overtimePay, 0),
          otherAllowance: quarterMonths.reduce((sum, m) => sum + m.otherAllowance, 0),
          totalSalary: quarterMonths.reduce((sum, m) => sum + m.totalSalary, 0),
          socialInsurance: quarterMonths.reduce((sum, m) => sum + m.socialInsurance, 0),
          taxAmount: quarterMonths.reduce((sum, m) => sum + m.taxAmount, 0),
          netPayout: quarterMonths.reduce((sum, m) => sum + m.netPayout, 0)
        })
      }
      
      const annualSummary = {
        year,
        employeeCount: Math.max(...monthlySummary.map(m => m.employeeCount)),
        baseSalary: monthlySummary.reduce((sum, m) => sum + m.baseSalary, 0),
        performanceBonus: monthlySummary.reduce((sum, m) => sum + m.performanceBonus, 0),
        overtimePay: monthlySummary.reduce((sum, m) => sum + m.overtimePay, 0),
        otherAllowance: monthlySummary.reduce((sum, m) => sum + m.otherAllowance, 0),
        totalSalary: monthlySummary.reduce((sum, m) => sum + m.totalSalary, 0),
        socialInsurance: monthlySummary.reduce((sum, m) => sum + m.socialInsurance, 0),
        taxAmount: monthlySummary.reduce((sum, m) => sum + m.taxAmount, 0),
        netPayout: monthlySummary.reduce((sum, m) => sum + m.netPayout, 0)
      }
      
      // 根据查询类型返回不同周期的数据
      const responseData = {
        year,
        annual: annualSummary
      }
      
      if (type === 'monthly' || type === 'all') {
        responseData.monthly = monthlySummary
      }
      
      if (type === 'quarterly' || type === 'all') {
        responseData.quarterly = quarterlySummary
      }
      
      return {
        code: 200,
        data: responseData,
        message: '获取综合薪酬报表数据成功'
      }
    }
  }
] 