package com.asaki0019.enterprisemanagementsb.service.secutity;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.security.GetUserLogRequest;
import com.asaki0019.enterprisemanagementsb.request.security.UpdateRolePermissionRequest;

public interface SecurityService {
    Result<?> updateRolePermission(UpdateRolePermissionRequest request);

    Result<?> getSystemLog(String beginDate, String endDate);

    Result<?> getUserLog(GetUserLogRequest request);
}
