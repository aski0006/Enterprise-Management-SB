package com.asaki0019.enterprisemanagementsb.service.secutity;

import com.asaki0019.enterprisemanagementsb.core.annotation.RequiresPermission;
import com.asaki0019.enterprisemanagementsb.core.authContext.AuthContext;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.asaki0019.enterprisemanagementsb.entities.log.Log;
import com.asaki0019.enterprisemanagementsb.entities.permission.Permission;
import com.asaki0019.enterprisemanagementsb.entities.permission.Role;
import com.asaki0019.enterprisemanagementsb.entities.permission.User;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.enums.Logical;
import com.asaki0019.enterprisemanagementsb.repositories.LogRepository;
import com.asaki0019.enterprisemanagementsb.repositories.RoleRepository;
import com.asaki0019.enterprisemanagementsb.repositories.UserRepository;
import com.asaki0019.enterprisemanagementsb.request.security.GetUserLogRequest;
import com.asaki0019.enterprisemanagementsb.request.security.UpdateRolePermissionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SysLogger sysLogger;
    private final LogRepository logRepository; // 新增LogRepository

    @Autowired
    public SecurityServiceImpl(UserRepository userRepository,
                               RoleRepository roleRepository,
                               SysLogger sysLogger,
                               LogRepository logRepository) { // 修改构造函数
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sysLogger = sysLogger;
        this.logRepository = logRepository; // 初始化LogRepository
    }

    @Transient
    @Override
    @RequiresPermission(value = {"admin:all"}, logical = Logical.OR)
    public Result<?> updateRolePermission(UpdateRolePermissionRequest request) {
        User modifyUser = userRepository.findByUsername(request.getUsername());
        if (modifyUser == null) {
            sysLogger.warn("SecurityController", "UpdateRolePermissionRequest", "目标用户不存在");
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "目标用户不存在");
        }

        Role targetRole = roleRepository.findByRoleName(request.getRoleName());
        if (targetRole == null) {
            sysLogger.warn("SecurityController", "UpdateRolePermissionRequest",
                    MessageConstructor.constructParameterMessage("指定的角色不存在", request.getRoleName()));
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "指定的角色不存在");
        }
        var targetPermissions = targetRole.getPermissions();
        modifyUser.getRoles().clear();
        modifyUser.getRoles().add(targetRole);

        userRepository.save(modifyUser);
        sysLogger.info("SecurityController", "UpdateRolePermissionRequest",
                MessageConstructor.constructParameterMessage
                        ("修改角色", request.getUsername(),
                                "权限变更为", targetPermissions.stream().map(Permission::getPermissionCode).toList())
        );
        return Result.success(
                MessageConstructor.dataConversion("current Permissions", targetPermissions.stream().map(Permission::getPermissionCode).toList())
        );

    }


    @Override
    @RequiresPermission(value = {"admin:all"}, logical = Logical.OR)
    public Result<?> getSystemLog(String beginDate, String endDate) {
        var currentUser = userRepository.findById(AuthContext.getUserId());
        if (currentUser.isEmpty()) {
            sysLogger.warn("SecurityController", "GetSystemLog", "用户不存在");
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "用户不存在");
        }

        var currentUserRoles = currentUser.get().getRoles().stream()
                .map(Role::getRoleName)
                .toList();
        if (!currentUserRoles.contains("ADMIN")) {
            sysLogger.warn("SecurityController", "GetSystemLog", "当前用户没有权限");
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "当前用户没有权限");
        }

        if (beginDate == null || endDate == null) {
            sysLogger.warn("SecurityController", "GetSystemLog", "日期参数不能为空");
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "日期参数不能为空");
        }

        // 提取日期解析逻辑
        var dateRange = parseDateRange(beginDate, endDate);
        if (dateRange == null) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "日期格式应为yyyy-MM-dd");
        }

        List<Log> logs = logRepository.findByTimestampBetween(dateRange.startDate, dateRange.endDate);
        if (logs.isEmpty()) {
            sysLogger.warn("SecurityController", "GetSystemLog", "没有找到从" + beginDate + "到" + endDate + "的日志");
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "没有找到日志");
        }

        sysLogger.info("SecurityController", "GetSystemLog",
                MessageConstructor.constructParameterMessage(
                        "管理员在", new Date().toString(),
                        "查询了从" + beginDate + "到" + endDate + "的日志共", logs.size() + "条"
                ));
        return Result.success(logs);
    }

    @Override
    @RequiresPermission(value = {"admin:all"}, logical = Logical.OR)
    public Result<?> getUserLog(GetUserLogRequest request) {
        String username = request.getUsername();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        if (beginDate == null || endDate == null || username == null) {
            sysLogger.warn("SecurityController", "GetUserLog", "日期参数或用户ID不能为空");
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "日期参数或用户名不能为空");
        }

        var userOptional = userRepository.findByUsername(username);
        if (userOptional == null) {
            sysLogger.warn("SecurityController", "GetUserLog", MessageConstructor.constructParameterMessage("用户不存在", username));
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "用户不存在");
        }

        // 提取日期解析逻辑
        var dateRange = parseDateRange(beginDate, endDate);
        if (dateRange == null) {
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "日期格式应为yyyy-MM-dd");
        }

        List<Log> logs = logRepository.findByUserIdAndTimestampBetween(userOptional.getUserId(), dateRange.startDate, dateRange.endDate);
        if (logs.isEmpty()) {
            sysLogger.warn("SecurityController", "GetUserLog", MessageConstructor.constructParameterMessage(
                "没有找到用户", username, "从", beginDate, "到", endDate, "的日志"
            ));
            return Result.failure(ErrorCode.PARAM_VALIDATION_ERROR, "没有找到该用户的日志");
        }

        sysLogger.info("SecurityController", "GetUserLog",
                MessageConstructor.constructParameterMessage(
                        "管理员在", new Date().toString(),
                        "查询了用户", username,
                        "从", beginDate, "到", endDate, "的日志共", logs.size() + "条"
                ));

        return Result.success(
                MessageConstructor.dataConversion("logs", logs)
        );
    }

    // 新增私有方法：解析日期范围
    private DateRange parseDateRange(String beginDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        Date endDateParsed;
        try {
            startDate = sdf.parse(beginDate);
            endDateParsed = sdf.parse(endDate);

            // 将结束时间调整为当天的23:59:59.999
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDateParsed);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            endDateParsed = calendar.getTime();
        } catch (java.text.ParseException e) {
            sysLogger.warn("SecurityController", "DateParsing", "日期格式不正确");
            return null;
        }
        return new DateRange(startDate, endDateParsed);
    }

    // 新增内部类：日期范围
        private record DateRange(Date startDate, Date endDate) {

    }

}
