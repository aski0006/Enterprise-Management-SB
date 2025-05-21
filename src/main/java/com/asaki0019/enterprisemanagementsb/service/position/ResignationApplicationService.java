package com.asaki0019.enterprisemanagementsb.service.position;

import com.asaki0019.enterprisemanagementsb.request.position.ResignationApplicationRequest;
import com.asaki0019.enterprisemanagementsb.request.position.ResignationApplicationResponse;
import com.asaki0019.enterprisemanagementsb.core.model.Result;

import java.util.List;
import java.util.Map;

public interface ResignationApplicationService {
    Result<List<ResignationApplicationResponse>> getAllApplications(String name);
    Result<ResignationApplicationResponse> getApplicationById(Integer id);
    Result<?> createApplication(ResignationApplicationRequest request);
    Result<?> updateApplicationStatus(Integer id, Map<String, String> request);
}
