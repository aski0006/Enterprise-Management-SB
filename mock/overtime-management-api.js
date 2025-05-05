export default [
  {
    url: '/api/overtime/records',
    method: 'get',
    response: ({ query }) => {
      const { employeeId, departmentId, status, startDate, endDate, page = 1, pageSize = 10 } = query
      
      // 生成模拟加班记录
      const mockOvertimeRecords = []
      
      // 确定日期范围
      const start = startDate ? new Date(startDate) : new Date(new Date().getFullYear(), new Date().getMonth() - 1, 1)
      const end = endDate ? new Date(endDate) : new Date()
      
      for (let i = 1; i <= 80; i++) {
        const empId = employeeId || `E${String((i % 20) + 1).padStart(5, '0')}`
        if (employeeId && empId !== employeeId) continue
        
        const deptId = departmentId || `D${(i % 5) + 1}`
        if (departmentId && deptId !== departmentId) continue
        
        // 随机生成加班日期（在范围内）
        const overtimeDate = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()))
        const dateStr = `${overtimeDate.getFullYear()}-${String(overtimeDate.getMonth() + 1).padStart(2, '0')}-${String(overtimeDate.getDate()).padStart(2, '0')}`
        
        // 加班类型
        const overtimeTypes = ['工作日加班', '休息日加班', '节假日加班']
        const overtimeType = overtimeTypes[Math.floor(Math.random() * overtimeTypes.length)]
        
        // 加班时长（小时）
        let hours
        if (overtimeType === '工作日加班') {
          hours = 1 + Math.floor(Math.random() * 4) // 1-4小时
        } else if (overtimeType === '休息日加班') {
          hours = 4 + Math.floor(Math.random() * 6) // 4-9小时
        } else {
          hours = 6 + Math.floor(Math.random() * 6) // 6-11小时
        }
        
        // 加班补偿类型
        const compensationTypes = ['调休', '加班工资', '混合补偿']
        const compensationType = compensationTypes[Math.floor(Math.random() * compensationTypes.length)]
        
        // 加班状态
        const statusOptions = ['待审批', '已批准', '已拒绝', '已完成']
        const overtimeStatus = status || statusOptions[Math.floor(Math.random() * statusOptions.length)]
        if (status && overtimeStatus !== status) continue
        
        // 计算加班费用
        const hourlyRate = 50 + Math.floor(Math.random() * 100) // 基本时薪
        let rateMultiplier
        
        if (overtimeType === '工作日加班') {
          rateMultiplier = 1.5
        } else if (overtimeType === '休息日加班') {
          rateMultiplier = 2
        } else {
          rateMultiplier = 3
        }
        
        let compensationAmount = 0
        let timeOffHours = 0
        
        if (compensationType === '加班工资') {
          compensationAmount = Math.round(hourlyRate * hours * rateMultiplier)
        } else if (compensationType === '调休') {
          timeOffHours = hours
        } else {
          // 混合补偿：50%加班费，50%调休
          compensationAmount = Math.round(hourlyRate * hours * rateMultiplier * 0.5)
          timeOffHours = Math.round(hours * 0.5)
        }
        
        mockOvertimeRecords.push({
          id: `OT${String(i).padStart(6, '0')}`,
          employeeId: empId,
          employeeName: `员工${empId.substring(1).replace(/^0+/, '')}`,
          departmentId: deptId,
          department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][parseInt(deptId.substring(1)) - 1],
          overtimeDate: dateStr,
          startTime: `${dateStr} ${overtimeType === '工作日加班' ? '18:00' : '09:00'}`,
          endTime: `${dateStr} ${overtimeType === '工作日加班' ? (18 + hours).toString().padStart(2, '0') : (9 + hours).toString().padStart(2, '0')}:00`,
          hours,
          overtimeType,
          reason: generateOvertimeReason(overtimeType),
          status: overtimeStatus,
          compensationType,
          compensationDetails: {
            hourlyRate,
            rateMultiplier,
            amount: compensationAmount,
            timeOffHours
          },
          submittedDate: dateStr,
          approvedDate: overtimeStatus !== '待审批' ? 
                      `${dateStr.substring(0, 8)}${String(Number(dateStr.substring(8)) + Math.floor(Math.random() * 2) + 1).padStart(2, '0')}` : '',
          approvedBy: overtimeStatus !== '待审批' ? '部门经理' : '',
          remarks: overtimeStatus === '已拒绝' ? '当前工作不紧急，请合理安排工作时间' : ''
        })
      }
      
      // 按日期排序（降序）
      mockOvertimeRecords.sort((a, b) => new Date(b.overtimeDate) - new Date(a.overtimeDate))
      
      // 计算分页
      const total = mockOvertimeRecords.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedRecords = mockOvertimeRecords.slice(startIndex, endIndex)
      
      return {
        code: 200,
        data: {
          records: paginatedRecords,
          total,
          summary: {
            totalHours: mockOvertimeRecords.reduce((sum, record) => sum + record.hours, 0),
            totalCompensationAmount: mockOvertimeRecords.reduce((sum, record) => sum + record.compensationDetails.amount, 0),
            totalTimeOffHours: mockOvertimeRecords.reduce((sum, record) => sum + record.compensationDetails.timeOffHours, 0),
            pendingCount: mockOvertimeRecords.filter(record => record.status === '待审批').length,
            approvedCount: mockOvertimeRecords.filter(record => record.status === '已批准' || record.status === '已完成').length,
            rejectedCount: mockOvertimeRecords.filter(record => record.status === '已拒绝').length
          },
          filters: {
            overtimeTypes: ['工作日加班', '休息日加班', '节假日加班'],
            compensationTypes: ['调休', '加班工资', '混合补偿'],
            statusOptions: ['待审批', '已批准', '已拒绝', '已完成']
          }
        },
        message: '获取加班记录成功'
      }
    }
  },
  {
    url: '/api/overtime/submit',
    method: 'post',
    response: ({ body }) => {
      const { employeeId, departmentId, overtimeDate, startTime, endTime, overtimeType, reason, compensationType } = body
      
      // 检查必要参数
      if (!employeeId || !departmentId || !overtimeDate || !startTime || !endTime || !overtimeType || !reason || !compensationType) {
        return {
          code: 400,
          message: '请提供所有必要的加班申请信息'
        }
      }
      
      // 计算加班时长
      const start = new Date(startTime)
      const end = new Date(endTime)
      const hours = (end - start) / (1000 * 60 * 60)
      
      if (hours <= 0) {
        return {
          code: 400,
          message: '加班结束时间必须晚于开始时间'
        }
      }
      
      // 模拟提交成功
      return {
        code: 200,
        data: {
          id: `OT${String(Math.floor(Math.random() * 1000000)).padStart(6, '0')}`,
          employeeId,
          overtimeDate,
          hours: Math.round(hours * 10) / 10,
          status: '待审批',
          submittedDate: new Date().toISOString().split('T')[0]
        },
        message: '加班申请提交成功，等待审批'
      }
    }
  },
  {
    url: '/api/overtime/approve',
    method: 'post',
    response: ({ body }) => {
      const { id, status, remarks, approvedBy } = body
      
      // 检查必要参数
      if (!id || !status || !approvedBy) {
        return {
          code: 400,
          message: '请提供所有必要的审批信息'
        }
      }
      
      // 检查状态是否有效
      if (status !== '已批准' && status !== '已拒绝') {
        return {
          code: 400,
          message: '无效的审批状态'
        }
      }
      
      // 模拟审批成功
      return {
        code: 200,
        data: {
          id,
          status,
          approvedDate: new Date().toISOString().split('T')[0],
          approvedBy,
          remarks: remarks || ''
        },
        message: `加班申请${status === '已批准' ? '审批通过' : '已拒绝'}`
      }
    }
  },
  {
    url: '/api/overtime/statistics',
    method: 'get',
    response: ({ query }) => {
      const { departmentId, year = new Date().getFullYear(), month } = query
      
      // 生成部门列表
      const departments = [
        { id: 'D1', name: '技术部', employeeCount: 38 },
        { id: 'D2', name: '销售部', employeeCount: 31 },
        { id: 'D3', name: '市场部', employeeCount: 19 },
        { id: 'D4', name: '财务部', employeeCount: 15 },
        { id: 'D5', name: '人力资源部', employeeCount: 10 }
      ]
      
      // 过滤部门
      let filteredDepts = departmentId ? 
                        departments.filter(dept => dept.id === departmentId) : 
                        departments
      
      // 生成月度加班统计数据
      const monthlyStats = []
      const monthCount = month ? 1 : 12
      const startMonth = month ? parseInt(month) - 1 : 0
      
      for (let i = 0; i < monthCount; i++) {
        const currentMonth = startMonth + i
        const daysInMonth = new Date(year, currentMonth + 1, 0).getDate()
        
        filteredDepts.forEach(dept => {
          // 为每个部门生成随机加班数据
          const workdayOvertimeHours = Math.round(daysInMonth * 0.6 * (0.5 + Math.random() * 0.5) * (dept.employeeCount / 2))
          const weekendOvertimeHours = Math.round((daysInMonth / 7 * 2) * (1 + Math.random() * 2) * (dept.employeeCount / 3))
          const holidayOvertimeHours = Math.round(Math.random() * 20 * (dept.employeeCount / 5))
          
          const totalOvertimeHours = workdayOvertimeHours + weekendOvertimeHours + holidayOvertimeHours
          const totalCompensationAmount = Math.round(totalOvertimeHours * (50 + Math.random() * 100) * 2)
          
          // 每月人均加班时长
          const avgOvertimeHoursPerEmployee = Math.round(totalOvertimeHours / dept.employeeCount * 10) / 10
          
          // 最高加班员工
          const topOvertimeHours = Math.round(avgOvertimeHoursPerEmployee * (1.5 + Math.random() * 0.5))
          
          monthlyStats.push({
            departmentId: dept.id,
            department: dept.name,
            year,
            month: currentMonth + 1,
            employeeCount: dept.employeeCount,
            workdayOvertimeHours,
            weekendOvertimeHours,
            holidayOvertimeHours,
            totalOvertimeHours,
            avgOvertimeHoursPerEmployee,
            overtimeCompensationAmount: totalCompensationAmount,
            topOvertimeEmployee: {
              employeeId: `E${String(Math.floor(Math.random() * dept.employeeCount) + 1).padStart(5, '0')}`,
              hours: topOvertimeHours
            },
            overtimeDistribution: {
              '0-10小时': Math.round(dept.employeeCount * 0.4),
              '11-20小时': Math.round(dept.employeeCount * 0.3),
              '21-30小时': Math.round(dept.employeeCount * 0.2),
              '30小时以上': Math.round(dept.employeeCount * 0.1)
            },
            comparisonWithLastMonth: {
              hours: Math.round((Math.random() * 0.4 - 0.2) * 100) / 100, // -20%到+20%
              amount: Math.round((Math.random() * 0.4 - 0.2) * 100) / 100
            }
          })
        })
      }
      
      // 按月份和部门排序
      monthlyStats.sort((a, b) => {
        if (a.month !== b.month) return a.month - b.month
        return a.departmentId.localeCompare(b.departmentId)
      })
      
      // 汇总数据（如果查询的是所有部门）
      let summaryData = null
      if (!departmentId) {
        // 按月份分组
        const monthGroups = {}
        monthlyStats.forEach(stat => {
          if (!monthGroups[stat.month]) {
            monthGroups[stat.month] = []
          }
          monthGroups[stat.month].push(stat)
        })
        
        // 计算每个月份的汇总
        summaryData = Object.keys(monthGroups).map(month => {
          const monthStats = monthGroups[month]
          const totalEmployees = monthStats.reduce((sum, dept) => sum + dept.employeeCount, 0)
          const totalWorkdayHours = monthStats.reduce((sum, dept) => sum + dept.workdayOvertimeHours, 0)
          const totalWeekendHours = monthStats.reduce((sum, dept) => sum + dept.weekendOvertimeHours, 0)
          const totalHolidayHours = monthStats.reduce((sum, dept) => sum + dept.holidayOvertimeHours, 0)
          const totalHours = totalWorkdayHours + totalWeekendHours + totalHolidayHours
          const totalAmount = monthStats.reduce((sum, dept) => sum + dept.overtimeCompensationAmount, 0)
          
          return {
            year,
            month: parseInt(month),
            employeeCount: totalEmployees,
            totalOvertimeHours: totalHours,
            avgOvertimeHoursPerEmployee: Math.round(totalHours / totalEmployees * 10) / 10,
            totalCompensationAmount: totalAmount,
            distributionByDepartment: monthStats.map(dept => ({
              departmentId: dept.departmentId,
              department: dept.department,
              hours: dept.totalOvertimeHours,
              percentage: Math.round(dept.totalOvertimeHours / totalHours * 1000) / 10
            })),
            distributionByType: {
              workday: {
                hours: totalWorkdayHours,
                percentage: Math.round(totalWorkdayHours / totalHours * 1000) / 10
              },
              weekend: {
                hours: totalWeekendHours,
                percentage: Math.round(totalWeekendHours / totalHours * 1000) / 10
              },
              holiday: {
                hours: totalHolidayHours,
                percentage: Math.round(totalHolidayHours / totalHours * 1000) / 10
              }
            }
          }
        })
      }
      
      return {
        code: 200,
        data: {
          departmentStats: monthlyStats,
          summary: summaryData,
          year,
          month: month ? parseInt(month) : null
        },
        message: '获取加班统计数据成功'
      }
    }
  },
  {
    url: '/api/overtime/rules',
    method: 'get',
    response: () => {
      // 生成加班规则数据
      const overtimeRules = [
        {
          id: 'OTR001',
          type: '工作日加班',
          eligibility: '所有全职员工',
          approvalProcess: '需部门经理批准',
          compensationRules: {
            rateMultiplier: 1.5,
            minDuration: 1,
            maxDuration: 4,
            compensationOptions: ['调休', '加班工资']
          },
          specialRules: '加班需提前24小时申请，特殊情况除外',
          effectiveFrom: '2020-01-01',
          lastUpdated: '2023-04-15'
        },
        {
          id: 'OTR002',
          type: '休息日加班',
          eligibility: '所有全职员工',
          approvalProcess: '需部门经理批准',
          compensationRules: {
            rateMultiplier: 2,
            minDuration: 4,
            maxDuration: 8,
            compensationOptions: ['调休', '加班工资', '混合补偿']
          },
          specialRules: '加班需提前48小时申请，特殊情况需总监批准',
          effectiveFrom: '2020-01-01',
          lastUpdated: '2023-04-15'
        },
        {
          id: 'OTR003',
          type: '节假日加班',
          eligibility: '所有全职员工',
          approvalProcess: '需部门经理及人力资源部批准',
          compensationRules: {
            rateMultiplier: 3,
            minDuration: 4,
            maxDuration: 8,
            compensationOptions: ['加班工资', '混合补偿']
          },
          specialRules: '节假日加班需提前一周申请，并说明加班必要性',
          effectiveFrom: '2020-01-01',
          lastUpdated: '2023-04-15'
        },
        {
          id: 'OTR004',
          type: '调休规则',
          eligibility: '所有参与加班的员工',
          approvalProcess: '需部门经理批准',
          compensationRules: {
            validityPeriod: '3个月',
            minimumUnit: '0.5天',
            usageProcess: '提前3个工作日申请'
          },
          specialRules: '调休应在有效期内使用，过期作废',
          effectiveFrom: '2020-01-01',
          lastUpdated: '2023-04-15'
        },
        {
          id: 'OTR005',
          type: '特殊岗位加班',
          eligibility: '技术支持、客服岗位',
          approvalProcess: '需部门经理批准',
          compensationRules: {
            rateMultiplier: {
              workday: 2,
              weekend: 2.5,
              holiday: 3.5
            },
            minDuration: 2,
            maxDuration: 12,
            compensationOptions: ['加班工资', '混合补偿']
          },
          specialRules: '特殊岗位员工加班规则，适用于需24小时值守的岗位',
          effectiveFrom: '2021-06-01',
          lastUpdated: '2023-04-15'
        }
      ]
      
      return {
        code: 200,
        data: {
          rules: overtimeRules,
          policyDocument: {
            title: '公司加班管理制度',
            version: 'v2.3',
            effectiveDate: '2023-05-01',
            approvedBy: '人力资源部',
            docLink: '/documents/overtime-policy-v2.3.pdf'
          }
        },
        message: '获取加班规则成功'
      }
    }
  }
]

// 辅助函数：生成加班原因
function generateOvertimeReason(overtimeType) {
  const workdayReasons = [
    '项目紧急交付',
    '系统维护',
    '客户紧急需求',
    '修复生产环境问题',
    '准备重要演示'
  ]
  
  const weekendReasons = [
    '版本发布',
    '系统升级',
    '数据迁移',
    '重要客户演示准备',
    '季度结算'
  ]
  
  const holidayReasons = [
    '重大系统故障修复',
    '关键客户紧急需求',
    '重要项目上线',
    '年终数据处理',
    '重要安全更新'
  ]
  
  if (overtimeType === '工作日加班') {
    return workdayReasons[Math.floor(Math.random() * workdayReasons.length)]
  } else if (overtimeType === '休息日加班') {
    return weekendReasons[Math.floor(Math.random() * weekendReasons.length)]
  } else {
    return holidayReasons[Math.floor(Math.random() * holidayReasons.length)]
  }
} 