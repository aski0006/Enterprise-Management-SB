package com.asaki0019.enterprisemanagementsb.controller.security;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.request.security.GetUserLogRequest;
import com.asaki0019.enterprisemanagementsb.request.security.UpdateRolePermissionRequest;
import com.asaki0019.enterprisemanagementsb.service.secutity.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    private final SecurityService securityService;
    private final SysLogger sysLogger;

    @Autowired
    public SecurityController(SecurityService securityService, SysLogger sysLogger) {
        this.securityService = securityService;
        this.sysLogger = sysLogger;

    }

    @PostMapping("/update-role-permission")
    public Result<?> updateRolePermission(@RequestBody UpdateRolePermissionRequest request) {
        return securityService.updateRolePermission(request);
    }

    @GetMapping("/get-system-log")
    public Result<?> getSystemLog(String beginDate, String endDate) {
        return securityService.getSystemLog(beginDate, endDate);
    }

    @PostMapping("/get-user-log")
    public Result<?> getUserLog(@RequestBody GetUserLogRequest request) {
        return securityService.getUserLog(request);
    }
}