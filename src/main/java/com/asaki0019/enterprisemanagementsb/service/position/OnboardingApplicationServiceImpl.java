package com.asaki0019.enterprisemanagementsb.service.position;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.entities.employee.Position;
import com.asaki0019.enterprisemanagementsb.entities.position.OnboardingApplication;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.repositories.*;
import com.asaki0019.enterprisemanagementsb.request.position.OnboardingApplicationRequest;
import com.asaki0019.enterprisemanagementsb.request.position.OnboardingApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OnboardingApplicationServiceImpl implements OnboardingApplicationService {
    private final OnboardingApplicationRepository applicationRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final SysLogger logger;
    private final FileStorageService fileStorageService;

    @Autowired
    public OnboardingApplicationServiceImpl(OnboardingApplicationRepository applicationRepository,
                                            DepartmentRepository departmentRepository,
                                            PositionRepository positionRepository,
                                            EmployeeRepository employeeRepository,
                                            SysLogger logger,
                                            FileStorageService fileStorageService) {
        this.applicationRepository = applicationRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.employeeRepository = employeeRepository;
        this.logger = logger;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public Result<List<OnboardingApplicationResponse>> getAllApplications(String name) {
        try {
            List<OnboardingApplication> applications = name == null || name.isEmpty()
                    ? applicationRepository.findAll()
                    : applicationRepository.findByNameContaining(name);
            List<OnboardingApplicationResponse> responses = applications.stream()
                    .map(OnboardingApplicationResponse::new)
                    .collect(Collectors.toList());
            logger.info("OnboardingApplicationServiceImpl", "getAllApplications", "Fetched " + responses.size() + " applications");
            return Result.success(responses);
        } catch (Exception e) {
            logger.error("OnboardingApplicationServiceImpl", "getAllApplications", "Failed to fetch applications", e);
            return (Result<List<OnboardingApplicationResponse>>) Result.failure(ErrorCode.INTERNAL_SERVER_ERROR, "获取入职申请列表失败");
        }
    }

    @Transactional(readOnly = true)
    public Result<OnboardingApplicationResponse> getApplicationById(Integer id) {
        try {
            OnboardingApplication application = applicationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Application not found"));
            logger.info("OnboardingApplicationServiceImpl", "getApplicationById", "Fetched application ID: " + id);
            return Result.success(new OnboardingApplicationResponse(application));
        } catch (IllegalArgumentException e) {
            logger.warn("OnboardingApplicationServiceImpl", "getApplicationById", "Application not found: " + id);
            return (Result<OnboardingApplicationResponse>) Result.failure(ErrorCode.NOT_FOUND, "入职申请不存在");
        } catch (Exception e) {
            logger.error("OnboardingApplicationServiceImpl", "getApplicationById", "Failed to fetch application", e);
            return (Result<OnboardingApplicationResponse>) Result.failure(ErrorCode.INTERNAL_SERVER_ERROR, "获取入职申请详情失败");
        }
    }

    @Transactional
    public Result<?> createApplication(OnboardingApplicationRequest request) {
        try {
            if (!request.getIdCard().matches("^\\d{17}[\\dXx]$")) {
                return Result.failure(ErrorCode.BAD_REQUEST, "请输入有效的身份证号码");
            }

            Department department = departmentRepository.findByName(request.getDepartment());
            if (department == null) {
                department = new Department();
                department.setName(request.getDepartment());
                department = departmentRepository.save(department);
            }

            Position position = positionRepository.findByTitle(request.getPosition());
            if (position == null) {
                position = new Position();
                position.setTitle(request.getPosition());
                position.setDepartment(department);
                position = positionRepository.save(position);
            }

            String[] nameParts = request.getName().trim().split("\\s+");
            String firstName = nameParts.length > 0 ? nameParts[0] : request.getName();
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            OnboardingApplication application = new OnboardingApplication();
            application.setFirstName(firstName);
            application.setLastName(lastName);
            application.setGender(request.getDepartment());
            application.setBirthDate(request.getBirthDate());
            application.setIdNumber(request.getIdCard());
            application.setPhone(request.getPhone());
            application.setEmail(request.getEmail());
            application.setDepartment(department);
            application.setPosition(position);
            application.setJoinDate(request.getJoinDate());
            application.setApplyTime(LocalDateTime.now());
            application.setStatus(ApprovalStatus.PENDING);

            if (request.getIdCardFile() != null && !request.getIdCardFile().isEmpty()) {
                if (request.getIdCardFile().size() > 2) {
                    return Result.failure(ErrorCode.BAD_REQUEST, "身份证照片最多上传2张");
                }
                application.setIdCardFiles(uploadFiles(request.getIdCardFile(), "id_card"));
            }
            if (request.getContractFile() != null && !request.getContractFile().isEmpty()) {
                if (request.getContractFile().size() > 1) {
                    return Result.failure(ErrorCode.BAD_REQUEST, "劳动合同最多上传1份");
                }
                application.setContractFiles(uploadFiles(request.getContractFile(), "contract"));
            }

            applicationRepository.save(application);
            logger.info("OnboardingApplicationServiceImpl", "createApplication", "Created application for: " + firstName);
            return Result.success("入职申请提交成功");
        } catch (Exception e) {
            logger.error("OnboardingApplicationServiceImpl", "createApplication", "Failed to create application", e);
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR, "提交入职申请失败");
        }
    }

    @Transactional
    public Result<?> updateApplicationStatus(Integer id, Map<String, String> request) {
        try {
            OnboardingApplication application = applicationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Application not found"));
            String statusStr = request.get("status");
            ApprovalStatus newStatus = switch (statusStr) {
                case "已通过" -> ApprovalStatus.APPROVED;
                case "已驳回" -> ApprovalStatus.REJECTED;
                default -> throw new IllegalArgumentException("无效的状态");
            };
            application.setStatus(newStatus);
            application.setRejectReason(newStatus == ApprovalStatus.REJECTED ? request.get("rejectReason") : null);

            if (newStatus == ApprovalStatus.APPROVED) {
                Employee employee = new Employee();
                employee.setFirstName(application.getFirstName());
                employee.setLastName(application.getLastName());
                employee.setGender(application.getGender());
                employee.setBirthDate(application.getBirthDate());
                employee.setIdNumber(application.getIdNumber());
                employee.setDepartment(application.getDepartment());
                employee.setPosition(application.getPosition());
                // Note: user is nullable, so not set
                employeeRepository.save(employee);
                application.setEmployee(employee);
            }

            applicationRepository.save(application);
            logger.info("OnboardingApplicationServiceImpl", "updateApplicationStatus", "Updated application ID: " + id + " to " + newStatus);
            return Result.success("状态更新成功");
        } catch (IllegalArgumentException e) {
            logger.warn("OnboardingApplicationServiceImpl", "updateApplicationStatus", e.getMessage());
            return Result.failure(ErrorCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("OnboardingApplicationServiceImpl", "updateApplicationStatus", "Failed to update application status", e);
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR, "更新状态失败");
        }
    }

    private List<OnboardingApplication.FileInfo> uploadFiles(List<MultipartFile> files, String type) {
        return files.stream().map(file -> {
            String url = fileStorageService.uploadFile(file, type);
            OnboardingApplication.FileInfo fileInfo = new OnboardingApplication.FileInfo();
            fileInfo.setName(file.getOriginalFilename());
            fileInfo.setUrl(url);
            return fileInfo;
        }).toList();
    }
}

interface OnboardingApplicationService {
    Result<List<OnboardingApplicationResponse>> getAllApplications(String name);
    Result<OnboardingApplicationResponse> getApplicationById(Integer id);
    Result<?> createApplication(OnboardingApplicationRequest request);
    Result<?> updateApplicationStatus(Integer id, Map<String, String> request);
}