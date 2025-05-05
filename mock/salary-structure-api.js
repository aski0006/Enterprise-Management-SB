export default [
  {
    url: '/api/salary/structure/grades',
    method: 'get',
    response: ({ query }) => {
      const { department, page = 1, pageSize = 10 } = query
      
      // 生成模拟薪资等级数据
      const mockGrades = []
      
      for (let i = 1; i <= 30; i++) {
        const grade = Math.ceil(i / 3)
        const level = (i % 3) || 3
        const minSalary = 5000 + 1000 * grade
        const maxSalary = minSalary + 2000 + (level * 500)
        
        mockGrades.push({
          id: `G${grade}L${level}`,
          gradeName: `${grade}级`,
          level: `${level}档`,
          title: `${grade}级${level}档`,
          department: ['全部部门', '技术部', '人力资源部', '财务部', '市场部', '销售部'][grade % 6],
          positionTypes: ['初级', '中级', '高级', '专家'][Math.min(grade - 1, 3)],
          minSalary,
          maxSalary,
          midSalary: Math.round((minSalary + maxSalary) / 2),
          baseSalaryRatio: Math.round(70 - grade) / 100,
          performanceRatio: Math.round(20 + grade) / 100,
          createdTime: '2023-01-01',
          updatedTime: '2023-06-15'
        })
      }
      
      // 根据部门过滤
      let filteredGrades = [...mockGrades]
      
      if (department && department !== '全部部门') {
        filteredGrades = filteredGrades.filter(grade => 
          grade.department === department || grade.department === '全部部门'
        )
      }
      
      // 计算分页
      const total = filteredGrades.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedGrades = filteredGrades.slice(startIndex, endIndex)
      
      return {
        code: 200,
        data: {
          grades: paginatedGrades,
          total,
          departments: ['全部部门', '技术部', '人力资源部', '财务部', '市场部', '销售部']
        },
        message: '获取薪资等级数据成功'
      }
    }
  },
  {
    url: '/api/salary/structure/grade/save',
    method: 'post',
    response: ({ body }) => {
      const { id, gradeName, level, department, minSalary, maxSalary } = body
      
      if (!gradeName || !level || !department) {
        return {
          code: 400,
          message: '等级名称、档位和部门不能为空'
        }
      }
      
      if (minSalary && maxSalary && Number(minSalary) > Number(maxSalary)) {
        return {
          code: 400,
          message: '最低薪资不能高于最高薪资'
        }
      }
      
      return {
        code: 200,
        data: {
          id: id || `G${Math.ceil(Math.random() * 10)}L${Math.ceil(Math.random() * 3)}`,
          ...body,
          midSalary: Math.round((Number(minSalary) + Number(maxSalary)) / 2),
          createdTime: id ? undefined : new Date().toISOString(),
          updatedTime: new Date().toISOString()
        },
        message: id ? '薪资等级更新成功' : '薪资等级创建成功'
      }
    }
  },
  {
    url: '/api/salary/structure/adjust-rules',
    method: 'get',
    response: ({ query }) => {
      const { type, page = 1, pageSize = 10 } = query
      
      // 生成模拟调薪规则数据
      const mockRules = [
        {
          id: 'AR001',
          ruleName: '年度调薪规则',
          ruleType: '定期调薪',
          applyTo: '全部员工',
          effectiveDate: '2023-01-01',
          expireDate: '2023-12-31',
          adjustRatio: {
            excellent: 0.15,
            good: 0.10,
            average: 0.05,
            poor: 0
          },
          approvalFlow: ['部门经理', '人力资源总监', '财务总监', 'CEO'],
          status: '生效中',
          createdBy: '管理员',
          createdTime: '2022-12-01',
          description: '根据年度绩效评估结果进行对应比例的调薪'
        },
        {
          id: 'AR002',
          ruleName: '技术部晋升调薪规则',
          ruleType: '晋升调薪',
          applyTo: '技术部',
          effectiveDate: '2023-02-01',
          expireDate: '2024-01-31',
          adjustRatio: {
            oneLevelUp: 0.20,
            twoLevelUp: 0.35,
            threeLevelUp: 0.50
          },
          approvalFlow: ['技术总监', '人力资源总监', 'CTO', 'CEO'],
          status: '生效中',
          createdBy: '管理员',
          createdTime: '2023-01-15',
          description: '根据晋升级别差距确定调薪比例'
        },
        {
          id: 'AR003',
          ruleName: '销售部业绩调薪规则',
          ruleType: '绩效调薪',
          applyTo: '销售部',
          effectiveDate: '2023-03-01',
          expireDate: '2023-12-31',
          adjustRatio: {
            overTarget200: 0.25,
            overTarget150: 0.15,
            overTarget120: 0.10,
            overTarget100: 0.05
          },
          approvalFlow: ['销售经理', '销售总监', '人力资源总监', 'CEO'],
          status: '生效中',
          createdBy: '管理员',
          createdTime: '2023-02-20',
          description: '根据销售业绩完成目标比例进行调薪'
        },
        {
          id: 'AR004',
          ruleName: '试用期转正调薪规则',
          ruleType: '转正调薪',
          applyTo: '全部员工',
          effectiveDate: '2023-01-01',
          expireDate: '2025-12-31',
          adjustRatio: {
            excellent: 0.20,
            good: 0.15,
            average: 0.10,
            poor: 0
          },
          approvalFlow: ['部门经理', '人力资源经理'],
          status: '生效中',
          createdBy: '管理员',
          createdTime: '2022-12-15',
          description: '根据试用期表现确定转正调薪比例'
        },
        {
          id: 'AR005',
          ruleName: '季度绩效调薪规则',
          ruleType: '绩效调薪',
          applyTo: '市场部',
          effectiveDate: '2023-04-01',
          expireDate: '2023-12-31',
          adjustRatio: {
            excellent: 0.08,
            good: 0.05,
            average: 0.02,
            poor: 0
          },
          approvalFlow: ['市场经理', '人力资源经理', '市场总监'],
          status: '草稿',
          createdBy: '管理员',
          createdTime: '2023-03-25',
          description: '根据季度绩效评估进行调薪'
        }
      ]
      
      // 根据类型过滤
      let filteredRules = [...mockRules]
      
      if (type) {
        filteredRules = filteredRules.filter(rule => rule.ruleType === type)
      }
      
      // 计算分页
      const total = filteredRules.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedRules = filteredRules.slice(startIndex, endIndex)
      
      return {
        code: 200,
        data: {
          rules: paginatedRules,
          total,
          ruleTypes: ['定期调薪', '晋升调薪', '绩效调薪', '转正调薪', '特殊调薪']
        },
        message: '获取调薪规则数据成功'
      }
    }
  },
  {
    url: '/api/salary/structure/bonus-schemes',
    method: 'get',
    response: ({ query }) => {
      const { department, year, page = 1, pageSize = 10 } = query
      
      // 生成模拟奖金方案数据
      const mockBonusSchemes = [
        {
          id: 'BS001',
          schemeName: '年终奖金方案',
          department: '全部部门',
          year: '2023',
          formulaType: '基础工资倍数',
          formulaDetail: {
            excellent: 4,
            good: 3,
            average: 2,
            poor: 1
          },
          totalBudget: 4500000,
          distributionMethod: '绩效比例分配',
          approvalStatus: '已批准',
          paymentDate: '2024-01-20',
          createdBy: '管理员',
          createdTime: '2023-11-15',
          description: '根据年度绩效评估结果确定年终奖金倍数'
        },
        {
          id: 'BS002',
          schemeName: '销售季度奖金方案',
          department: '销售部',
          year: '2023',
          formulaType: '销售业绩比例',
          formulaDetail: {
            overTarget150: 0.15,
            overTarget120: 0.10,
            overTarget100: 0.05,
            belowTarget: 0
          },
          totalBudget: 800000,
          distributionMethod: '业绩目标达成比例',
          approvalStatus: '已批准',
          paymentDate: '2023-04-15,2023-07-15,2023-10-15,2024-01-15',
          createdBy: '管理员',
          createdTime: '2023-01-10',
          description: '根据季度销售业绩完成比例发放季度奖金'
        },
        {
          id: 'BS003',
          schemeName: '技术创新奖金方案',
          department: '技术部',
          year: '2023',
          formulaType: '固定金额+项目贡献',
          formulaDetail: {
            basePart: 5000,
            projectContribution: '根据项目价值评估'
          },
          totalBudget: 600000,
          distributionMethod: '项目价值评估',
          approvalStatus: '已批准',
          paymentDate: '2023-06-15,2023-12-15',
          createdBy: '管理员',
          createdTime: '2023-01-20',
          description: '奖励技术创新和重要项目贡献'
        },
        {
          id: 'BS004',
          schemeName: '财务部绩效奖金方案',
          department: '财务部',
          year: '2023',
          formulaType: '固定金额',
          formulaDetail: {
            excellent: 10000,
            good: 8000,
            average: 5000,
            poor: 2000
          },
          totalBudget: 300000,
          distributionMethod: '绩效评级分配',
          approvalStatus: '已批准',
          paymentDate: '2023-12-20',
          createdBy: '管理员',
          createdTime: '2023-01-25',
          description: '根据年度绩效评估发放固定金额奖金'
        },
        {
          id: 'BS005',
          schemeName: '市场部项目奖金方案',
          department: '市场部',
          year: '2023',
          formulaType: '项目提成比例',
          formulaDetail: {
            successfulCampaign: '项目预算的5%',
            normalCampaign: '项目预算的3%'
          },
          totalBudget: 450000,
          distributionMethod: '项目成功度评估',
          approvalStatus: '审批中',
          paymentDate: '项目结束后15天内',
          createdBy: '管理员',
          createdTime: '2023-02-05',
          description: '根据市场活动成功程度发放项目奖金'
        }
      ]
      
      // 根据条件过滤
      let filteredSchemes = [...mockBonusSchemes]
      
      if (department && department !== '全部部门') {
        filteredSchemes = filteredSchemes.filter(scheme => 
          scheme.department === department || scheme.department === '全部部门'
        )
      }
      
      if (year) {
        filteredSchemes = filteredSchemes.filter(scheme => scheme.year === year)
      }
      
      // 计算分页
      const total = filteredSchemes.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedSchemes = filteredSchemes.slice(startIndex, endIndex)
      
      return {
        code: 200,
        data: {
          schemes: paginatedSchemes,
          total,
          departments: ['全部部门', '技术部', '人力资源部', '财务部', '市场部', '销售部'],
          years: ['2023', '2022', '2021']
        },
        message: '获取奖金方案数据成功'
      }
    }
  }
] 