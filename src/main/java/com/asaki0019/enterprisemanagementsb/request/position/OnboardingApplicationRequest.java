package com.asaki0019.enterprisemanagementsb.request.position;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class OnboardingApplicationRequest {
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String phone;
    private String email;
    private String department;
    private String position;
    private LocalDate joinDate;
    private String idCard;
    private List<MultipartFile> idCardFile;
    private List<MultipartFile> contractFile;
}
