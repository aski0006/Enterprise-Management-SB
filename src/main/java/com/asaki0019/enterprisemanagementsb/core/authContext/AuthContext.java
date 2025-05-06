package com.asaki0019.enterprisemanagementsb.core.authContext;

import java.util.Collections;
import java.util.Set;

/**
 * 认证上下文类，用于存储当前线程的用户权限和用户ID信息
 *
 * 使用步骤:
 * 1. 在用户登录成功后，设置用户信息:
 *    AuthContext.setUserId(用户ID);
 *    AuthContext.setPermissions(用户权限集合);
 *
 * 2. 在需要获取用户信息的地方:
 *    Long userId = AuthContext.getUserId();
 *    Set<String> permissions = AuthContext.getPermissions();
 *
 * 3. 在请求结束时清理线程数据:
 *    AuthContext.clear();
 *
 * 注意事项:
 * - 建议在拦截器或过滤器中统一清理线程数据，避免内存泄漏
 * - 确保在多线程环境下正确传递上下文信息
 */
public class AuthContext {
    /** 存储当前线程的权限信息 */
    private static final ThreadLocal<Set<String>> PERMISSIONS = new ThreadLocal<>();
    /** 存储当前线程的用户ID */
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    /**
     * 设置当前线程的权限信息
     * @param permissions 权限集合
     */
    public static void setPermissions(Set<String> permissions) {
        PERMISSIONS.set(permissions);
    }

    /**
     * 获取当前线程的权限信息
     * @return 权限集合，如果未设置则返回空集合
     */
    public static Set<String> getPermissions() {
        return PERMISSIONS.get() != null ? PERMISSIONS.get() : Collections.emptySet();
    }

    /**
     * 设置当前线程的用户ID
     * @param userId 用户ID
     */
    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前线程的用户ID
     * @return 用户ID
     */
    public static String getUserId() {
        return USER_ID.get();
    }

    /**
     * 清除当前线程存储的所有信息
     */
    public static void clear() {
        PERMISSIONS.remove();
        USER_ID.remove();
    }
}