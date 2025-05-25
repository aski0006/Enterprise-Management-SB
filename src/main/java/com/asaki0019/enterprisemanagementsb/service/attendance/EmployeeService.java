package com.asaki0019.enterprisemanagementsb.service.attendance;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;

public interface EmployeeService {
    /**
     * 根据ID获取员工信息
     * @param employeeId 员工ID
     * @return 员工信息
     */
    Employee getEmployeeById(Integer employeeId);
} 