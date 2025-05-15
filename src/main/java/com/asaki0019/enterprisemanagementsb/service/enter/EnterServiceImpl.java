package com.asaki0019.enterprisemanagementsb.service.enter;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.JwtUtils;
import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.employee.Position;
import com.asaki0019.enterprisemanagementsb.entities.permission.Permission;
import com.asaki0019.enterprisemanagementsb.entities.permission.Role;
import com.asaki0019.enterprisemanagementsb.entities.permission.User;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.repositories.*;
import com.asaki0019.enterprisemanagementsb.request.enter.LoginRequest;
import com.asaki0019.enterprisemanagementsb.request.enter.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EnterServiceImpl implements EnterService {
    private final UserRepository userRepository;
    private final SysLogger logger;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public EnterServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            DepartmentRepository departmentRepository,
                            PositionRepository positionRepository,
                            EmployeeRepository employeeRepository,
                            PermissionRepository permissionRepository,
                            SysLogger logger) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.employeeRepository = employeeRepository;
        this.permissionRepository = permissionRepository;
        this.logger = logger;
    }

    @Transactional
    @Override
    public Result<?> Login(LoginRequest request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            if (username == null || password == null) {
                logger.warn("EnterServiceImpl", "Login", "用户名或密码为空");
                return Result.failure(ErrorCode.BAD_REQUEST, "用户名或密码为空");
            }

            User user = userRepository.findByUsername(username);
            if (user == null) {
                logger.warn("EnterServiceImpl", "Login", "用户不存在");
                return Result.failure(ErrorCode.BAD_REQUEST, "用户不存在");
            }
            if (!user.getPassword().equals(password)) {
                logger.warn("EnterServiceImpl", "Login", "密码错误");
                return Result.failure(ErrorCode.BAD_REQUEST, "密码错误");
            }
            var permissions = user.getRoles().stream().map(
                    role -> role.getPermissions().stream().map(
                            Permission::getPermissionCode
                    ).collect(Collectors.toList())
            ).toList();
            var hashSetPermissions = new HashSet<String>();
            permissions.forEach(hashSetPermissions::addAll);
            String token = JwtUtils.generateToken(user.getUsername(), user.getUserId(), hashSetPermissions);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            logger.info("EnterServiceImpl", "Login", username + " 登录成功" +
                    ", 权限: " + hashSetPermissions.stream().toList());
            return Result.success(data);
        } catch (Exception e) {
            logger.error("EnterServiceImpl", "Login", "登录失败", e);
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR, "登录失败");
        }
    }

    @Transactional
    @Override
    public Result<?> Register(RegisterRequest request) {
        try {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                logger.warn("EnterServiceImpl", "Register", "密码和确认密码不一致");
                return Result.failure(ErrorCode.BAD_REQUEST, "密码和确认密码不一致");
            }
            if (userRepository.existsByUsername(request.getUsername())) {
                logger.warn("EnterServiceImpl", "Register", "用户名已存在");
                return Result.failure(ErrorCode.BAD_REQUEST, "用户名已被注册");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("EnterServiceImpl", "Register", "邮箱已存在");
                return Result.failure(ErrorCode.BAD_REQUEST, "邮箱已被使用");
            }
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setName(request.getName());

            Role defaultRole = roleRepository.findByRoleName("USER");
            if (defaultRole == null) {

                Permission viewProfilePerm = permissionRepository.findByPermissionCode("user:read");
                if (viewProfilePerm == null) {
                    viewProfilePerm = new Permission();
                    viewProfilePerm.setPermissionCode("user:read");
                    viewProfilePerm.setDescription("允许查看基础信息");
                    permissionRepository.save(viewProfilePerm);
                }

                defaultRole = new Role();
                defaultRole.setRoleName("USER");
                defaultRole.setDescription("默认角色");
                defaultRole.setPermissions(new HashSet<>(Set.of(viewProfilePerm))); // 添加权限
                roleRepository.save(defaultRole);
            }
            user.getRoles().add(defaultRole);
            userRepository.save(user);

            Department defaultDept = departmentRepository.findByName("游客");
            if (defaultDept == null) {
                defaultDept = new Department();
                defaultDept.setName("游客");
                departmentRepository.save(defaultDept);
            }
            Position defaultPosition = positionRepository.findByTitle("游客");
            if (defaultPosition == null) {
                defaultPosition = new Position();
                defaultPosition.setTitle("游客");
                defaultPosition.setDepartment(defaultDept);
                positionRepository.save(defaultPosition);
            }

            Employee employee = new Employee();
            employee.setUser(user);
            employee.setDepartment(defaultDept);
            employee.setPosition(defaultPosition);
            employee.setFirstName(user.getName()); // 示例拆分姓名
            employee.setLastName(user.getName());
            employee.setGender("未知");
            employee.setBirthDate(LocalDate.of(1990, 1, 1)); // 默认生日
            employee.setIdNumber("N/A"); // 默认身份证号
            employeeRepository.save(employee);

            logger.info("EnterServiceImpl", "Register", "注册成功");
            return Result.success("注册成功");

        } catch (Exception e) {
            logger.error("EnterServiceImpl", "Register", "注册失败", e);
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR, "注册失败");
        }
    }
}