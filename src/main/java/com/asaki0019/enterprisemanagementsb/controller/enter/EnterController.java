package com.asaki0019.enterprisemanagementsb.controller.enter;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.request.enter.LoginRequest;
import com.asaki0019.enterprisemanagementsb.request.enter.RegisterRequest;
import com.asaki0019.enterprisemanagementsb.service.enter.EnterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/enter")
public class EnterController {

    private final EnterService enterService;

    @Autowired
    public EnterController(EnterService enterService) {
        this.enterService = enterService;
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest request) {
        return enterService.loginService(request);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterRequest request) {
        return enterService.registerService(request);
    }

}