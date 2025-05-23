package com.asaki0019.enterprisemanagementsb.controller.attendance;

import com.asaki0019.enterprisemanagementsb.request.attendance.ApprovalRequestRequest;
import com.asaki0019.enterprisemanagementsb.request.attendance.LeaveRecordRequest;
import com.asaki0019.enterprisemanagementsb.service.attendance.LeaveService;
import com.asaki0019.enterprisemanagementsb.service.attendance.LeaveServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;

    // 获取待审批请假申请（只返回PENDING状态）
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRecordRequest>> getPendingLeaveApplications() {
        return ResponseEntity.ok(leaveService.getPendingLeaveApplications());
    }

    // 获取历史请假记录（只返回APPROVED/REJECTED状态）
    @GetMapping("/history")
    public ResponseEntity<List<LeaveRecordRequest>> getLeaveHistory(){
        return ResponseEntity.ok(leaveService.getLeaveHistory());
}

    // 审批请假申请
    @PostMapping("/approve")
    public ResponseEntity<Void> approveLeaveApplication(
            @RequestBody @Valid ApprovalRequestRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername()); // 改为Long
        leaveService.processLeaveApproval(request, userId);
        return ResponseEntity.ok().build();
    }
}