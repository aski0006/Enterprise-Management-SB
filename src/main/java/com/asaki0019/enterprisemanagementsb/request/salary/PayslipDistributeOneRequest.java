package com.asaki0019.enterprisemanagementsb.request.salary;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 发放单个工资单请求
 */
@Data
public class PayslipDistributeOneRequest {
    /**
     * 工资单ID
     */
    private String id;
    
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
     * 设置简单的month字符串，兼容前端直接发送
     */
    public void setMonth(String month) {
        setMonthStr(month);
    }
} 