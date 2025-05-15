package com.asaki0019.enterprisemanagementsb.request.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserLogRequest {
    String beginDate;
    String endDate;
    String username;
}