package com.asaki0019.enterprisemanagementsb.service.enter;

import com.asaki0019.enterprisemanagementsb.core.authContext.AuthContext;
import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.exception.BusinessException;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.asaki0019.enterprisemanagementsb.core.utils.JwtUtils;
import com.asaki0019.enterprisemanagementsb.mapper.enter.UserRepository;
import com.asaki0019.enterprisemanagementsb.repositories.*;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.department.Department;
import com.asaki0019.enterprisemanagementsb.entities.salary.Position;
import com.asaki0019.enterprisemanagementsb.entities.security.Role;
import com.asaki0019.enterprisemanagementsb.entities.security.Permission;
import com.asaki0019.enterprisemanagementsb.entities.security.RolePermission;
import com.asaki0019.enterprisemanagementsb.request.enter.LoginRequest;
import com.asaki0019.enterprisemanagementsb.request.enter.RegisterRequest;
import com.asaki0019.enterprisemanagementsb.entities.enter.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

/**
 * 服务类：处理进入相关业务逻辑
 */
@Service
@Transactional
public class EnterService {
    private final SysLogger sysLogger;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PositionRepository positionRepository;

    @Autowired
    public EnterService(SysLogger sysLogger, UserRepository userRepository, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository, PositionRepository positionRepository) {
        this.sysLogger = sysLogger;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.positionRepository = positionRepository;
    }

    public Result<?> loginService(@RequestBody LoginRequest request) {

        try {
            var username = request.getUsername();
            var password = request.getPassword();
            var user = userRepository.findByUsername(username);
            if (user == null) {
                sysLogger.warn(
                        MessageConstructor.constructPlainMessage(
                                "login",
                                "username", username,
                                "warning", "user not exists"
                        )
                );
                return Result.failure(ErrorCode.NOT_EXIST_USER);
            }
            if (!password.equals(user.getPassword())) {
                sysLogger.warn(
                        MessageConstructor.constructPlainMessage(
                                "login",
                                "username", username,
                                "warning", "password not match")
                );
                return Result.failure(ErrorCode.PASSWORD_NOT_MATCH);
            }
            // success:
            var permissionSet = new HashSet<String>();
            if(user.getRole().equals("admin")){
                permissionSet.add("admin");
                permissionSet.add("common");
            }
            else {
                permissionSet.add("common");
            }
            var token = JwtUtils.generateToken(username, user.getId(), permissionSet);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", user.getName());
            userData.put("role", user.getRole());
            data.put("user", userData);

            AuthContext.setUserId(user.getId());
            AuthContext.setPermissions(permissionSet);
            sysLogger.info(
                    MessageConstructor.constructPlainMessage(
                            "login",
                            "username", username,
                            "role", user.getRole(),
                            "token", token
                    )
            );
            // 根据角色更新权限
            updatePermissionsForUser(user.getRole(), permissionSet);
            return Result.success(data);
        } catch (BusinessException e) {
            sysLogger.error(
                    MessageConstructor.constructPlainMessage(
                            "login",
                            "username", request.getUsername(),
                            "error", e.getMessage()
                    ), e);
        }
        return Result.failure(ErrorCode.BUSINESS_ERROR);
    }

    private void updatePermissionsForUser(String roleName, HashSet<String> permissions) {
        // 查找或创建角色
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            role = new Role();
            role.setRoleName(roleName);
            role.setDescription("Auto created role for " + roleName);
            roleRepository.save(role);
        }
        // 清除现有权限
        rolePermissionRepository.deleteByRoleId(role.getRoleId());
        // 添加新权限
        for (String permissionCode : permissions) {
            Permission permission = permissionRepository.findByPermissionCode(permissionCode);
            if (permission == null) {
                permission = new Permission();
                permission.setPermissionCode(permissionCode);
                permission.setDescription("Auto created permission for " + permissionCode);
                permissionRepository.save(permission);
            }
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            rolePermissionRepository.save(rolePermission);
        }
    }

    public Result<?> registerService(@RequestBody RegisterRequest request) {
        // TODO：与数据库交互
        try {
            var username = request.getUsername();
            var password = request.getPassword();
            var confirmPassword = request.getConfirmPassword();
            var email = request.getEmail();
            var name = request.getName();
            var user = userRepository.findByUsername(username);
            if (user == null) {
                sysLogger.warn(
                        MessageConstructor.constructPlainMessage(
                                "register",
                                "username", username,
                                "warning", "username already exists"
                        )
                );
                return Result.failure(ErrorCode.EXIST_USER);
            }
            if (!password.equals(confirmPassword)) {
                sysLogger.warn(
                        MessageConstructor.constructPlainMessage(
                                "register",
                                "username", username,
                                "warning", "password not match")
                );
                return Result.failure(ErrorCode.PASSWORD_NOT_MATCH);
            }
            // success:
            var userModel = new UserModel();
            userModel.setUsername(username);
            userModel.setPassword(password);
            userModel.setEmail(email);
            userModel.setName(name);
            userModel.setRole("common");
            userRepository.save(userModel);
            sysLogger.info(
                    MessageConstructor.constructPlainMessage(
                            "register",
                            "username", username,
                            "role", userModel.getRole(),
                            "success", "register success"
                    )
            );
            // 创建基础员工记录
            var employee = new Employee();
            employee.setFirstName(name);
            employee.setLastName("");
            employee.setGender("未知");
            employee.setBirthDate(new Date());
            employee.setIdNumber("未提供");
            employee.setEmail(email);
            employee.setPhone("未提供");
            employee.setHireDate(new Date());
            employee.setEmploymentStatus("在职");
            employee.setBankAccount("未提供");
            employee.setPasswordHash(password);
            // 设置默认部门为 "基础部门"
            Department defaultDepartment = departmentRepository.findByName("基础部门");
            if (defaultDepartment == null) {
                defaultDepartment = new Department();
                defaultDepartment.setName("基础部门");
                departmentRepository.save(defaultDepartment);
            }
            employee.setDepartment(defaultDepartment);
            // 设置职位为 "普通员工"
            Position commonPosition = positionRepository.findByTitle("普通员工");
            if (commonPosition == null) {
                commonPosition = new Position();
                commonPosition.setTitle("普通员工");
                commonPosition.setJobDescription("普通员工职位");
                commonPosition.setBaseSalary(3000.0);
                // 设置默认部门
                commonPosition.setDepartment(defaultDepartment);
                positionRepository.save(commonPosition);
            }
            employee.setPosition(commonPosition);
            employeeRepository.save(employee);
            // 设置用户角色为 "Common"
            Role commonRole = roleRepository.findByRoleName("Common");
            if (commonRole == null) {
                commonRole = new Role();
                commonRole.setRoleName("Common");
                commonRole.setDescription("普通用户权限");
                roleRepository.save(commonRole);
            }
            return Result.success(null);
        } catch (BusinessException e) {
            sysLogger.error(
                    MessageConstructor.constructPlainMessage(
                            "register",
                            "username", request.getUsername(),
                            "error", e.getMessage()
                    ), e);
        }
        return Result.failure(ErrorCode.BUSINESS_ERROR);
    }
}