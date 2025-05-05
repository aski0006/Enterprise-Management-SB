package com.asaki0019.enterprisemanagementsb.service.enter;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.JwtUtils;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.asaki0019.enterprisemanagementsb.request.enter.LoginRequest;
import com.asaki0019.enterprisemanagementsb.request.enter.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class EnterService {
    private final SysLogger sysLogger;

    @Autowired
    public EnterService(SysLogger sysLogger) {
        this.sysLogger = sysLogger;
    }

    public Result<?> loginService(LoginRequest request) {
        // TODO：与数据库交互
        var permissionSet = new HashSet<String>();
        permissionSet.add("read");
        var token = JwtUtils.generateToken(request.getUsername(), 1L, permissionSet);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        sysLogger.info(
                MessageConstructor.constructPlainMessage(
                        "login",
                        "username", request.getUsername(),
                        "token", token
                )
        );
        // success:
        return Result.success(data);
    }

    public Result<?> registerService(RegisterRequest request) {
        // TODO：与数据库交互
        sysLogger.info(
                MessageConstructor.constructPlainMessage(
                        "register",
                        "username", request.getUsername()
                )
        );
        // success:
        return Result.success(null);
    }
}