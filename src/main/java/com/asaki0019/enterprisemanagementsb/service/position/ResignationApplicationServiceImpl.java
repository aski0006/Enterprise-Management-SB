package com.asaki0019.enterprisemanagementsb.service.position;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.employee.Position;
import com.asaki0019.enterprisemanagementsb.entities.permission.User;
import com.asaki0019.enterprisemanagementsb.entities.position.OnboardingApplication;
import com.asaki0019.enterprisemanagementsb.entities.position.ResignationApplication;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.repositories.EmployeeRepository;
import com.asaki0019.enterprisemanagementsb.repositories.UserRepository;
import com.asaki0019.enterprisemanagementsb.repositories.DepartmentRepository;
import com.asaki0019.enterprisemanagementsb.repositories.position.OnboardingApplicationRepository;
import com.asaki0019.enterprisemanagementsb.repositories.PositionRepository;
import com.asaki0019.enterprisemanagementsb.repositories.position.ResignationApplicationRepository;
import com.asaki0019.enterprisemanagementsb.request.position.ResignationApplicationRequest;
import com.asaki0019.enterprisemanagementsb.request.position.ResignationApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResignationApplicationServiceImpl implements ResignationApplicationService {

    private final ResignationApplicationRepository applicationRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final OnboardingApplicationRepository onboardingApplicationRepository;
    private final SysLogger logger;

    @Autowired
    public ResignationApplicationServiceImpl(
            ResignationApplicationRepository applicationRepository,
            DepartmentRepository departmentRepository,
            PositionRepository positionRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            OnboardingApplicationRepository onboardingApplicationRepository,
            SysLogger logger) {
        this.applicationRepository = applicationRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.onboardingApplicationRepository = onboardingApplicationRepository;
        this.logger = logger;
    }

    @Transactional
    @Override
    public Result<List<ResignationApplicationResponse>> getAllApplications(String name) {
        try {
            List<ResignationApplication> applications = name == null || name.isEmpty()
                    ? applicationRepository.findAll()
                    : applicationRepository.findByNameContainingIgnoreCase(name);
            List<ResignationApplicationResponse> responses = applications.stream()
                    .map(ResignationApplicationResponse::new)
                    .collect(Collectors.toList());
            logger.info("ResignationApplicationServiceImpl", "getAllApplications", "Fetched " + responses.size() + " applications");
            return Result.success(responses);
        } catch (Exception e) {
            logger.error("ResignationApplicationServiceImpl", "getAllApplications", "Failed to fetch applications", e);
            return new Result<List<ResignationApplicationResponse>>()
                    .setCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                    .setMessage("获取离职申请列表失败")
                    .setData(null);
        }
    }

    @Transactional
    @Override
    public Result<ResignationApplicationResponse> getApplicationById(Integer id) {
        try {
            ResignationApplication application = applicationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Application not found"));
            logger.info("ResignationApplicationServiceImpl", "getApplicationById", "Fetched application ID: " + id);
            return Result.success(new ResignationApplicationResponse(application));
        } catch (IllegalArgumentException e) {
            logger.warn("ResignationApplicationServiceImpl", "getApplicationById", "Application not found: " + id);
            return new Result<ResignationApplicationResponse>()
                    .setCode(ErrorCode.NOT_FOUND.getCode())
                    .setMessage("离职申请不存在")
                    .setData(null);
        } catch (Exception e) {
            logger.error("ResignationApplicationServiceImpl", "getApplicationById", "Failed to fetch application", e);
            return new Result<ResignationApplicationResponse>()
                    .setCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                    .setMessage("获取离职申请详情失败")
                    .setData(null);
        }
    }

    @Transactional
    @Override
    public Result<?> createApplication(ResignationApplicationRequest request) {
        try {
            // 验证员工是否存在
            String[] nameParts = request.getName().trim().split("\\s+");
            String firstName = nameParts.length > 0 ? nameParts[0] : request.getName();
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            Employee employee = employeeRepository.findByFirstNameAndLastName(firstName, lastName)
                    .orElse(null);
            if (employee == null) {
                logger.warn("ResignationApplicationServiceImpl", "createApplication", "Employee not found: " + request.getName());
                return Result.failure(ErrorCode.BAD_REQUEST, "员工不存在");
            }

            // 验证部门是否存在
            Department department = departmentRepository.findByName(request.getDepartment());
            if (department == null) {
                logger.warn("ResignationApplicationServiceImpl", "createApplication", "Department not found: " + request.getDepartment());
                return Result.failure(ErrorCode.BAD_REQUEST, "部门不存在");
            }

            // 验证职位是否存在
            Position position = positionRepository.findByTitle(request.getPosition());
            if (position == null) {
                logger.warn("ResignationApplicationServiceImpl", "createApplication", "Position not found: " + request.getPosition());
                return Result.failure(ErrorCode.BAD_REQUEST, "职位不存在");
            }

            // 创建离职申请
            ResignationApplication application = new ResignationApplication();
            application.setName(request.getName());
            application.setDepartment(department);
            application.setPosition(position);
            application.setResignDate(request.getResignDate());
            application.setResignReason(request.getResignReason());
            application.setApplyTime(LocalDateTime.now());
            application.setStatus(ApprovalStatus.PENDING);
            application.setEmployee(employee);

            applicationRepository.save(application);
            logger.info("ResignationApplicationServiceImpl", "createApplication", "Created application for: " + request.getName());
            return Result.success("离职申请提交成功");
        } catch (Exception e) {
            logger.error("ResignationApplicationServiceImpl", "createApplication", "Failed to create application", e);
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR, "提交离职申请失败");
        }
    }

    @Transactional
    @Override
    public Result<?> updateApplicationStatus(Integer id, Map<String, String> request) {
        try {
            // 查找离职申请
            ResignationApplication application = applicationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Application not found"));

            // 解析状态
            String statusStr = request.get("status");
            ApprovalStatus newStatus = switch (statusStr) {
                case "已通过" -> ApprovalStatus.APPROVED;
                case "已驳回" -> ApprovalStatus.REJECTED;
                default -> throw new IllegalArgumentException("无效的状态");
            };

            // 更新状态和驳回原因
            application.setStatus(newStatus);
            application.setRejectReason(newStatus == ApprovalStatus.REJECTED ? request.get("rejectReason") : null);

            // 如果状态为“已通过”，删除员工和用户
            if (newStatus == ApprovalStatus.APPROVED) {
                Employee employee = application.getEmployee();
                if (employee == null) {
                    logger.warn("ResignationApplicationServiceImpl", "updateApplicationStatus", "Employee not found for application ID: " + id);
                    return Result.failure(ErrorCode.BAD_REQUEST, "员工不存在");
                }

                // 清空 onboarding_applications 的 employee_id
                onboardingApplicationRepository.updateEmployeeToNull(employee.getEmployeeId());
                logger.info("ResignationApplicationServiceImpl", "updateApplicationStatus", "Cleared employee_id in onboarding_applications for employee ID: " + employee.getEmployeeId());

                // 清空 resignations 的 employee_id
                application.setEmployee(null);

                // 获取关联的用户
                User user = employee.getUser();
                if (user != null) {
                    userRepository.delete(user);
                    logger.info("ResignationApplicationServiceImpl", "updateApplicationStatus", "Deleted user ID: " + user.getUserId() + " for employee ID: " + employee.getEmployeeId());
                } else {
                    logger.warn("ResignationApplicationServiceImpl", "updateApplicationStatus", "No user found for employee ID: " + employee.getEmployeeId());
                }

                // 删除员工
                employeeRepository.delete(employee);
                logger.info("ResignationApplicationServiceImpl", "updateApplicationStatus", "Deleted employee ID: " + employee.getEmployeeId());
            }

            // 保存申请
            applicationRepository.save(application);
            logger.info("ResignationApplicationServiceImpl", "updateApplicationStatus", "Updated application ID: " + id + " to " + newStatus);
            return Result.success("状态更新成功");
        } catch (IllegalArgumentException e) {
            logger.warn("ResignationApplicationServiceImpl", "updateApplicationStatus", e.getMessage());
            return Result.failure(ErrorCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("ResignationApplicationServiceImpl", "updateApplicationStatus", "Failed to update application status", e);
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR, "更新状态失败");
        }
    }
}