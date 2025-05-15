package com.asaki0019.enterprisemanagementsb.controller.position;

import com.asaki0019.enterprisemanagementsb.core.annotation.RequiresPermission;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.request.position.OnboardingApplicationRequest;
import com.asaki0019.enterprisemanagementsb.request.position.OnboardingApplicationResponse;
import com.asaki0019.enterprisemanagementsb.service.position.OnboardingApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OnboardingApplicationController {
    private final OnboardingApplicationServiceImpl applicationService;

    @Autowired
    public OnboardingApplicationController(OnboardingApplicationServiceImpl applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/api/employees")
    @RequiresPermission("employee:read")
    public Result<List<OnboardingApplicationResponse>> getAllApplications(@RequestParam(required = false) String name) {
        return applicationService.getAllApplications(name);
    }

    @GetMapping("/api/employees/{id}")
    @RequiresPermission("employee:read")
    public Result<OnboardingApplicationResponse> getApplicationById(@PathVariable Integer id) {
        return applicationService.getApplicationById(id);
    }

    @PostMapping("/api/employees")
    @RequiresPermission("employee:write")
    public Result<?> createApplication(@RequestBody OnboardingApplicationRequest request) {
        return applicationService.createApplication(request);
    }

    @PutMapping("/api/employees/{id}")
    @RequiresPermission("employee:write")
    public Result<?> updateApplicationStatus(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        return applicationService.updateApplicationStatus(id, request);
    }
}