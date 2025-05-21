package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 批量发放工资单请求
 */
@Data
public class PayslipDistributeRequest {
    /**
     * 年份
     */
    private Integer year;
    
    /**
     * 月份（1-12）
     */
    private Integer month;
    
    /**
     * 月份字符串，格式如"2023-12"
     */
    private String monthStr;
    
    /**
     * 部门ID（可选，为空时发放所有部门）
     */
    private String departmentId;
    
    /**
     * 部门名称
     */
    private String department;
    
    /**
     * 设置monthStr并自动解析为year和month
     */
    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
        if (StringUtils.hasText(monthStr) && monthStr.contains("-")) {
            try {
                String[] parts = monthStr.split("-");
                if (parts.length == 2) {
                    this.year = Integer.parseInt(parts[0]);
                    this.month = Integer.parseInt(parts[1]);
                }
            } catch (NumberFormatException e) {
                // 解析失败时保持原值
            }
        }
    }
    
    /**
     * 设置department并同步到departmentId
     */
    public void setDepartment(String department) {
        this.department = department;
        this.departmentId = department; // 设置departmentId为部门名称
    }
    
    /**
     * 获取部门名称，优先返回department，其次返回departmentId
     */
    public String getDepartmentName() {
        return StringUtils.hasText(department) ? department : departmentId;
    }
} 