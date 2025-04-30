package com.asaki0019.enterprisemanagementsb.core.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TestExcelData {
    @ExcelProperty("ID")
    private Integer id;

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("创建时间")
    private Date createTime;

}