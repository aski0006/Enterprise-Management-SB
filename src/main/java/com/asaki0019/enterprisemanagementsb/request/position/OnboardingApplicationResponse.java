package com.asaki0019.enterprisemanagementsb.request.position;

import com.asaki0019.enterprisemanagementsb.entities.position.OnboardingApplication;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OnboardingApplicationResponse {
    private Integer id;
    private String name;
    private String department;
    private String position;
    private LocalDateTime applyTime;
    private String status;
    private String gender;
    private LocalDate birthDate;
    private String phone;
    private String email;
    private LocalDate joinDate;
    private String idCard;
    private List<FileInfo> idCardFile;
    private List<FileInfo> contractFile;
    private String rejectReason;

    @Data
    public static class FileInfo {
        private String name;
        private String url;
    }

    public OnboardingApplicationResponse(OnboardingApplication application) {
        this.id = application.getId();
        this.name = application.getFirstName() + " " + application.getLastName();
        this.department = application.getDepartment().getName();
        this.position = application.getPosition().getTitle();
        this.applyTime = application.getApplyTime();
        this.status = mapStatus(application.getStatus());
        this.gender = application.getGender();
        this.birthDate = application.getBirthDate();
        this.phone = application.getPhone();
        this.email = application.getEmail();
        this.joinDate = application.getJoinDate();
        this.idCard = application.getIdNumber();
        this.idCardFile = application.getIdCardFiles().stream()
                .map(f -> {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setName(f.getName());
                    fileInfo.setUrl(f.getUrl());
                    return fileInfo;
                }).toList();
        this.contractFile = application.getContractFiles().stream()
                .map(f -> {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setName(f.getName());
                    fileInfo.setUrl(f.getUrl());
                    return fileInfo;
                }).toList();
        this.rejectReason = application.getRejectReason();
    }

    private String mapStatus(ApprovalStatus status) {
        return switch (status) {
            case PENDING -> "待审批";
            case APPROVED -> "已通过";
            case REJECTED -> "已驳回";
        };
    }
}