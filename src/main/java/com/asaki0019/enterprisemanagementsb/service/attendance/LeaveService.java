package com.asaki0019.enterprisemanagementsb.service.attendance;

import com.asaki0019.enterprisemanagementsb.request.attendance.ApprovalRequestRequest;
import com.asaki0019.enterprisemanagementsb.request.attendance.LeaveRecordRequest;

import java.util.List;

public interface LeaveService {
    /**
     * 获取待处理的请假申请
     * @return 请假记录DTO列表
     */
    List<LeaveRecordRequest> getPendingLeaveApplications();

    /**
     * 获取历史请假记录
     * @return 请假记录DTO列表
     */
    List<LeaveRecordRequest> getLeaveHistory();

    /**
     * 处理请假/加班审批
     * @param request 审批请求DTO
     * @param approverId 审批人ID
     */
    void processLeaveApproval(ApprovalRequestRequest request, Long approverId);
}