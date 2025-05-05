package com.asaki0019.enterprisemanagementsb.controller;

import com.asaki0019.enterprisemanagementsb.core.annotation.RequiresPermission;
import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.enums.Logical;
import com.asaki0019.enterprisemanagementsb.core.exception.AuthException;
import com.asaki0019.enterprisemanagementsb.core.exception.BusinessException;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.ExcelUtils;
import com.asaki0019.enterprisemanagementsb.core.utils.JwtUtils;
import com.asaki0019.enterprisemanagementsb.core.utils.PermissionCheckUtils;
import com.asaki0019.enterprisemanagementsb.core.utils.TestExcelData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
public class TestController {
    private final SysLogger logger;

    @Autowired
    public TestController(SysLogger sysLogger) {
        logger = sysLogger;
    }

    @GetMapping("/test/exception")
    public Result<?> testExceptionExample(@RequestParam String type) throws MethodArgumentNotValidException {
        switch (type) {
            case "business":
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "订单创建失败");
            case "auth":
                throw new AuthException(ErrorCode.INVALID_TOKEN, "Token已过期");
            case "validation":
                throw new MethodArgumentNotValidException(null, null);
            default:
                throw new RuntimeException("未知错误");
        }
    }
    @PostMapping("/test/token")
    public Result<?> testLog(@RequestBody LoginRequest request) {
        // 从请求体中获取用户名和权限集合
        String username = request.getUsername();
        HashSet<String> permissionSet = request.getPermissionSet();
        String userId = request.getUserId();
        logger.info("用户登录, 用户名: " + username + ", 用户ID: " + userId + ", 权限: " + permissionSet);
        // 生成token
        String token = JwtUtils.generateToken(username, userId, permissionSet);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }

    // 登录请求参数类
    @Setter
    @Getter
    public static class LoginRequest {
        private String username;
        private String userId;
        private HashSet<String> permissionSet;
    }

    @GetMapping("/test/auth-AND")
    @RequiresPermission(value = {"read", "manage"}, logical = Logical.AND)
    public Result<?> testPermissionCheckAND() {
        return Result.success("需要同时具备read和manage权限");
    }

    @GetMapping("/test/auth-OR")
    @RequiresPermission(value = {"read", "manage"}, logical = Logical.OR)
    public Result<?> testPermissionCheckOR() {
        return Result.success("需要具备read或manage权限");
    }

    @GetMapping("/test/data-auth")
    public Result<?> testDataPermission(@RequestParam Long dataId) {
        PermissionCheckUtils.checkDataPermission(123L);
        return Result.success("数据权限校验通过");
    }


    @GetMapping("/test/export")
    public void testExportExcel(HttpServletResponse response) throws BusinessException, IOException {
        // 1. 准备测试数据
        List<TestExcelData> dataList = new ArrayList<>();
        dataList.add(new TestExcelData(1, "测试数据1", new Date()));
        dataList.add(new TestExcelData(2, "测试数据2", new Date()));

        // 2. 调用 ExcelUtils 导出
        ExcelUtils.export(response, "测试导出文件", TestExcelData.class, dataList);
    }
}
