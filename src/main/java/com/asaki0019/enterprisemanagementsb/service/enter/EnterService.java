package com.asaki0019.enterprisemanagementsb.service.enter;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.enter.LoginRequest;
import com.asaki0019.enterprisemanagementsb.request.enter.RegisterRequest;

import java.beans.Transient;

public interface EnterService {

    Result<?> Login(LoginRequest request);

    Result<?> Register(RegisterRequest request);
}
