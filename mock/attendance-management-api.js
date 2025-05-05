export default [
  {
    url: '/api/attendance/records',
    method: 'get',
    response: ({ query }) => {
      const { employeeId, startDate, endDate, status, page = 1, pageSize = 10 } = query
      
      // 生成模拟考勤记录数据
      const mockAttendances = []
      
      // 确定日期范围
      const start = startDate ? new Date(startDate) : new Date(new Date().getFullYear(), new Date().getMonth(), 1)
      const end = endDate ? new Date(endDate) : new Date()
      
      // 为简化，我们为每个员工每天生成一条记录
      for (let i = 1; i <= 20; i++) {
        const empId = employeeId || `E${String(i).padStart(5, '0')}`
        if (employeeId && empId !== employeeId) continue
        
        // 为当前员工生成日期范围内的考勤记录
        const currentDate = new Date(start)
        while (currentDate <= end) {
          // 跳过周末
          const dayOfWeek = currentDate.getDay()
          if (dayOfWeek !== 0 && dayOfWeek !== 6) {
            // 生成随机考勤状态
            const random = Math.random()
            let attendanceStatus, reason
            
            if (random > 0.9) {
              attendanceStatus = '缺勤'
              reason = ['生病', '私事', '家庭原因', '其他'][Math.floor(Math.random() * 4)]
            } else if (random > 0.8) {
              attendanceStatus = '迟到'
              reason = ['交通拥堵', '临时有事', '闹钟没响', '其他'][Math.floor(Math.random() * 4)]
            } else if (random > 0.7) {
              attendanceStatus = '早退'
              reason = ['身体不适', '家里有急事', '其他'][Math.floor(Math.random() * 3)]
            } else {
              attendanceStatus = '正常'
              reason = ''
            }
            
            // 如果指定了状态且不匹配，则跳过
            if (status && status !== attendanceStatus) {
              currentDate.setDate(currentDate.getDate() + 1)
              continue
            }
            
            // 生成随机签到签退时间
            let checkInTime, checkOutTime
            
            if (attendanceStatus === '正常') {
              // 正常签到时间 8:30-9:00
              const checkInHour = 8
              const checkInMinute = 30 + Math.floor(Math.random() * 30)
              checkInTime = `${String(checkInHour).padStart(2, '0')}:${String(checkInMinute).padStart(2, '0')}`
              
              // 正常签退时间 17:30-18:30
              const checkOutHour = 17 + Math.floor(Math.random() * 2)
              const checkOutMinute = Math.floor(Math.random() * 60)
              checkOutTime = `${String(checkOutHour).padStart(2, '0')}:${String(checkOutMinute).padStart(2, '0')}`
            } else if (attendanceStatus === '迟到') {
              // 迟到签到时间 9:01-10:00
              const checkInHour = 9 + Math.floor(Math.random() * 2)
              const checkInMinute = Math.floor(Math.random() * 60)
              checkInTime = `${String(checkInHour).padStart(2, '0')}:${String(checkInMinute).padStart(2, '0')}`
              
              // 正常签退时间 17:30-18:30
              const checkOutHour = 17 + Math.floor(Math.random() * 2)
              const checkOutMinute = Math.floor(Math.random() * 60)
              checkOutTime = `${String(checkOutHour).padStart(2, '0')}:${String(checkOutMinute).padStart(2, '0')}`
            } else if (attendanceStatus === '早退') {
              // 正常签到时间 8:30-9:00
              const checkInHour = 8
              const checkInMinute = 30 + Math.floor(Math.random() * 30)
              checkInTime = `${String(checkInHour).padStart(2, '0')}:${String(checkInMinute).padStart(2, '0')}`
              
              // 早退签退时间 15:00-17:00
              const checkOutHour = 15 + Math.floor(Math.random() * 2)
              const checkOutMinute = Math.floor(Math.random() * 60)
              checkOutTime = `${String(checkOutHour).padStart(2, '0')}:${String(checkOutMinute).padStart(2, '0')}`
            } else {
              // 缺勤
              checkInTime = '--:--'
              checkOutTime = '--:--'
            }
            
            const dateStr = `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, '0')}-${String(currentDate.getDate()).padStart(2, '0')}`
            
            mockAttendances.push({
              id: `ATT${dateStr.replace(/-/g, '')}${empId.substring(1)}`,
              employeeId: empId,
              employeeName: `员工${empId.substring(1).replace(/^0+/, '')}`,
              department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][i % 5],
              date: dateStr,
              weekday: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][currentDate.getDay()],
              checkInTime,
              checkOutTime,
              status: attendanceStatus,
              workHours: attendanceStatus === '正常' ? '8.0' : 
                         attendanceStatus === '迟到' || attendanceStatus === '早退' ? '7.0' : '0.0',
              overtimeHours: Math.random() > 0.8 ? Math.floor(Math.random() * 3) : 0,
              reason,
              approvalStatus: reason ? (Math.random() > 0.2 ? '已批准' : '待批准') : '',
              approver: reason ? '部门经理' : ''
            })
          }
          
          // 增加一天
          currentDate.setDate(currentDate.getDate() + 1)
        }
      }
      
      // 按日期排序（降序）
      mockAttendances.sort((a, b) => new Date(b.date) - new Date(a.date))
      
      // 计算分页
      const total = mockAttendances.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedAttendances = mockAttendances.slice(startIndex, endIndex)
      
      return {
        code: 200,
        data: {
          records: paginatedAttendances,
          total,
          summary: {
            normalCount: mockAttendances.filter(att => att.status === '正常').length,
            lateCount: mockAttendances.filter(att => att.status === '迟到').length,
            earlyLeaveCount: mockAttendances.filter(att => att.status === '早退').length,
            absentCount: mockAttendances.filter(att => att.status === '缺勤').length,
            attendanceRate: Math.round(mockAttendances.filter(att => att.status === '正常').length / mockAttendances.length * 100 * 10) / 10
          }
        },
        message: '获取考勤记录成功'
      }
    }
  },
  {
    url: '/api/attendance/monthly-statistics',
    method: 'get',
    response: ({ query }) => {
      const { year, month, department } = query
      
      // 默认年月为当前年月
      const currentDate = new Date()
      const targetYear = year || currentDate.getFullYear()
      const targetMonth = month || currentDate.getMonth() + 1
      
      // 生成模拟月度考勤统计数据
      const employeeStatistics = []
      
      for (let i = 1; i <= 20; i++) {
        const empId = `E${String(i).padStart(5, '0')}`
        const deptIndex = i % 5
        const deptName = ['技术部', '销售部', '市场部', '财务部', '人力资源部'][deptIndex]
        
        // 如果指定了部门且不匹配，则跳过
        if (department && department !== deptName) continue
        
        // 当月工作日估算（20-23天）
        const workDays = 20 + Math.floor(Math.random() * 3)
        
        // 生成随机统计数据
        const normalDays = workDays - Math.floor(Math.random() * 5)
        const lateDays = Math.floor(Math.random() * 3)
        const earlyLeaveDays = Math.floor(Math.random() * 2)
        const absentDays = workDays - normalDays - lateDays - earlyLeaveDays
        const overtimeHours = Math.floor(Math.random() * 20)
        
        employeeStatistics.push({
          employeeId: empId,
          employeeName: `员工${i}`,
          department: deptName,
          position: ['工程师', '销售专员', '市场专员', '财务专员', 'HR专员'][deptIndex],
          year: targetYear,
          month: targetMonth,
          workDays,
          normalDays,
          lateDays,
          earlyLeaveDays,
          absentDays,
          overtimeHours,
          attendanceRate: Math.round(normalDays / workDays * 100 * 10) / 10,
          salaryImpact: absentDays > 3 ? '扣款' : (lateDays + earlyLeaveDays > 5 ? '警告' : '正常')
        })
      }
      
      // 计算部门统计数据
      const departmentStatistics = []
      const departments = department ? [department] : ['技术部', '销售部', '市场部', '财务部', '人力资源部']
      
      departments.forEach(dept => {
        const deptEmployees = employeeStatistics.filter(emp => emp.department === dept)
        if (deptEmployees.length === 0) return
        
        departmentStatistics.push({
          department: dept,
          employeeCount: deptEmployees.length,
          avgAttendanceRate: Math.round(deptEmployees.reduce((sum, emp) => sum + emp.attendanceRate, 0) / deptEmployees.length * 10) / 10,
          totalLateDays: deptEmployees.reduce((sum, emp) => sum + emp.lateDays, 0),
          totalEarlyLeaveDays: deptEmployees.reduce((sum, emp) => sum + emp.earlyLeaveDays, 0),
          totalAbsentDays: deptEmployees.reduce((sum, emp) => sum + emp.absentDays, 0),
          totalOvertimeHours: deptEmployees.reduce((sum, emp) => sum + emp.overtimeHours, 0),
          salaryImpactCount: deptEmployees.filter(emp => emp.salaryImpact !== '正常').length
        })
      })
      
      // 计算公司整体统计数据
      const companySummary = {
        year: targetYear,
        month: targetMonth,
        totalEmployees: employeeStatistics.length,
        avgAttendanceRate: Math.round(employeeStatistics.reduce((sum, emp) => sum + emp.attendanceRate, 0) / (employeeStatistics.length || 1) * 10) / 10,
        totalLateDays: employeeStatistics.reduce((sum, emp) => sum + emp.lateDays, 0),
        totalEarlyLeaveDays: employeeStatistics.reduce((sum, emp) => sum + emp.earlyLeaveDays, 0),
        totalAbsentDays: employeeStatistics.reduce((sum, emp) => sum + emp.absentDays, 0),
        totalOvertimeHours: employeeStatistics.reduce((sum, emp) => sum + emp.overtimeHours, 0),
        abnormalEmployees: employeeStatistics.filter(emp => emp.absentDays > 3 || emp.lateDays + emp.earlyLeaveDays > 5).length
      }
      
      return {
        code: 200,
        data: {
          employees: employeeStatistics,
          departments: departmentStatistics,
          company: companySummary
        },
        message: '获取月度考勤统计成功'
      }
    }
  },
  {
    url: '/api/attendance/overtime-records',
    method: 'get',
    response: ({ query }) => {
      const { employeeId, startDate, endDate, page = 1, pageSize = 10 } = query
      
      // 生成模拟加班记录数据
      const mockOvertimeRecords = []
      
      // 确定日期范围
      const start = startDate ? new Date(startDate) : new Date(new Date().getFullYear(), new Date().getMonth() - 1, 1)
      const end = endDate ? new Date(endDate) : new Date()
      
      for (let i = 1; i <= 50; i++) {
        const empId = employeeId || `E${String((i % 20) + 1).padStart(5, '0')}`
        if (employeeId && empId !== employeeId) continue
        
        // 生成随机日期（在范围内）
        const recordDate = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()))
        // 确保是工作日
        const dayOfWeek = recordDate.getDay()
        if (dayOfWeek === 0 || dayOfWeek === 6) continue
        
        const dateStr = `${recordDate.getFullYear()}-${String(recordDate.getMonth() + 1).padStart(2, '0')}-${String(recordDate.getDate()).padStart(2, '0')}`
        
        // 计算加班类型和时长
        const overtimeType = Math.random() > 0.3 ? '工作日加班' : (Math.random() > 0.5 ? '休息日加班' : '节假日加班')
        const overtimeHours = overtimeType === '工作日加班' ? 
                              Math.round((1 + Math.random() * 3) * 10) / 10 : 
                              Math.round((2 + Math.random() * 6) * 10) / 10
        
        // 计算薪资倍率
        let salaryRate
        if (overtimeType === '工作日加班') {
          salaryRate = 1.5
        } else if (overtimeType === '休息日加班') {
          salaryRate = 2.0
        } else {
          salaryRate = 3.0
        }
        
        const startTime = overtimeType === '工作日加班' ? 
                         `18:${String(Math.floor(Math.random() * 30) + 30).padStart(2, '0')}` : 
                         `${String(Math.floor(Math.random() * 3) + 9).padStart(2, '0')}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}`
        
        mockOvertimeRecords.push({
          id: `OT${String(i).padStart(6, '0')}`,
          employeeId: empId,
          employeeName: `员工${empId.substring(1).replace(/^0+/, '')}`,
          department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][i % 5],
          date: dateStr,
          overtimeType,
          startTime,
          endTime: calculateEndTime(startTime, overtimeHours),
          overtimeHours,
          salaryRate,
          salaryAmount: Math.round(overtimeHours * salaryRate * (100 + Math.floor(Math.random() * 50))),
          reason: generateOvertimeReason(overtimeType),
          status: Math.random() > 0.1 ? '已批准' : (Math.random() > 0.5 ? '待批准' : '已拒绝'),
          approver: '部门经理',
          approvalTime: Math.random() > 0.2 ? `${dateStr} 14:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}` : ''
        })
      }
      
      // 按日期排序（降序）
      mockOvertimeRecords.sort((a, b) => new Date(b.date) - new Date(a.date))
      
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
            totalOvertimeHours: Math.round(mockOvertimeRecords.reduce((sum, record) => sum + record.overtimeHours, 0) * 10) / 10,
            totalSalaryAmount: mockOvertimeRecords.reduce((sum, record) => sum + record.salaryAmount, 0),
            approvedCount: mockOvertimeRecords.filter(record => record.status === '已批准').length,
            pendingCount: mockOvertimeRecords.filter(record => record.status === '待批准').length,
            rejectedCount: mockOvertimeRecords.filter(record => record.status === '已拒绝').length
          }
        },
        message: '获取加班记录成功'
      }
    }
  },
  {
    url: '/api/attendance/leave-records',
    method: 'get',
    response: ({ query }) => {
      const { employeeId, leaveType, status, page = 1, pageSize = 10 } = query
      
      // 生成模拟请假记录数据
      const mockLeaveRecords = []
      
      // 假期类型
      const leaveTypes = ['年假', '事假', '病假', '婚假', '产假', '丧假', '调休']
      
      for (let i = 1; i <= 60; i++) {
        const empId = employeeId || `E${String((i % 20) + 1).padStart(5, '0')}`
        if (employeeId && empId !== employeeId) continue
        
        // 随机选择假期类型
        const recordLeaveType = leaveType || leaveTypes[Math.floor(Math.random() * leaveTypes.length)]
        if (leaveType && recordLeaveType !== leaveType) continue
        
        // 随机生成开始和结束日期
        const now = new Date()
        const year = now.getFullYear()
        const month = Math.floor(Math.random() * 6) + (now.getMonth() - 5 + 12) % 12 + 1
        const day = Math.floor(Math.random() * 28) + 1
        
        const startDate = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
        
        // 计算请假天数（不同假期类型有不同的典型天数）
        let leaveDays
        switch (recordLeaveType) {
          case '年假':
            leaveDays = Math.ceil(Math.random() * 5)
            break
          case '事假':
            leaveDays = Math.ceil(Math.random() * 3)
            break
          case '病假':
            leaveDays = Math.ceil(Math.random() * 7)
            break
          case '婚假':
            leaveDays = 3 + Math.floor(Math.random() * 8)
            break
          case '产假':
            leaveDays = 90 + Math.floor(Math.random() * 30)
            break
          case '丧假':
            leaveDays = 1 + Math.floor(Math.random() * 3)
            break
          case '调休':
            leaveDays = Math.ceil(Math.random() * 2)
            break
          default:
            leaveDays = Math.ceil(Math.random() * 3)
        }
        
        // 计算结束日期
        const endDateObj = new Date(year, month - 1, day + leaveDays - 1)
        const endDate = `${endDateObj.getFullYear()}-${String(endDateObj.getMonth() + 1).padStart(2, '0')}-${String(endDateObj.getDate()).padStart(2, '0')}`
        
        // 决定请假状态
        const leaveStatus = status || (['已批准', '待批准', '已拒绝'][Math.floor(Math.random() * 3)])
        if (status && leaveStatus !== status) continue
        
        // 是否有薪
        const isPaid = recordLeaveType === '年假' || recordLeaveType === '婚假' || recordLeaveType === '产假' || recordLeaveType === '丧假' || recordLeaveType === '调休'
        
        // 计算薪资影响
        let salaryImpact = 0
        if (!isPaid) {
          const dailySalary = Math.round(100 + Math.random() * 400) * 10
          salaryImpact = dailySalary * leaveDays
        }
        
        // 生成申请和审批时间
        const applyDate = `${year}-${String(month).padStart(2, '0')}-${String(Math.max(1, day - 7)).padStart(2, '0')}`
        const approvalDate = leaveStatus !== '待批准' ? 
                            `${year}-${String(month).padStart(2, '0')}-${String(Math.max(1, day - 5)).padStart(2, '0')}` : ''
        
        mockLeaveRecords.push({
          id: `LV${String(i).padStart(6, '0')}`,
          employeeId: empId,
          employeeName: `员工${empId.substring(1).replace(/^0+/, '')}`,
          department: ['技术部', '销售部', '市场部', '财务部', '人力资源部'][i % 5],
          leaveType: recordLeaveType,
          startDate,
          endDate,
          leaveDays,
          reason: generateLeaveReason(recordLeaveType),
          status: leaveStatus,
          isPaid,
          salaryImpact,
          applyTime: applyDate,
          approver: '部门经理',
          approvalTime: approvalDate,
          comments: leaveStatus === '已拒绝' ? '与工作安排冲突，请调整时间' : ''
        })
      }
      
      // 按日期排序（降序）
      mockLeaveRecords.sort((a, b) => new Date(b.startDate) - new Date(a.startDate))
      
      // 计算分页
      const total = mockLeaveRecords.length
      const startIndex = (page - 1) * pageSize
      const endIndex = startIndex + Number(pageSize)
      const paginatedRecords = mockLeaveRecords.slice(startIndex, endIndex)
      
      return {
        code: 200,
        data: {
          records: paginatedRecords,
          total,
          summary: {
            totalLeaveDays: mockLeaveRecords.reduce((sum, record) => sum + record.leaveDays, 0),
            paidLeaveDays: mockLeaveRecords.filter(record => record.isPaid).reduce((sum, record) => sum + record.leaveDays, 0),
            unpaidLeaveDays: mockLeaveRecords.filter(record => !record.isPaid).reduce((sum, record) => sum + record.leaveDays, 0),
            totalSalaryImpact: mockLeaveRecords.reduce((sum, record) => sum + record.salaryImpact, 0),
            approvedCount: mockLeaveRecords.filter(record => record.status === '已批准').length,
            pendingCount: mockLeaveRecords.filter(record => record.status === '待批准').length,
            rejectedCount: mockLeaveRecords.filter(record => record.status === '已拒绝').length
          },
          leaveTypes
        },
        message: '获取请假记录成功'
      }
    }
  }
]

