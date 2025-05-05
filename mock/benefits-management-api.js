export default [
  {
    url: '/api/benefits/employee-benefits',
    method: 'get',
    response: ({ query }) => {
      const { employeeId, page = 1, pageSize = 10 } = query
      
      // 生成模拟员工福利数据
      const mockBenefits = []
      
      for (let i = 1; i <= 20; i++) {
        const empId = employeeId || `E${String(i).padStart(5, '0')}`
        if (employeeId && empId !== employeeId) continue
        
        // 基础福利
        mockBenefits.push({
          id: `BEN${String(i * 4 - 3).padStart(6, '0')}`,
          employeeId: empId,
          employeeName: `员工${empId.substring(1).replace(/^0+/, '')}`,
          department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][i % 5],
          benefitType: '社会保险',
          benefitName: '五险一金',
          status: '生效中',
          startDate: '2020-06-01',
          endDate: null,
          coverage: {
            pensionInsurance: {
              personalRatio: 0.08,
              companyRatio: 0.16,
              base: 10000,
              personal: 800,
              company: 1600
            },
            medicalInsurance: {
              personalRatio: 0.02,
              companyRatio: 0.08,
              base: 10000,
              personal: 200,
              company: 800
            },
            unemploymentInsurance: {
              personalRatio: 0.005,
              companyRatio: 0.015,
              base: 10000,
              personal: 50,
              company: 150
            },
            workInjuryInsurance: {
              personalRatio: 0,
              companyRatio: 0.005,
              base: 10000,
              personal: 0,
              company: 50
            },
            maternityInsurance: {
              personalRatio: 0,
              companyRatio: 0.01,
              base: 10000,
              personal: 0,
              company: 100
            },
            housingFund: {
              personalRatio: 0.07,
              companyRatio: 0.07,
              base: 10000,
              personal: 700,
              company: 700
            }
          },
          monthlyCost: 4400
        })
        
        // 商业保险
        mockBenefits.push({
          id: `BEN${String(i * 4 - 2).padStart(6, '0')}`,
          employeeId: empId,
          employeeName: `员工${empId.substring(1).replace(/^0+/, '')}`,
          department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][i % 5],
          benefitType: '商业保险',
          benefitName: '补充医疗保险',
          status: '生效中',
          startDate: '2020-06-01',
          endDate: null,
          coverage: {
            provider: '某保险公司',
            policyNumber: `MED${String(10000 + i).padStart(8, '0')}`,
            coverageAmount: 1000000,
            coverageDetails: '包含门诊、住院、重大疾病等全面医疗保障',
            familyIncluded: true
          },
          monthlyCost: 300
        })
        
        // 企业年金
        mockBenefits.push({
          id: `BEN${String(i * 4 - 1).padStart(6, '0')}`,
          employeeId: empId,
          employeeName: `员工${empId.substring(1).replace(/^0+/, '')}`,
          department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][i % 5],
          benefitType: '企业年金',
          benefitName: '企业养老金计划',
          status: '生效中',
          startDate: '2021-01-01',
          endDate: null,
          coverage: {
            personalContribution: 500,
            companyContribution: 1000,
            accumulatedAmount: 30000 + Math.floor(Math.random() * 20000),
            expectedReturnRate: '5%-8%',
            withdrawalConditions: '退休后或特殊情况下可提取'
          },
          monthlyCost: 1500
        })
        
        // 其他福利
        mockBenefits.push({
          id: `BEN${String(i * 4).padStart(6, '0')}`,
          employeeId: empId,
          employeeName: `员工${empId.substring(1).replace(/^0+/, '')}`,
          department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][i % 5],
          benefitType: '其他福利',
          benefitName: '健康体检',
          status: '生效中',
          startDate: '2020-06-01',
          endDate: null,
          coverage: {
            frequency: '每年一次',
            provider: '某三甲医院',
            coverageDetails: '全面体检套餐，包含常规检查和专项检查',
            lastCheckupDate: '2023-05-15',
            nextCheckupDate: '2024-05-15'
          },
          monthlyCost: 100
        })
      }
      
      // 过滤和排序
      let filteredBenefits = [...mockBenefits]
      
      // 按福利类型排序
      filteredBenefits.sort((a, b) => a.benefitType.localeCompare(b.benefitType))
      
      // 计算分页
      const total = filteredBenefits.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedBenefits = filteredBenefits.slice(startIndex, endIndex)
      
      return {
        code: 200,
        data: {
          benefits: paginatedBenefits,
          total,
          summary: {
            totalBenefitTypes: [...new Set(filteredBenefits.map(item => item.benefitType))].length,
            totalMonthlyCost: filteredBenefits.reduce((sum, item) => sum + item.monthlyCost, 0)
          }
        },
        message: '获取员工福利信息成功'
      }
    }
  },
  {
    url: '/api/benefits/available-plans',
    method: 'get',
    response: ({ query }) => {
      const { benefitType, department } = query
      
      // 生成可用福利计划数据
      const mockBenefitPlans = [
        {
          id: 'BP001',
          planName: '基础社保计划',
          benefitType: '社会保险',
          description: '法定五险一金，按照当地最低缴费基数',
          eligibleDepartments: ['全部部门'],
          eligiblePositions: ['全部职位'],
          coverageDetails: '包含养老保险、医疗保险、失业保险、工伤保险、生育保险和住房公积金',
          companyContribution: '按照法定比例',
          employeeContribution: '按照法定比例',
          isDefault: true,
          status: '生效中',
          createdTime: '2020-01-01'
        },
        {
          id: 'BP002',
          planName: '高级社保计划',
          benefitType: '社会保险',
          description: '五险一金，按照当地平均工资缴费',
          eligibleDepartments: ['技术部', '销售部', '市场部'],
          eligiblePositions: ['经理', '总监', '高级工程师'],
          coverageDetails: '包含养老保险、医疗保险、失业保险、工伤保险、生育保险和住房公积金，缴费基数高于最低标准',
          companyContribution: '按照法定比例，基数为员工实际工资',
          employeeContribution: '按照法定比例，基数为员工实际工资',
          isDefault: false,
          status: '生效中',
          createdTime: '2020-01-01'
        },
        {
          id: 'BP003',
          planName: '基础医疗保险',
          benefitType: '商业保险',
          description: '基础补充医疗保险计划',
          eligibleDepartments: ['全部部门'],
          eligiblePositions: ['全部职位'],
          coverageDetails: '年度最高报销额50万元，覆盖门诊和住院',
          companyContribution: '100%',
          employeeContribution: '0%',
          isDefault: true,
          status: '生效中',
          createdTime: '2020-01-01'
        },
        {
          id: 'BP004',
          planName: '高级医疗保险',
          benefitType: '商业保险',
          description: '高级补充医疗保险计划，含家属保障',
          eligibleDepartments: ['技术部', '销售部', '市场部'],
          eligiblePositions: ['经理', '总监', '副总裁'],
          coverageDetails: '年度最高报销额100万元，覆盖门诊、住院和特殊医疗需求，包含家属医疗保障',
          companyContribution: '100%',
          employeeContribution: '家属部分自付',
          isDefault: false,
          status: '生效中',
          createdTime: '2020-01-01'
        },
        {
          id: 'BP005',
          planName: '基础企业年金',
          benefitType: '企业年金',
          description: '基础企业养老金计划',
          eligibleDepartments: ['全部部门'],
          eligiblePositions: ['全部职位'],
          coverageDetails: '公司每月缴纳员工工资的5%，员工自愿缴纳2%',
          companyContribution: '工资的5%',
          employeeContribution: '工资的2%（自愿）',
          isDefault: true,
          status: '生效中',
          createdTime: '2021-01-01'
        },
        {
          id: 'BP006',
          planName: '高级企业年金',
          benefitType: '企业年金',
          description: '高级企业养老金计划，缴纳比例更高',
          eligibleDepartments: ['技术部', '销售部', '财务部'],
          eligiblePositions: ['经理', '总监', '副总裁'],
          coverageDetails: '公司每月缴纳员工工资的8%，员工自愿缴纳3-5%',
          companyContribution: '工资的8%',
          employeeContribution: '工资的3-5%（自愿）',
          isDefault: false,
          status: '生效中',
          createdTime: '2021-01-01'
        },
        {
          id: 'BP007',
          planName: '健康体检计划',
          benefitType: '其他福利',
          description: '年度健康体检福利',
          eligibleDepartments: ['全部部门'],
          eligiblePositions: ['全部职位'],
          coverageDetails: '每年一次全面体检，包含常规检查项目',
          companyContribution: '100%',
          employeeContribution: '0%',
          isDefault: true,
          status: '生效中',
          createdTime: '2020-01-01'
        },
        {
          id: 'BP008',
          planName: '高级健康体检计划',
          benefitType: '其他福利',
          description: '高级健康体检福利，含家属体检',
          eligibleDepartments: ['技术部', '销售部', '市场部'],
          eligiblePositions: ['经理', '总监', '副总裁'],
          coverageDetails: '每年一次全面体检，包含常规检查和特殊检查项目，可携带一名家属',
          companyContribution: '员工100%，家属50%',
          employeeContribution: '家属50%',
          isDefault: false,
          status: '生效中',
          createdTime: '2020-01-01'
        },
        {
          id: 'BP009',
          planName: '弹性福利计划',
          benefitType: '其他福利',
          description: '员工自选福利计划',
          eligibleDepartments: ['全部部门'],
          eligiblePositions: ['全部职位'],
          coverageDetails: '每年固定福利额度，员工可自由选择使用方向，如健身、培训、旅游等',
          companyContribution: '每年5000元额度',
          employeeContribution: '0%',
          isDefault: false,
          status: '生效中',
          createdTime: '2022-01-01'
        },
        {
          id: 'BP010',
          planName: '员工关怀计划',
          benefitType: '其他福利',
          description: '员工生日、结婚、生育等特殊时刻关怀',
          eligibleDepartments: ['全部部门'],
          eligiblePositions: ['全部职位'],
          coverageDetails: '生日礼品、结婚礼金、生育礼金等',
          companyContribution: '100%',
          employeeContribution: '0%',
          isDefault: true,
          status: '生效中',
          createdTime: '2020-01-01'
        }
      ]
      
      // 根据类型和部门筛选
      let filteredPlans = [...mockBenefitPlans]
      
      if (benefitType) {
        filteredPlans = filteredPlans.filter(plan => plan.benefitType === benefitType)
      }
      
      if (department) {
        filteredPlans = filteredPlans.filter(plan => 
          plan.eligibleDepartments.includes('全部部门') || 
          plan.eligibleDepartments.includes(department)
        )
      }
      
      return {
        code: 200,
        data: {
          plans: filteredPlans,
          benefitTypes: ['社会保险', '商业保险', '企业年金', '其他福利'],
          departments: ['技术部', '销售部', '市场部', '财务部', '人力资源部']
        },
        message: '获取可用福利计划成功'
      }
    }
  },
  {
    url: '/api/benefits/claim-records',
    method: 'get',
    response: ({ query }) => {
      const { employeeId, benefitType, status, startDate, endDate, page = 1, pageSize = 10 } = query
      
      // 生成模拟报销记录
      const mockClaimRecords = []
      
      // 确定日期范围
      const start = startDate ? new Date(startDate) : new Date(new Date().getFullYear(), 0, 1)
      const end = endDate ? new Date(endDate) : new Date()
      
      for (let i = 1; i <= 50; i++) {
        const empId = employeeId || `E${String((i % 20) + 1).padStart(5, '0')}`
        if (employeeId && empId !== employeeId) continue
        
        // 随机生成日期（在范围内）
        const claimDate = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()))
        const dateStr = `${claimDate.getFullYear()}-${String(claimDate.getMonth() + 1).padStart(2, '0')}-${String(claimDate.getDate()).padStart(2, '0')}`
        
        // 随机决定报销类型
        const claimBenefitType = benefitType || ['医疗保险', '商业保险', '其他'][Math.floor(Math.random() * 3)]
        if (benefitType && claimBenefitType !== benefitType) continue
        
        // 决定金额范围
        let amountRange
        switch (claimBenefitType) {
          case '医疗保险':
            amountRange = { min: 100, max: 5000 }
            break
          case '商业保险':
            amountRange = { min: 1000, max: 20000 }
            break
          default:
            amountRange = { min: 200, max: 2000 }
        }
        
        // 计算申请金额和批准金额
        const claimAmount = Math.round((amountRange.min + Math.random() * (amountRange.max - amountRange.min)) * 100) / 100
        const approvedAmount = Math.round(claimAmount * (0.7 + Math.random() * 0.3) * 100) / 100
        
        // 决定状态
        const claimStatus = status || ['待审批', '已批准', '已拒绝', '已支付'][Math.floor(Math.random() * 4)]
        if (status && claimStatus !== status) continue
        
        // 决定报销项目
        let claimItems
        if (claimBenefitType === '医疗保险') {
          claimItems = [
            { name: '门诊费用', amount: Math.round(claimAmount * 0.4 * 100) / 100 },
            { name: '药品费用', amount: Math.round(claimAmount * 0.6 * 100) / 100 }
          ]
        } else if (claimBenefitType === '商业保险') {
          claimItems = [
            { name: '住院费用', amount: Math.round(claimAmount * 0.7 * 100) / 100 },
            { name: '手术费用', amount: Math.round(claimAmount * 0.3 * 100) / 100 }
          ]
        } else {
          claimItems = [
            { name: '培训费用', amount: claimAmount }
          ]
        }
        
        mockClaimRecords.push({
          id: `CLM${String(i).padStart(6, '0')}`,
          employeeId: empId,
          employeeName: `员工${empId.substring(1).replace(/^0+/, '')}`,
          department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][i % 5],
          benefitType: claimBenefitType,
          claimDate: dateStr,
          claimAmount,
          approvedAmount: claimStatus === '已批准' || claimStatus === '已支付' ? approvedAmount : 0,
          items: claimItems,
          reason: generateClaimReason(claimBenefitType),
          status: claimStatus,
          submittedDate: dateStr,
          processedDate: claimStatus !== '待审批' ? 
                        `${dateStr.substring(0, 8)}${String(Number(dateStr.substring(8)) + Math.floor(Math.random() * 5) + 1).padStart(2, '0')}` : '',
          approver: claimStatus !== '待审批' ? '人力资源部经理' : '',
          comments: claimStatus === '已拒绝' ? '报销凭证不足，请补充材料' : ''
        })
      }
      
      // 按日期排序（降序）
      mockClaimRecords.sort((a, b) => new Date(b.claimDate) - new Date(a.claimDate))
      
      // 计算分页
      const total = mockClaimRecords.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedRecords = mockClaimRecords.slice(startIndex, endIndex)
      
      return {
        code: 200,
        data: {
          records: paginatedRecords,
          total,
          summary: {
            totalClaimAmount: Math.round(mockClaimRecords.reduce((sum, record) => sum + record.claimAmount, 0) * 100) / 100,
            totalApprovedAmount: Math.round(mockClaimRecords.reduce((sum, record) => sum + record.approvedAmount, 0) * 100) / 100,
            pendingCount: mockClaimRecords.filter(record => record.status === '待审批').length,
            approvedCount: mockClaimRecords.filter(record => record.status === '已批准' || record.status === '已支付').length,
            rejectedCount: mockClaimRecords.filter(record => record.status === '已拒绝').length
          },
          benefitTypes: ['医疗保险', '商业保险', '其他'],
          statusOptions: ['待审批', '已批准', '已拒绝', '已支付']
        },
        message: '获取报销记录成功'
      }
    }
  },
  {
    url: '/api/benefits/usage-statistics',
    method: 'get',
    response: ({ query }) => {
      const { year = new Date().getFullYear(), department } = query
      
      // 生成模拟福利使用统计数据
      // 部门数据
      const departmentData = [
        {
          department: '技术部',
          employeeCount: 38,
          benefitUsage: {
            medicalInsurance: {
              budget: 456000,
              used: 320000,
              claimCount: 65,
              averagePerEmployee: 8421
            },
            commercialInsurance: {
              budget: 240000,
              used: 180000,
              claimCount: 22,
              averagePerEmployee: 4737
            },
            enterprisePension: {
              budget: 684000,
              used: 684000,
              claimCount: 0,
              averagePerEmployee: 18000
            },
            otherBenefits: {
              budget: 190000,
              used: 150000,
              claimCount: 45,
              averagePerEmployee: 3947
            }
          },
          totalBudget: 1570000,
          totalUsed: 1334000,
          usageRate: 85
        },
        {
          department: '销售部',
          employeeCount: 31,
          benefitUsage: {
            medicalInsurance: {
              budget: 372000,
              used: 280000,
              claimCount: 48,
              averagePerEmployee: 9032
            },
            commercialInsurance: {
              budget: 198000,
              used: 150000,
              claimCount: 18,
              averagePerEmployee: 4839
            },
            enterprisePension: {
              budget: 558000,
              used: 558000,
              claimCount: 0,
              averagePerEmployee: 18000
            },
            otherBenefits: {
              budget: 155000,
              used: 130000,
              claimCount: 35,
              averagePerEmployee: 4194
            }
          },
          totalBudget: 1283000,
          totalUsed: 1118000,
          usageRate: 87
        },
        {
          department: '市场部',
          employeeCount: 19,
          benefitUsage: {
            medicalInsurance: {
              budget: 228000,
              used: 190000,
              claimCount: 32,
              averagePerEmployee: 10000
            },
            commercialInsurance: {
              budget: 120000,
              used: 90000,
              claimCount: 12,
              averagePerEmployee: 4737
            },
            enterprisePension: {
              budget: 342000,
              used: 342000,
              claimCount: 0,
              averagePerEmployee: 18000
            },
            otherBenefits: {
              budget: 95000,
              used: 80000,
              claimCount: 25,
              averagePerEmployee: 4211
            }
          },
          totalBudget: 785000,
          totalUsed: 702000,
          usageRate: 89
        },
        {
          department: '财务部',
          employeeCount: 15,
          benefitUsage: {
            medicalInsurance: {
              budget: 180000,
              used: 160000,
              claimCount: 28,
              averagePerEmployee: 10667
            },
            commercialInsurance: {
              budget: 96000,
              used: 75000,
              claimCount: 10,
              averagePerEmployee: 5000
            },
            enterprisePension: {
              budget: 270000,
              used: 270000,
              claimCount: 0,
              averagePerEmployee: 18000
            },
            otherBenefits: {
              budget: 75000,
              used: 68000,
              claimCount: 20,
              averagePerEmployee: 4533
            }
          },
          totalBudget: 621000,
          totalUsed: 573000,
          usageRate: 92
        },
        {
          department: '人力资源部',
          employeeCount: 10,
          benefitUsage: {
            medicalInsurance: {
              budget: 120000,
              used: 95000,
              claimCount: 18,
              averagePerEmployee: 9500
            },
            commercialInsurance: {
              budget: 60000,
              used: 45000,
              claimCount: 6,
              averagePerEmployee: 4500
            },
            enterprisePension: {
              budget: 180000,
              used: 180000,
              claimCount: 0,
              averagePerEmployee: 18000
            },
            otherBenefits: {
              budget: 50000,
              used: 42000,
              claimCount: 14,
              averagePerEmployee: 4200
            }
          },
          totalBudget: 410000,
          totalUsed: 362000,
          usageRate: 88
        }
      ]
      
      // 月度数据
      const monthlyData = []
      for (let month = 1; month <= 12; month++) {
        const totalBudget = 350000 + Math.floor(Math.random() * 50000)
        const totalUsed = Math.round(totalBudget * (0.7 + Math.random() * 0.2))
        
        monthlyData.push({
          month: `${year}-${String(month).padStart(2, '0')}`,
          medicalInsurance: Math.round(totalUsed * 0.35),
          commercialInsurance: Math.round(totalUsed * 0.2),
          enterprisePension: Math.round(totalUsed * 0.35),
          otherBenefits: Math.round(totalUsed * 0.1),
          totalBudget,
          totalUsed,
          usageRate: Math.round(totalUsed / totalBudget * 100)
        })
      }
      
      // 福利类型数据
      const benefitTypeData = [
        {
          type: '医疗保险',
          budget: 1356000,
          used: 1045000,
          usageRate: 77,
          claimCount: 191,
          averageAmount: 5471
        },
        {
          type: '商业保险',
          budget: 714000,
          used: 540000,
          usageRate: 76,
          claimCount: 68,
          averageAmount: 7941
        },
        {
          type: '企业年金',
          budget: 2034000,
          used: 2034000,
          usageRate: 100,
          claimCount: 0,
          averageAmount: 0
        },
        {
          type: '其他福利',
          budget: 565000,
          used: 470000,
          usageRate: 83,
          claimCount: 139,
          averageAmount: 3381
        }
      ]
      
      // 如果指定了部门，只返回该部门的数据
      let filteredDepartments = department ? 
                             departmentData.filter(dept => dept.department === department) : 
                             departmentData
      
      // 计算总体数据
      const totalEmployees = filteredDepartments.reduce((sum, dept) => sum + dept.employeeCount, 0)
      const totalBudget = filteredDepartments.reduce((sum, dept) => sum + dept.totalBudget, 0)
      const totalUsed = filteredDepartments.reduce((sum, dept) => sum + dept.totalUsed, 0)
      
      return {
        code: 200,
        data: {
          year,
          company: {
            employeeCount: totalEmployees,
            totalBudget,
            totalUsed,
            usageRate: Math.round(totalUsed / totalBudget * 100),
            perEmployeeAverage: Math.round(totalUsed / totalEmployees)
          },
          departments: filteredDepartments,
          monthlyData: department ? 
                     monthlyData.map(month => ({
                       ...month,
                       totalBudget: Math.round(month.totalBudget * filteredDepartments[0].employeeCount / totalEmployees),
                       totalUsed: Math.round(month.totalUsed * filteredDepartments[0].employeeCount / totalEmployees)
                     })) : 
                     monthlyData,
          benefitTypes: department ?
                      benefitTypeData.map(benefit => ({
                        ...benefit,
                        budget: Math.round(benefit.budget * filteredDepartments[0].employeeCount / totalEmployees),
                        used: Math.round(benefit.used * filteredDepartments[0].employeeCount / totalEmployees)
                      })) :
                      benefitTypeData
        },
        message: '获取福利使用统计成功'
      }
    }
  }
]

// 辅助函数：生成报销原因
function generateClaimReason(benefitType) {
  const medicalReasons = [
    '感冒治疗',
    '定期体检',
    '牙科治疗',
    '慢性病治疗',
    '意外伤害治疗'
  ]
  
  const commercialReasons = [
    '住院治疗',
    '手术费用',
    '重大疾病治疗',
    '专科治疗',
    '康复理疗'
  ]
  
  const otherReasons = [
    '专业培训费用',
    '健身房会员费',
    '团队建设活动',
    '生日福利',
    '工作餐补贴'
  ]
  
  if (benefitType === '医疗保险') {
    return medicalReasons[Math.floor(Math.random() * medicalReasons.length)]
  } else if (benefitType === '商业保险') {
    return commercialReasons[Math.floor(Math.random() * commercialReasons.length)]
  } else {
    return otherReasons[Math.floor(Math.random() * otherReasons.length)]
  }
} 