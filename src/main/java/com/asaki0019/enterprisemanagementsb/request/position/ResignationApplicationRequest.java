package com.asaki0019.enterprisemanagementsb.request.position;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResignationApplicationRequest {
    private String name;
    private String department;
    private String position;
    private LocalDate resignDate;
    private String resignReason;
}
