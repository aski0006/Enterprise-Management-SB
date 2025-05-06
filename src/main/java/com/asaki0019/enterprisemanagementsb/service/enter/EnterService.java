package com.asaki0019.enterprisemanagementsb.service.enter;

import com.asaki0019.enterprisemanagementsb.core.authContext.AuthContext;
import com.asaki0019.enterprisemanagementsb.core.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.core.exception.BusinessException;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.JwtUtils;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.asaki0019.enterprisemanagementsb.entities.enter.UserModel;
import com.asaki0019.enterprisemanagementsb.mapper.enter.UserRepository;
import com.asaki0019.enterprisemanagementsb.request.enter.LoginRequest;
import com.asaki0019.enterprisemanagementsb.request.enter.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
@Transactional
public class EnterService {
    private final SysLogger sysLogger;
    private final UserRepository userRepository;
    @Autowired
    public EnterService(SysLogger sysLogger, UserRepository userRepository) {
        this.sysLogger = sysLogger;
        this.userRepository = userRepository;
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

    public Result<?> registerService(@RequestBody RegisterRequest request) {
        // TODO：与数据库交互
        try {
            var username = request.getUsername();
            var password = request.getPassword();
            var confirmPassword = request.getConfirmPassword();
            var email = request.getEmail();
            var name = request.getName();
            var user = userRepository.findByUsername(username);
            if (user != null) {
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
                                "warning", "password not match"
                        )
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