package com.asaki0019.enterprisemanagementsb.request.position;

import com.asaki0019.enterprisemanagementsb.entities.position.ResignationApplication;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ResignationApplicationResponse {
    private Integer id;
    private String name;
    private String department;
    private String position;
    private LocalDate resignDate;
    private String resignReason;
    private LocalDateTime applyTime;
    private String status;
    private String rejectReason;

    public ResignationApplicationResponse(ResignationApplication application) {
        this.id = application.getId();
        this.name = application.getName();
        this.department = application.getDepartment().getName();
        this.position = application.getPosition().getTitle();
        this.resignDate = application.getResignDate();
        this.resignReason = application.getResignReason();
        this.applyTime = application.getApplyTime();
        this.status = switch (application.getStatus()) {
            case PENDING -> "待审批";
            case APPROVED -> "已通过";
            case REJECTED -> "已驳回";
        };
        this.rejectReason = application.getRejectReason();
    }
}
