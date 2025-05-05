export default [
  {
    url: '/api/salary/budget/department',
    method: 'get',
    response: ({ query }) => {
      const { year, quarter, department, page = 1, pageSize = 10 } = query
      
      // 生成模拟部门预算数据
      const mockDepartmentBudgets = [
        {
          id: 'DB001',
          department: '技术部',
          year: '2023',
          totalBudget: 9600000,
          allocatedBudget: 9350000,
          remainingBudget: 250000,
          quarterlyBudget: {
            Q1: 2400000,
            Q2: 2400000,
            Q3: 2400000,
            Q4: 2400000
          },
          actualExpenditure: {
            Q1: 2350000,
            Q2: 2380000,
            Q3: 2320000,
            Q4: 2070000
          },
          headcount: {
            planned: 40,
            actual: 38
          },
          avgSalary: 18250,
          status: '执行中',
          lastUpdated: '2023-12-10'
        },
        {
          id: 'DB002',
          department: '销售部',
          year: '2023',
          totalBudget: 7200000,
          allocatedBudget: 7150000,
          remainingBudget: 50000,
          quarterlyBudget: {
            Q1: 1800000,
            Q2: 1800000,
            Q3: 1800000,
            Q4: 1800000
          },
          actualExpenditure: {
            Q1: 1780000,
            Q2: 1810000,
            Q3: 1790000,
            Q4: 1770000
          },
          headcount: {
            planned: 30,
            actual: 31
          },
          avgSalary: 19220,
          status: '执行中',
          lastUpdated: '2023-12-08'
        },
        {
          id: 'DB003',
          department: '市场部',
          year: '2023',
          totalBudget: 4800000,
          allocatedBudget: 4750000,
          remainingBudget: 50000,
          quarterlyBudget: {
            Q1: 1200000,
            Q2: 1200000,
            Q3: 1200000,
            Q4: 1200000
          },
          actualExpenditure: {
            Q1: 1180000,
            Q2: 1210000,
            Q3: 1190000,
            Q4: 1170000
          },
          headcount: {
            planned: 20,
            actual: 19
          },
          avgSalary: 18560,
          status: '执行中',
          lastUpdated: '2023-12-05'
        },
        {
          id: 'DB004',
          department: '财务部',
          year: '2023',
          totalBudget: 3600000,
          allocatedBudget: 3550000,
          remainingBudget: 50000,
          quarterlyBudget: {
            Q1: 900000,
            Q2: 900000,
            Q3: 900000,
            Q4: 900000
          },
          actualExpenditure: {
            Q1: 880000,
            Q2: 895000,
            Q3: 890000,
            Q4: 885000
          },
          headcount: {
            planned: 15,
            actual: 15
          },
          avgSalary: 17750,
          status: '执行中',
          lastUpdated: '2023-12-07'
        },
        {
          id: 'DB005',
          department: '人力资源部',
          year: '2023',
          totalBudget: 2400000,
          allocatedBudget: 2380000,
          remainingBudget: 20000,
          quarterlyBudget: {
            Q1: 600000,
            Q2: 600000,
            Q3: 600000,
            Q4: 600000
          },
          actualExpenditure: {
            Q1: 590000,
            Q2: 595000,
            Q3: 600000,
            Q4: 595000
          },
          headcount: {
            planned: 10,
            actual: 10
          },
          avgSalary: 17250,
          status: '执行中',
          lastUpdated: '2023-12-09'
        },
        {
          id: 'DB006',
          department: '技术部',
          year: '2024',
          totalBudget: 10800000,
          allocatedBudget: 0,
          remainingBudget: 10800000,
          quarterlyBudget: {
            Q1: 2700000,
            Q2: 2700000,
            Q3: 2700000,
            Q4: 2700000
          },
          actualExpenditure: {
            Q1: 0,
            Q2: 0,
            Q3: 0,
            Q4: 0
          },
          headcount: {
            planned: 45,
            actual: 0
          },
          avgSalary: 20000,
          status: '计划中',
          lastUpdated: '2023-11-15'
        },
        {
          id: 'DB007',
          department: '销售部',
          year: '2024',
          totalBudget: 8400000,
          allocatedBudget: 0,
          remainingBudget: 8400000,
          quarterlyBudget: {
            Q1: 2100000,
            Q2: 2100000,
            Q3: 2100000,
            Q4: 2100000
          },
          actualExpenditure: {
            Q1: 0,
            Q2: 0,
            Q3: 0,
            Q4: 0
          },
          headcount: {
            planned: 35,
            actual: 0
          },
          avgSalary: 20000,
          status: '计划中',
          lastUpdated: '2023-11-20'
        }
      ]
      
      // 根据条件过滤
      let filteredBudgets = [...mockDepartmentBudgets]
      
      if (year) {
        filteredBudgets = filteredBudgets.filter(budget => budget.year === year)
      }
      
      if (department) {
        filteredBudgets = filteredBudgets.filter(budget => budget.department === department)
      }
      
      if (quarter) {
        // 筛选季度只是为了查看数据，不改变结果集
        // 后端实际只会返回有该季度数据的部门预算
      }
      
      // 计算分页
      const total = filteredBudgets.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedBudgets = filteredBudgets.slice(startIndex, endIndex)
      
      // 计算汇总数据
      const summary = {
        totalBudget: filteredBudgets.reduce((sum, budget) => sum + budget.totalBudget, 0),
        allocatedBudget: filteredBudgets.reduce((sum, budget) => sum + budget.allocatedBudget, 0),
        remainingBudget: filteredBudgets.reduce((sum, budget) => sum + budget.remainingBudget, 0),
        totalHeadcount: filteredBudgets.reduce((sum, budget) => sum + budget.headcount.planned, 0),
        actualHeadcount: filteredBudgets.reduce((sum, budget) => sum + budget.headcount.actual, 0)
      }
      
      return {
        code: 200,
        data: {
          budgets: paginatedBudgets,
          total,
          summary,
          departments: ['技术部', '销售部', '市场部', '财务部', '人力资源部'],
          years: ['2024', '2023', '2022']
        },
        message: '获取部门预算数据成功'
      }
    }
  },
  {
    url: '/api/salary/budget/forecast',
    method: 'post',
    response: ({ body }) => {
      const { year, department, growthRate, headcountChange, bonusRatio } = body
      
      if (!year || !department) {
        return {
          code: 400,
          message: '年份和部门不能为空'
        }
      }
      
      // 模拟基于参数生成预测数据
      const baseBudget = department === '技术部' ? 9600000 : 
                         department === '销售部' ? 7200000 : 
                         department === '市场部' ? 4800000 : 
                         department === '财务部' ? 3600000 : 2400000
      
      const headcount = department === '技术部' ? 40 : 
                        department === '销售部' ? 30 : 
                        department === '市场部' ? 20 : 
                        department === '财务部' ? 15 : 10
      
      const actualGrowthRate = growthRate || 0.05
      const actualHeadcountChange = headcountChange || 2
      const actualBonusRatio = bonusRatio || 0.12
      
      const forecastBudget = Math.round(baseBudget * (1 + actualGrowthRate))
      const forecastHeadcount = headcount + actualHeadcountChange
      const avgSalary = Math.round(forecastBudget / forecastHeadcount / 12)
      const bonusBudget = Math.round(forecastBudget * actualBonusRatio)
      
      const quarterlyBudget = Math.round(forecastBudget / 4)
      
      return {
        code: 200,
        data: {
          year,
          department,
          forecastBudget,
          baseBudget,
          growthAmount: forecastBudget - baseBudget,
          growthRate: actualGrowthRate,
          forecastHeadcount,
          avgSalary,
          bonusBudget,
          quarterlyBudget: {
            Q1: quarterlyBudget,
            Q2: quarterlyBudget,
            Q3: quarterlyBudget,
            Q4: quarterlyBudget
          },
          assumptions: {
            salaryGrowth: actualGrowthRate,
            headcountChange: actualHeadcountChange,
            bonusRatio: actualBonusRatio
          },
          generatedTime: new Date().toISOString()
        },
        message: '生成薪资预算预测成功'
      }
    }
  },
  {
    url: '/api/salary/budget/adjustment-history',
    method: 'get',
    response: ({ query }) => {
      const { department, year, page = 1, pageSize = 10 } = query
      
      // 生成模拟预算调整历史数据
      const mockAdjustments = [
        {
          id: 'ADJ001',
          department: '技术部',
          year: '2023',
          adjustmentDate: '2023-03-15',
          adjustmentType: '增加预算',
          originalBudget: 9200000,
          adjustmentAmount: 400000,
          newBudget: 9600000,
          reason: '新增项目需要增加2名高级工程师',
          approvedBy: 'CEO',
          requestedBy: '技术总监',
          status: '已批准',
          comments: '已调整Q2-Q4人力预算'
        },
        {
          id: 'ADJ002',
          department: '销售部',
          year: '2023',
          adjustmentDate: '2023-05-10',
          adjustmentType: '增加预算',
          originalBudget: 7000000,
          adjustmentAmount: 200000,
          newBudget: 7200000,
          reason: '拓展新市场需要增加销售人员',
          approvedBy: 'CEO',
          requestedBy: '销售总监',
          status: '已批准',
          comments: '已调整Q3-Q4人力预算'
        },
        {
          id: 'ADJ003',
          department: '市场部',
          year: '2023',
          adjustmentDate: '2023-04-20',
          adjustmentType: '减少预算',
          originalBudget: 5000000,
          adjustmentAmount: -200000,
          newBudget: 4800000,
          reason: '市场活动规模缩减',
          approvedBy: 'CFO',
          requestedBy: '市场总监',
          status: '已批准',
          comments: '调整已反映在Q2-Q4预算'
        },
        {
          id: 'ADJ004',
          department: '人力资源部',
          year: '2023',
          adjustmentDate: '2023-02-25',
          adjustmentType: '增加预算',
          originalBudget: 2300000,
          adjustmentAmount: 100000,
          newBudget: 2400000,
          reason: '增加招聘人员满足扩张需求',
          approvedBy: 'CEO',
          requestedBy: 'HR总监',
          status: '已批准',
          comments: '新增1名资深招聘专员'
        },
        {
          id: 'ADJ005',
          department: '技术部',
          year: '2023',
          adjustmentDate: '2023-09-05',
          adjustmentType: '调整分布',
          originalBudget: 9600000,
          adjustmentAmount: 0,
          newBudget: 9600000,
          reason: '调整Q3和Q4预算分配比例',
          approvedBy: 'CFO',
          requestedBy: '技术总监',
          status: '已批准',
          comments: 'Q3减少10万，Q4增加10万'
        },
        {
          id: 'ADJ006',
          department: '技术部',
          year: '2024',
          adjustmentDate: '2023-11-15',
          adjustmentType: '初始预算',
          originalBudget: 0,
          adjustmentAmount: 10800000,
          newBudget: 10800000,
          reason: '2024年度初始预算制定',
          approvedBy: '董事会',
          requestedBy: 'CFO',
          status: '已批准',
          comments: '包含5名新增技术人员预算'
        }
      ]
      
      // 根据条件过滤
      let filteredAdjustments = [...mockAdjustments]
      
      if (department) {
        filteredAdjustments = filteredAdjustments.filter(adj => adj.department === department)
      }
      
      if (year) {
        filteredAdjustments = filteredAdjustments.filter(adj => adj.year === year)
      }
      
      // 按日期倒序排序
      filteredAdjustments.sort((a, b) => new Date(b.adjustmentDate) - new Date(a.adjustmentDate))
      
      // 计算分页
      const total = filteredAdjustments.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedAdjustments = filteredAdjustments.slice(startIndex, endIndex)
      
      return {
        code: 200,
        data: {
          adjustments: paginatedAdjustments,
          total,
          departments: ['技术部', '销售部', '市场部', '财务部', '人力资源部'],
          years: ['2024', '2023', '2022'],
          adjustmentTypes: ['增加预算', '减少预算', '调整分布', '初始预算']
        },
        message: '获取预算调整历史数据成功'
      }
    }
  },
  {
    url: '/api/salary/budget/execution-analysis',
    method: 'get',
    response: ({ query }) => {
      const { year = '2023', quarter } = query
      
      // 生成模拟执行分析数据
      const departmentAnalysis = [
        {
          department: '技术部',
          planned: 9600000,
          actual: 9120000,
          variance: -480000,
          variancePercent: -5,
          quarterlyAnalysis: {
            Q1: { planned: 2400000, actual: 2350000, variance: -50000, variancePercent: -2.08 },
            Q2: { planned: 2400000, actual: 2380000, variance: -20000, variancePercent: -0.83 },
            Q3: { planned: 2400000, actual: 2320000, variance: -80000, variancePercent: -3.33 },
            Q4: { planned: 2400000, actual: 2070000, variance: -330000, variancePercent: -13.75 }
          },
          headcount: { planned: 40, actual: 38, variance: -2 },
          comments: 'Q4有两个职位未及时招聘到合适人选'
        },
        {
          department: '销售部',
          planned: 7200000,
          actual: 7150000,
          variance: -50000,
          variancePercent: -0.69,
          quarterlyAnalysis: {
            Q1: { planned: 1800000, actual: 1780000, variance: -20000, variancePercent: -1.11 },
            Q2: { planned: 1800000, actual: 1810000, variance: 10000, variancePercent: 0.56 },
            Q3: { planned: 1800000, actual: 1790000, variance: -10000, variancePercent: -0.56 },
            Q4: { planned: 1800000, actual: 1770000, variance: -30000, variancePercent: -1.67 }
          },
          headcount: { planned: 30, actual: 31, variance: 1 },
          comments: '销售部预算执行控制良好'
        },
        {
          department: '市场部',
          planned: 4800000,
          actual: 4750000,
          variance: -50000,
          variancePercent: -1.04,
          quarterlyAnalysis: {
            Q1: { planned: 1200000, actual: 1180000, variance: -20000, variancePercent: -1.67 },
            Q2: { planned: 1200000, actual: 1210000, variance: 10000, variancePercent: 0.83 },
            Q3: { planned: 1200000, actual: 1190000, variance: -10000, variancePercent: -0.83 },
            Q4: { planned: 1200000, actual: 1170000, variance: -30000, variancePercent: -2.50 }
          },
          headcount: { planned: 20, actual: 19, variance: -1 },
          comments: '市场部人员缺一名专员，节约部分人力成本'
        },
        {
          department: '财务部',
          planned: 3600000,
          actual: 3550000,
          variance: -50000,
          variancePercent: -1.39,
          quarterlyAnalysis: {
            Q1: { planned: 900000, actual: 880000, variance: -20000, variancePercent: -2.22 },
            Q2: { planned: 900000, actual: 895000, variance: -5000, variancePercent: -0.56 },
            Q3: { planned: 900000, actual: 890000, variance: -10000, variancePercent: -1.11 },
            Q4: { planned: 900000, actual: 885000, variance: -15000, variancePercent: -1.67 }
          },
          headcount: { planned: 15, actual: 15, variance: 0 },
          comments: '预算执行控制良好'
        },
        {
          department: '人力资源部',
          planned: 2400000,
          actual: 2380000,
          variance: -20000,
          variancePercent: -0.83,
          quarterlyAnalysis: {
            Q1: { planned: 600000, actual: 590000, variance: -10000, variancePercent: -1.67 },
            Q2: { planned: 600000, actual: 595000, variance: -5000, variancePercent: -0.83 },
            Q3: { planned: 600000, actual: 600000, variance: 0, variancePercent: 0 },
            Q4: { planned: 600000, actual: 595000, variance: -5000, variancePercent: -0.83 }
          },
          headcount: { planned: 10, actual: 10, variance: 0 },
          comments: '预算执行控制良好'
        }
      ]
      
      // 如果指定了季度，只返回该季度的分析
      if (quarter) {
        const quarterKey = `Q${quarter}`
        departmentAnalysis.forEach(dept => {
          dept.quarterly = dept.quarterlyAnalysis[quarterKey]
          // 删除全部季度数据以简化响应
          delete dept.quarterlyAnalysis
        })
      }
      
      // 计算总体汇总
      const totalPlanned = departmentAnalysis.reduce((sum, dept) => sum + dept.planned, 0)
      const totalActual = departmentAnalysis.reduce((sum, dept) => sum + dept.actual, 0)
      const totalVariance = totalActual - totalPlanned
      const totalVariancePercent = Math.round(totalVariance / totalPlanned * 100 * 100) / 100
      
      return {
        code: 200,
        data: {
          year,
          quarter: quarter ? `Q${quarter}` : '年度',
          departments: departmentAnalysis,
          summary: {
            totalPlanned,
            totalActual,
            totalVariance,
            totalVariancePercent,
            headcountPlanned: departmentAnalysis.reduce((sum, dept) => sum + dept.headcount.planned, 0),
            headcountActual: departmentAnalysis.reduce((sum, dept) => sum + dept.headcount.actual, 0)
          },
          analysisDate: new Date().toISOString()
        },
        message: '获取预算执行分析数据成功'
      }
    }
  }
] 