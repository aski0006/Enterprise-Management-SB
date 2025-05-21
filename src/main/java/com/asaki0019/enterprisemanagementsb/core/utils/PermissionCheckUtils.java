package com.asaki0019.enterprisemanagementsb.core.utils;

import com.asaki0019.enterprisemanagementsb.core.authContext.AuthContext;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.enums.Logical;
import com.asaki0019.enterprisemanagementsb.core.exception.AuthException;

import java.util.Arrays;
import java.util.Set;

/**
 * 权限检查工具类
 *
 * <p>提供权限验证和数据所有权检查的工具方法。
 *
 * <h2>使用示例:</h2>
 * <pre>
 * // 1. 检查用户是否拥有指定权限
 * String[] permissions = {"user:view", "user:edit"};
 *
 * // AND 逻辑 - 需要同时具备所有权限
 * boolean hasAllPermissions = PermissionCheckUtils.checkPermissions(permissions, Logical.AND);
 *
 * // OR 逻辑 - 只需具备其中一个权限
 * boolean hasAnyPermission = PermissionCheckUtils.checkPermissions(permissions, Logical.OR);
 *
 * // 2. 检查数据所有权
 * try {
 *     PermissionCheckUtils.checkDataPermission(dataOwnerId);
 * } catch (AuthException e) {
 *     // 处理无权限异常
 * }
 * </pre>
 */
public class PermissionCheckUtils {

    /**
     * 检查用户是否拥有指定的权限
     *
     * @param requiredPermissions 需要检查的权限数组
     * @param logical 权限逻辑关系（AND 或 OR）
     * @return 如果用户具有指定权限返回true，否则返回false
     */
    public static boolean checkPermissions(String[] requiredPermissions, Logical logical) {
        if (requiredPermissions.length == 0) return true;

        Set<String> userPermissions = AuthContext.getPermissions();
        if (userPermissions.isEmpty()) return false;

        if (logical == Logical.AND) {
            return Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
        } else {
            return Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }
    }

    /**
     * 检查当前用户是否为数据所有者
     *
     * @param dataOwnerId 数据所有者ID
     * @throws AuthException 如果当前用户不是数据所有者，抛出权限异常
     */
    public static void checkDataPermission(String dataOwnerId) {
        String currentUserId = AuthContext.getUserId();
        if (!dataOwnerId.equals(currentUserId)) {
            throw new AuthException(ErrorCode.NO_PERMISSION);
        }
    }
}