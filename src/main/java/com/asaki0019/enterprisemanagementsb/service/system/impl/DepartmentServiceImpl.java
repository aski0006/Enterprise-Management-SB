package com.asaki0019.enterprisemanagementsb.service.system.impl;

import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.exception.BusinessException;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.service.system.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 部门管理服务实现类
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final SysLogger sysLogger;

    // 模拟部门数据
    private static final List<Map<String, Object>> DEPARTMENTS = new ArrayList<>();

    static {
        // 初始化模拟数据
        String[] deptNames = {"技术部", "市场部", "销售部", "财务部", "人力资源部", "运营部", "产品部", "设计部"};
        String[] managerNames = {"张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十"};
        
        for (int i = 0; i < deptNames.length; i++) {
            Map<String, Object> dept = new HashMap<>();
            dept.put("id", "D" + String.format("%03d", i + 1));
            dept.put("name", deptNames[i]);
            dept.put("manager", managerNames[i]);
            dept.put("employeeCount", 10 + new Random().nextInt(90));
            dept.put("status", "active");
            dept.put("createdTime", "2023-01-01 00:00:00");
            dept.put("parentId", i < 3 ? null : "D00" + (1 + i % 3));
            dept.put("description", deptNames[i] + "负责公司的" + deptNames[i].substring(0, deptNames[i].length() - 1) + "相关工作");
            
            DEPARTMENTS.add(dept);
        }
    }

    @Autowired
    public DepartmentServiceImpl(SysLogger sysLogger) {
        this.sysLogger = sysLogger;
    }

    @Override
    public Result<?> getDepartments(String name, String status, int page, int pageSize) {
        try {
            sysLogger.info("获取部门列表: name=" + name + ", status=" + status + ", page=" + page + ", pageSize=" + pageSize);
            
            // 过滤部门
            List<Map<String, Object>> filteredDepts = new ArrayList<>(DEPARTMENTS);
            
            if (name != null && !name.isEmpty()) {
                filteredDepts.removeIf(dept -> !String.valueOf(dept.get("name")).contains(name));
            }
            
            if (status != null && !status.isEmpty()) {
                filteredDepts.removeIf(dept -> !status.equals(String.valueOf(dept.get("status"))));
            }
            
            // 计算分页
            int total = filteredDepts.size();
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);
            
            List<Map<String, Object>> pagedDepts = new ArrayList<>();
            if (startIndex < total) {
                pagedDepts = filteredDepts.subList(startIndex, endIndex);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("departments", pagedDepts);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            
            return Result.success(result);
        } catch (Exception e) {
            sysLogger.error("获取部门列表失败: " + e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "获取部门列表失败");
        }
    }

    @Override
    public Result<?> getDepartmentById(String id) {
        try {
            sysLogger.info("获取部门详情: id=" + id);
            
            // 查找部门
            Optional<Map<String, Object>> departmentOpt = DEPARTMENTS.stream()
                    .filter(dept -> id.equals(String.valueOf(dept.get("id"))))
                    .findFirst();
            
            if (departmentOpt.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "部门不存在");
            }
            
            return Result.success(departmentOpt.get());
        } catch (Exception e) {
            sysLogger.error("获取部门详情失败: " + e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "获取部门详情失败");
        }
    }

    @Override
    public Result<?> createDepartment(Map<String, Object> department) {
        try {
            sysLogger.info("创建部门: " + department);
            
            // 校验必填字段
            if (department.get("name") == null || String.valueOf(department.get("name")).isEmpty()) {
                return Result.failure(ErrorCode.BAD_REQUEST, "部门名称不能为空");
            }
            
            // 生成ID
            String id = "D" + String.format("%03d", DEPARTMENTS.size() + 1);
            department.put("id", id);
            
            // 设置默认值
            if (department.get("status") == null) {
                department.put("status", "active");
            }
            department.put("createdTime", LocalDateTime.now().toString().replace("T", " ").substring(0, 19));
            
            // 添加部门
            DEPARTMENTS.add(new HashMap<>(department));
            
            return Result.success(department);
        } catch (Exception e) {
            sysLogger.error("创建部门失败: " + e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "创建部门失败");
        }
    }

    @Override
    public Result<?> updateDepartment(String id, Map<String, Object> department) {
        try {
            sysLogger.info("更新部门: id=" + id + ", department=" + department);
            
            // 查找部门
            Optional<Map<String, Object>> departmentOpt = DEPARTMENTS.stream()
                    .filter(dept -> id.equals(String.valueOf(dept.get("id"))))
                    .findFirst();
            
            if (departmentOpt.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "部门不存在");
            }
            
            Map<String, Object> existingDept = departmentOpt.get();
            
            // 更新字段
            for (Map.Entry<String, Object> entry : department.entrySet()) {
                if (!"id".equals(entry.getKey()) && !"createdTime".equals(entry.getKey())) {
                    existingDept.put(entry.getKey(), entry.getValue());
                }
            }
            
            // 设置更新时间
            existingDept.put("updatedTime", LocalDateTime.now().toString().replace("T", " ").substring(0, 19));
            
            return Result.success(existingDept);
        } catch (Exception e) {
            sysLogger.error("更新部门失败: " + e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "更新部门失败");
        }
    }

    @Override
    public Result<?> deleteDepartment(String id) {
        try {
            sysLogger.info("删除部门: id=" + id);
            
            // 检查是否有子部门
            boolean hasChildren = DEPARTMENTS.stream()
                    .anyMatch(dept -> id.equals(String.valueOf(dept.get("parentId"))));
            
            if (hasChildren) {
                return Result.failure(ErrorCode.BAD_REQUEST, "存在子部门，无法删除");
            }
            
            // 查找部门
            Optional<Map<String, Object>> departmentOpt = DEPARTMENTS.stream()
                    .filter(dept -> id.equals(String.valueOf(dept.get("id"))))
                    .findFirst();
            
            if (departmentOpt.isEmpty()) {
                return Result.failure(ErrorCode.NOT_FOUND, "部门不存在");
            }
            
            // 删除部门
            DEPARTMENTS.remove(departmentOpt.get());
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", id);
            result.put("deleted", true);
            
            return Result.success(result);
        } catch (Exception e) {
            sysLogger.error("删除部门失败: " + e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "删除部门失败");
        }
    }
} 