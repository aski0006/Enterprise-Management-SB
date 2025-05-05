export default [
  {
    url: '/api/performance/employee-evaluations',
    method: 'get',
    response: ({ query }) => {
      const { employeeId, year, quarter, page = 1, pageSize = 10 } = query

      // 生成模拟员工绩效评估数据
      const mockEvaluations = []

      for (let i = 1; i <= 100; i++) {
        const empId = employeeId || `E${String((i % 20) + 1).padStart(5, '0')}`
        const evalYear = year || (2023 - Math.floor(i / 40))
        const evalQuarter = quarter || ((i % 4) + 1)

        if (employeeId && empId !== employeeId) continue
        if (year && evalYear.toString() !== year.toString()) continue
        if (quarter && evalQuarter.toString() !== quarter.toString()) continue

        const scoreRandom = Math.random()
        let score, level

        if (scoreRandom > 0.85) {
          score = 90 + Math.floor(Math.random() * 11)
          level = 'A'
        } else if (scoreRandom > 0.6) {
          score = 80 + Math.floor(Math.random() * 10)
          level = 'B+'
        } else if (scoreRandom > 0.3) {
          score = 70 + Math.floor(Math.random() * 10)
          level = 'B'
        } else if (scoreRandom > 0.1) {
          score = 60 + Math.floor(Math.random() * 10)
          level = 'C'
        } else {
          score = 50 + Math.floor(Math.random() * 10)
          level = 'D'
        }

        mockEvaluations.push({
          id: `PE${String(i).padStart(6, '0')}`,
          employeeId: empId,
          employeeName: '员工' + empId.replace(/\D/g, ''),
          department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][i % 5],
          position: ['工程师', '销售专员', '市场专员', '财务专员', 'HR专员'][i % 5],
          evaluationPeriod: `${evalYear}年Q${evalQuarter}`,
          year: evalYear,
          quarter: evalQuarter,
          evaluationDate: `${evalYear}-${String(evalQuarter * 3).padStart(2, '0')}-${String(10 + Math.floor(Math.random() * 15)).padStart(2, '0')}`,
          score,
          level,
          evaluator: ['部门经理', '直接主管', '项目经理', 'HR经理'][Math.floor(Math.random() * 4)],
          status: ['已完成', '已确认', '待确认'][Math.floor(Math.random() * 3)],
          salaryAdjustment: level === 'A' || level === 'B+' ? '建议加薪' : (level === 'D' ? '暂缓加薪' : ''),
          comments: getRandomComment(level)
        })
      }

      // 过滤数据
      let filteredEvaluations = [...mockEvaluations]

      // 计算分页
      const total = filteredEvaluations.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedEvaluations = filteredEvaluations.slice(startIndex, endIndex)

      return {
        code: 200,
        data: {
          evaluations: paginatedEvaluations,
          total,
          summary: {
            avgScore: Math.round(filteredEvaluations.reduce((sum, item) => sum + item.score, 0) / (filteredEvaluations.length || 1) * 10) / 10,
            levelDistribution: {
              A: filteredEvaluations.filter(item => item.level === 'A').length,
              'B+': filteredEvaluations.filter(item => item.level === 'B+').length,
              B: filteredEvaluations.filter(item => item.level === 'B').length,
              C: filteredEvaluations.filter(item => item.level === 'C').length,
              D: filteredEvaluations.filter(item => item.level === 'D').length
            }
          }
        },
        message: '获取绩效评估数据成功'
      }
    }
  },
  {
    url: '/api/performance/evaluation-detail',
    method: 'get',
    response: ({ query }) => {
      const { id } = query

      if (!id) {
        return {
          code: 400,
          message: '评估ID不能为空'
        }
      }

      // 模拟单个绩效评估详情
      const scoreRandom = Math.random()
      let score, level

      if (scoreRandom > 0.85) {
        score = 90 + Math.floor(Math.random() * 11)
        level = 'A'
      } else if (scoreRandom > 0.6) {
        score = 80 + Math.floor(Math.random() * 10)
        level = 'B+'
      } else if (scoreRandom > 0.3) {
        score = 70 + Math.floor(Math.random() * 10)
        level = 'B'
      } else if (scoreRandom > 0.1) {
        score = 60 + Math.floor(Math.random() * 10)
        level = 'C'
      } else {
        score = 50 + Math.floor(Math.random() * 10)
        level = 'D'
      }

      // 创建评估项目
      const categories = [
        {
          name: '工作质量',
          weight: 25,
          score: Math.round(score + (Math.random() * 10 - 5)),
          comment: '工作成果符合标准，' + getRandomDetailComment(level)
        },
        {
          name: '工作效率',
          weight: 20,
          score: Math.round(score + (Math.random() * 10 - 5)),
          comment: '工作效率' + (score > 80 ? '良好' : '一般') + '，' + getRandomDetailComment(level)
        },
        {
          name: '专业能力',
          weight: 20,
          score: Math.round(score + (Math.random() * 10 - 5)),
          comment: '专业知识掌握' + (score > 80 ? '扎实' : '基本达标') + '，' + getRandomDetailComment(level)
        },
        {
          name: '团队协作',
          weight: 15,
          score: Math.round(score + (Math.random() * 10 - 5)),
          comment: '团队合作' + (score > 80 ? '积极主动' : '正常') + '，' + getRandomDetailComment(level)
        },
        {
          name: '创新能力',
          weight: 10,
          score: Math.round(score + (Math.random() * 10 - 5)),
          comment: '创新思维' + (score > 80 ? '突出' : '有待提高') + '，' + getRandomDetailComment(level)
        },
        {
          name: '沟通能力',
          weight: 10,
          score: Math.round(score + (Math.random() * 10 - 5)),
          comment: '沟通表达' + (score > 80 ? '清晰有效' : '基本流畅') + '，' + getRandomDetailComment(level)
        }
      ]

      // 计算加权得分
      const weightedScore = Math.round(categories.reduce((sum, category) => sum + category.score * category.weight / 100, 0))

      const evaluationDetail = {
        id,
        employeeId: 'E00015',
        employeeName: '员工15',
        department: '技术部',
        position: '高级工程师',
        evaluationPeriod: '2023年Q2',
        year: 2023,
        quarter: 2,
        evaluationDate: '2023-06-15',
        score: weightedScore,
        level: level,
        categories,
        goals: [
          {
            goal: '完成产品X的核心功能开发',
            weight: 40,
            achievement: score > 80 ? '按时完成，质量良好' : '基本完成，有待改进',
            score: Math.round(score + (Math.random() * 10 - 5))
          },
          {
            goal: '优化系统性能，提升响应速度30%',
            weight: 30,
            achievement: score > 80 ? '超额完成，提升40%' : '部分完成，提升20%',
            score: Math.round(score + (Math.random() * 10 - 5))
          },
          {
            goal: '完成团队新成员培训',
            weight: 30,
            achievement: score > 80 ? '培训效果显著，新成员快速融入' : '完成基础培训',
            score: Math.round(score + (Math.random() * 10 - 5))
          }
        ],
        strengthsAndWeaknesses: {
          strengths: [
            score > 80 ? '技术能力突出，解决问题高效' : '基本技术能力扎实',
            score > 80 ? '主动性强，积极承担挑战' : '能按要求完成工作',
            score > 80 ? '团队协作能力强' : '能与团队成员合作'
          ],
          weaknesses: [
            score > 80 ? '可进一步提升项目管理能力' : '需要加强技术深度',
            score > 80 ? '文档编写可更加完善' : '沟通效率有待提高',
            score > 80 ? '可更多关注新技术发展' : '需要提高工作主动性'
          ]
        },
        developmentPlan: [
          {
            focus: '专业技能提升',
            actions: '参加高级架构设计培训，学习云原生技术',
            timeline: '下一季度',
            resources: '公司培训预算，导师指导'
          },
          {
            focus: '项目管理能力',
            actions: '担任小型项目负责人，实践项目管理流程',
            timeline: '未来6个月',
            resources: '项目管理工具，部门经理指导'
          }
        ],
        evaluator: '技术部经理',
        evaluatorComments: getRandomComment(level),
        employeeComments: score > 80 ? '认同评估结果，感谢肯定和建议' : '接受评估结果，会按照建议积极改进',
        status: '已确认',
        confirmDate: '2023-06-20',
        nextEvaluationDate: '2023-09-15'
      }

      return {
        code: 200,
        data: evaluationDetail,
        message: '获取绩效评估详情成功'
      }
    }
  },
  {
    url: '/api/performance/department-summary',
    method: 'get',
    response: ({ query }) => {
      const { year = '2023', quarter, department } = query

      // 生成模拟部门绩效汇总数据
      const departmentData = [
        {
          department: '技术部',
          employeeCount: 38,
          evaluationCompleted: 38,
          avgScore: 82.5,
          levelDistribution: {
            A: 6,
            'B+': 12,
            B: 14,
            C: 5,
            D: 1
          },
          quarterlyData: {
            Q1: { avgScore: 81.2, A: 5, 'B+': 11, B: 15, C: 6, D: 1 },
            Q2: { avgScore: 82.5, A: 6, 'B+': 12, B: 14, C: 5, D: 1 },
            Q3: { avgScore: 83.1, A: 7, 'B+': 12, B: 13, C: 5, D: 1 },
            Q4: { avgScore: 84.0, A: 8, 'B+': 13, B: 12, C: 4, D: 1 }
          }
        },
        {
          department: '销售部',
          employeeCount: 31,
          evaluationCompleted: 31,
          avgScore: 83.8,
          levelDistribution: {
            A: 6,
            'B+': 11,
            B: 10,
            C: 3,
            D: 1
          },
          quarterlyData: {
            Q1: { avgScore: 82.4, A: 5, 'B+': 10, B: 11, C: 4, D: 1 },
            Q2: { avgScore: 83.8, A: 6, 'B+': 11, B: 10, C: 3, D: 1 },
            Q3: { avgScore: 84.2, A: 7, 'B+': 11, B: 10, C: 2, D: 1 },
            Q4: { avgScore: 85.0, A: 8, 'B+': 12, B: 9, C: 2, D: 0 }
          }
        },
        {
          department: '市场部',
          employeeCount: 19,
          evaluationCompleted: 19,
          avgScore: 81.2,
          levelDistribution: {
            A: 3,
            'B+': 6,
            B: 7,
            C: 2,
            D: 1
          },
          quarterlyData: {
            Q1: { avgScore: 80.5, A: 2, 'B+': 6, B: 7, C: 3, D: 1 },
            Q2: { avgScore: 81.2, A: 3, 'B+': 6, B: 7, C: 2, D: 1 },
            Q3: { avgScore: 81.8, A: 3, 'B+': 7, B: 6, C: 2, D: 1 },
            Q4: { avgScore: 82.5, A: 4, 'B+': 7, B: 6, C: 2, D: 0 }
          }
        },
        {
          department: '财务部',
          employeeCount: 15,
          evaluationCompleted: 15,
          avgScore: 84.6,
          levelDistribution: {
            A: 3,
            'B+': 6,
            B: 5,
            C: 1,
            D: 0
          },
          quarterlyData: {
            Q1: { avgScore: 83.0, A: 2, 'B+': 5, B: 6, C: 2, D: 0 },
            Q2: { avgScore: 84.6, A: 3, 'B+': 6, B: 5, C: 1, D: 0 },
            Q3: { avgScore: 85.2, A: 3, 'B+': 7, B: 4, C: 1, D: 0 },
            Q4: { avgScore: 86.0, A: 4, 'B+': 7, B: 3, C: 1, D: 0 }
          }
        },
        {
          department: '人力资源部',
          employeeCount: 10,
          evaluationCompleted: 10,
          avgScore: 83.5,
          levelDistribution: {
            A: 2,
            'B+': 4,
            B: 3,
            C: 1,
            D: 0
          },
          quarterlyData: {
            Q1: { avgScore: 82.5, A: 1, 'B+': 4, B: 4, C: 1, D: 0 },
            Q2: { avgScore: 83.5, A: 2, 'B+': 4, B: 3, C: 1, D: 0 },
            Q3: { avgScore: 84.0, A: 2, 'B+': 4, B: 3, C: 1, D: 0 },
            Q4: { avgScore: 85.0, A: 2, 'B+': 5, B: 2, C: 1, D: 0 }
          }
        }
      ]

      // 根据部门过滤
      let filteredDepartments = [...departmentData]

      if (department) {
        filteredDepartments = filteredDepartments.filter(dept => dept.department === department)
      }

      // 如果指定了季度，只返回该季度的数据
      if (quarter) {
        const quarterKey = `Q${quarter}`
        filteredDepartments.forEach(dept => {
          const quarterData = dept.quarterlyData[quarterKey]
          dept.avgScore = quarterData.avgScore
          dept.levelDistribution = {
            A: quarterData.A,
            'B+': quarterData['B+'],
            B: quarterData.B,
            C: quarterData.C,
            D: quarterData.D
          }
          // 删除季度数据以简化响应
          delete dept.quarterlyData
        })
      }

      // 计算公司整体数据
      const overallEmployeeCount = filteredDepartments.reduce((sum, dept) => sum + dept.employeeCount, 0)
      const companyData = {
        totalEmployees: overallEmployeeCount,
        evaluationCompleted: filteredDepartments.reduce((sum, dept) => sum + dept.evaluationCompleted, 0),
        completionRate: 100,
        avgScore: Math.round(filteredDepartments.reduce((sum, dept) => sum + dept.avgScore * dept.employeeCount, 0) /
                  overallEmployeeCount * 10) / 10,
        levelDistribution: {
          A: filteredDepartments.reduce((sum, dept) => sum + dept.levelDistribution.A, 0),
          'B+': filteredDepartments.reduce((sum, dept) => sum + dept.levelDistribution['B+'], 0),
          B: filteredDepartments.reduce((sum, dept) => sum + dept.levelDistribution.B, 0),
          C: filteredDepartments.reduce((sum, dept) => sum + dept.levelDistribution.C, 0),
          D: filteredDepartments.reduce((sum, dept) => sum + dept.levelDistribution.D, 0)
        }
      }

      return {
        code: 200,
        data: filteredDepartments,
        message: '获取部门绩效汇总数据成功'
      }
    }
  },
  {
    url: '/api/performance/criteria',
    method: 'get',
    response: ({ query }) => {
      const { departmentId, position } = query

      // 通用评估标准
      const commonCriteria = [
        {
          name: '工作质量',
          weight: 25,
          description: '工作成果的完整性、准确性和可靠性',
          levels: [
            { level: 5, description: '工作质量始终超出预期，几乎无错误' },
            { level: 4, description: '工作质量稳定良好，很少出现错误' },
            { level: 3, description: '工作质量符合标准，偶尔需要修正' },
            { level: 2, description: '工作质量不稳定，经常需要修正' },
            { level: 1, description: '工作质量低下，错误频繁' }
          ]
        },
        {
          name: '工作效率',
          weight: 20,
          description: '完成工作任务的速度和资源利用效率',
          levels: [
            { level: 5, description: '工作效率极高，总能提前完成任务' },
            { level: 4, description: '工作效率高，按时完成任务且资源利用合理' },
            { level: 3, description: '工作效率良好，通常能按时完成任务' },
            { level: 2, description: '工作效率一般，经常需要延期' },
            { level: 1, description: '工作效率低下，无法在合理时间内完成任务' }
          ]
        },
        {
          name: '团队协作',
          weight: 15,
          description: '与团队成员合作、沟通和贡献',
          levels: [
            { level: 5, description: '出色的团队合作者，积极促进团队协作和绩效' },
            { level: 4, description: '良好的团队合作者，主动分享和帮助他人' },
            { level: 3, description: '能够与团队协作，完成分配的工作' },
            { level: 2, description: '团队协作意识不足，有时影响团队工作' },
            { level: 1, description: '不愿合作，经常阻碍团队工作' }
          ]
        },
        {
          name: '沟通能力',
          weight: 10,
          description: '表达、倾听和信息传递的有效性',
          levels: [
            { level: 5, description: '沟通清晰有效，能处理复杂沟通情境' },
            { level: 4, description: '沟通能力强，表达清晰，善于倾听' },
            { level: 3, description: '沟通基本有效，能传递必要信息' },
            { level: 2, description: '沟通能力有限，经常造成误解' },
            { level: 1, description: '沟通能力差，无法有效表达或理解' }
          ]
        }
      ]

      // 根据部门和职位添加特定标准
      let specificCriteria = []

      if (departmentId === 'tech' || position === 'developer') {
        specificCriteria = [
          {
            name: '技术能力',
            weight: 20,
            description: '技术知识掌握和应用能力',
            levels: [
              { level: 5, description: '技术精通，能解决复杂技术问题并创新' },
              { level: 4, description: '技术能力强，解决问题高效' },
              { level: 3, description: '具备必要的技术能力，能解决常见问题' },
              { level: 2, description: '技术能力有限，需要频繁指导' },
              { level: 1, description: '技术能力不足，无法满足工作要求' }
            ]
          },
          {
            name: '代码质量',
            weight: 10,
            description: '代码的可读性、可维护性和性能',
            levels: [
              { level: 5, description: '代码质量极高，可作为最佳实践示例' },
              { level: 4, description: '代码质量良好，结构清晰，性能优良' },
              { level: 3, description: '代码符合标准，基本可维护' },
              { level: 2, description: '代码质量不稳定，存在潜在问题' },
              { level: 1, description: '代码质量差，难以维护，性能低下' }
            ]
          }
        ]
      } else if (departmentId === 'sales' || position === 'sales') {
        specificCriteria = [
          {
            name: '销售业绩',
            weight: 25,
            description: '销售目标达成情况',
            levels: [
              { level: 5, description: '大幅超过销售目标(>120%)' },
              { level: 4, description: '超过销售目标(100-120%)' },
              { level: 3, description: '达到销售目标(90-100%)' },
              { level: 2, description: '接近销售目标(70-90%)' },
              { level: 1, description: '未能达到销售目标(<70%)' }
            ]
          },
          {
            name: '客户管理',
            weight: 15,
            description: '客户关系维护和发展',
            levels: [
              { level: 5, description: '出色的客户关系，高客户满意度和忠诚度' },
              { level: 4, description: '良好的客户关系，积极解决客户问题' },
              { level: 3, description: '能够维护客户关系，基本满足客户需求' },
              { level: 2, description: '客户关系管理不足，客户满意度低' },
              { level: 1, description: '客户关系差，频繁流失客户' }
            ]
          }
        ]
      }

      // 绩效等级说明
      const performanceLevels = [
        { level: 'A', scoreRange: '90-100', description: '杰出表现，远超预期', salaryImpact: '优先晋升和加薪' },
        { level: 'B+', scoreRange: '80-89', description: '优秀表现，超过预期', salaryImpact: '建议加薪' },
        { level: 'B', scoreRange: '70-79', description: '良好表现，达到预期', salaryImpact: '正常薪资调整' },
        { level: 'C', scoreRange: '60-69', description: '基本表现，部分达到预期', salaryImpact: '有条件调薪' },
        { level: 'D', scoreRange: '0-59', description: '不足表现，未达到预期', salaryImpact: '暂缓加薪，需改进' }
      ]

      return {
        code: 200,
        data: {
          commonCriteria,
          specificCriteria,
          performanceLevels,
          evaluationProcess: [
            { step: 1, name: '自我评估', description: '员工完成自我评估表' },
            { step: 2, name: '主管评估', description: '直接主管进行评估并给出分数' },
            { step: 3, name: '校准会议', description: '部门管理层讨论并校准评估结果' },
            { step: 4, name: '反馈面谈', description: '主管与员工沟通评估结果' },
            { step: 5, name: '结果确认', description: '员工确认评估结果，提出意见' },
            { step: 6, name: '薪资决策', description: '根据评估结果进行薪资调整决策' }
          ]
        },
        message: '获取绩效评估标准成功'
      }
    }
  }
]

