package com.asaki0019.enterprisemanagementsb.controller.position;

import com.asaki0019.enterprisemanagementsb.core.annotation.RequiresPermission;
import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.position.ResignationApplicationRequest;
import com.asaki0019.enterprisemanagementsb.request.position.ResignationApplicationResponse;
import com.asaki0019.enterprisemanagementsb.service.position.ResignationApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resignations")
public class ResignationApplicationController {

    private final ResignationApplicationServiceImpl applicationService;

    @Autowired
    public ResignationApplicationController(ResignationApplicationServiceImpl applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    @RequiresPermission("employee:read")
    public Result<List<ResignationApplicationResponse>> getAllApplications(@RequestParam(required = false) String name) {
        return applicationService.getAllApplications(name);
    }

    @GetMapping("/{id}")
    @RequiresPermission("employee:read")
    public Result<ResignationApplicationResponse> getApplicationById(@PathVariable Integer id) {
        return applicationService.getApplicationById(id);
    }

    @PostMapping
    @RequiresPermission("employee:write")
    public Result<?> createApplication(@RequestBody ResignationApplicationRequest request) {
        return applicationService.createApplication(request);
    }

    @PutMapping("/{id}")
    @RequiresPermission("employee:write")
    public Result<?> updateApplicationStatus(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        return applicationService.updateApplicationStatus(id, request);
    }
}