// 辅助函数：计算结束时间
function calculateEndTime(startTime, hours) {
  const [startHour, startMinute] = startTime.split(':').map(Number)
  let endHour = startHour + Math.floor(hours)
  let endMinute = startMinute + Math.round((hours - Math.floor(hours)) * 60)
  
  if (endMinute >= 60) {
    endHour += 1
    endMinute -= 60
  }
  
  return `${String(endHour).padStart(2, '0')}:${String(endMinute).padStart(2, '0')}`
}

// 辅助函数：生成加班原因
function generateOvertimeReason(overtimeType) {
  const workdayReasons = [
    '项目紧急，需要加班完成',
    '赶制季度报告',
    '处理系统故障',
    '准备明天的重要会议',
    '完成月底数据统计'
  ]
  
  const holidayReasons = [
    '重要客户紧急需求',
    '系统维护',
    '重大项目上线',
    '年终盘点',
    '解决关键bug'
  ]
  
  if (overtimeType === '工作日加班') {
    return workdayReasons[Math.floor(Math.random() * workdayReasons.length)]
  } else {
    return holidayReasons[Math.floor(Math.random() * holidayReasons.length)]
  }
}

// 辅助函数：生成请假原因
function generateLeaveReason(leaveType) {
  const reasons = {
    '年假': [
      '休息调整',
      '家庭旅行',
      '个人休息',
      '处理个人事务'
    ],
    '事假': [
      '办理个人证件',
      '家里有急事',
      '处理私人事务'
    ],
    '病假': [
      '感冒发烧需要休息',
      '医院就诊',
      '身体不适需要调养'
    ],
    '婚假': [
      '本人结婚',
      '筹备婚礼'
    ],
    '产假': [
      '生育',
      '产检',
      '产后休养'
    ],
    '丧假': [
      '直系亲属丧事',
      '参加葬礼'
    ],
    '调休': [
      '调整工作时间',
      '加班调休',
      '工作时间平衡'
    ]
  }
  
  const typeReasons = reasons[leaveType] || reasons['事假']
  return typeReasons[Math.floor(Math.random() * typeReasons.length)]
} 