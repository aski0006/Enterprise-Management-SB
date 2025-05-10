package com.asaki0019.enterprisemanagementsb.controller.position;

import com.asaki0019.enterprisemanagementsb.core.annotation.RequiresPermission;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.position.OnboardingApplicationRequest;
import com.asaki0019.enterprisemanagementsb.request.position.OnboardingApplicationResponse;
import com.asaki0019.enterprisemanagementsb.service.position.OnboardingApplicationServiceImpl;
import com.asaki0019.enterprisemanagementsb.service.position.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class OnboardingApplicationController {
    private final OnboardingApplicationServiceImpl applicationService;
    private final FileStorageService fileStorageService;

    @Autowired
    public OnboardingApplicationController(OnboardingApplicationServiceImpl applicationService,
                                           FileStorageService fileStorageService) {
        this.applicationService = applicationService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/employees")
    @RequiresPermission("employee:read")
    public Result<List<OnboardingApplicationResponse>> getAllApplications(@RequestParam(required = false) String name) {
        return applicationService.getAllApplications(name);
    }

    @GetMapping("/employees/{id}")
    @RequiresPermission("employee:read")
    public Result<OnboardingApplicationResponse> getApplicationById(@PathVariable Integer id) {
        return applicationService.getApplicationById(id);
    }

    @PostMapping("/employees")
    @RequiresPermission("employee:create")
    public Result<?> createApplication(@ModelAttribute OnboardingApplicationRequest request) {
        return applicationService.createApplication(request);
    }

    @PutMapping("/employees/{id}")
    @RequiresPermission("employee:approve")
    public Result<?> updateApplicationStatus(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        return applicationService.updateApplicationStatus(id, request);
    }

    @PostMapping("/upload")
    public Result<?> uploadFile(@RequestParam("file") MultipartFile file) {
        return fileStorageService.upload(file);
    }
}