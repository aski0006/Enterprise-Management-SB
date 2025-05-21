package com.asaki0019.enterprisemanagementsb.service.position;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.position.OnboardingApplicationRequest;
import com.asaki0019.enterprisemanagementsb.request.position.OnboardingApplicationResponse;

import java.util.List;
import java.util.Map;

public interface OnboardingApplicationService {
    Result<List<OnboardingApplicationResponse>> getAllApplications(String name);
    Result<OnboardingApplicationResponse> getApplicationById(Integer id);
    Result<?> createApplication(OnboardingApplicationRequest request);
    Result<?> updateApplicationStatus(Integer id, Map<String, String> request);
}
