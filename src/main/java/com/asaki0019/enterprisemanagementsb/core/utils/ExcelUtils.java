package com.asaki0019.enterprisemanagementsb.core.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel工具类,用于处理Excel相关操作
 *
 * @author asaki0019
 */
public class ExcelUtils {

    /**
     * 导出数据到Excel文件
     *
     * @param response HTTP响应对象
     * @param fileName 导出的文件名
     * @param clazz    导出数据的类型
     * @param data     要导出的数据列表
     * @param <T>      数据类型泛型
     * @throws IOException 当IO操作发生异常时抛出
     */
    public static <T> void export(HttpServletResponse response,
                                  String fileName,
                                  Class<T> clazz,
                                  List<T> data) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ".xlsx");
        EasyExcel.write(response.getOutputStream(), clazz)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("数据")
                .doWrite(data);
    }
}