// 辅助函数 - 随机评语
function getRandomComment(level) {
  if (level === 'A') {
    const comments = [
      '表现出色，是团队的榜样',
      '工作能力突出，创造了显著价值',
      '各方面表现优秀，超出期望',
      '工作态度积极，业绩显著，推动了团队进步',
      '专业能力强，解决问题高效，值得表扬'
    ]
    return comments[Math.floor(Math.random() * comments.length)]
  } else if (level === 'B+' || level === 'B') {
    const comments = [
      '工作表现良好，达到了预期目标',
      '专业能力扎实，能够完成任务',
      '有良好的团队合作精神',
      '工作认真负责，能按时完成任务',
      '具备专业能力，值得肯定'
    ]
    return comments[Math.floor(Math.random() * comments.length)]
  } else {
    const comments = [
      '基本完成工作，但有提升空间',
      '需要提高工作效率和质量',
      '专业能力有待加强',
      '团队协作意识需要提升',
      '建议加强学习，提高专业水平'
    ]
    return comments[Math.floor(Math.random() * comments.length)]
  }
}

function getRandomDetailComment(level) {
  if (level === 'A') {
    const comments = [
      '表现超出预期',
      '解决复杂问题的能力突出',
      '主动性强，积极寻求改进',
      '能够带动团队成员共同进步',
      '工作质量始终保持高水准'
    ]
    return comments[Math.floor(Math.random() * comments.length)]
  } else if (level === 'B+' || level === 'B') {
    const comments = [
      '完成任务及时准确',
      '能够独立解决问题',
      '与团队成员合作良好',
      '积极接受反馈并改进',
      '工作质量稳定可靠'
    ]
    return comments[Math.floor(Math.random() * comments.length)]
  } else {
    const comments = [
      '需要提升工作主动性',
      '在复杂任务中需要更多指导',
      '建议加强与团队的沟通',
      '工作计划性有待提高',
      '需要更多关注工作细节'
    ]
    return comments[Math.floor(Math.random() * comments.length)]
  }
}