package com.asaki0019.enterprisemanagementsb.core.aspect;

import com.asaki0019.enterprisemanagementsb.core.annotation.RequiresPermission;
import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.enums.Logical;
import com.asaki0019.enterprisemanagementsb.core.exception.AuthException;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.PermissionCheckUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 权限检查切面
 * 用于处理需要权限验证的方法调用
 */
@Aspect
@Component
public class PermissionAspect {

    /**
     * 系统日志记录器
     */
    private final SysLogger sysLogger;

    /**
     * 构造函数
     * @param sysLogger 系统日志记录器
     */
    @Autowired
    public PermissionAspect(SysLogger sysLogger) {
        this.sysLogger = sysLogger;
    }

    /**
     * 权限检查环绕通知
     * @param joinPoint 连接点
     * @param requiresPermission 权限注解
     * @return 目标方法的执行结果
     * @throws Throwable 如果权限验证失败或方法执行出错
     */
    @Around(value = "@annotation(requiresPermission)", argNames = "joinPoint,requiresPermission")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        sysLogger.info("权限检测 : " + Arrays.toString(requiresPermission.value()));
        String[] requiredPermissions = requiresPermission.value();
        Logical logical = requiresPermission.logical();
        if (!PermissionCheckUtils.checkPermissions(requiredPermissions, logical)) {
            throw new AuthException(ErrorCode.NO_PERMISSION);
        }
        return joinPoint.proceed();
    }
}