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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;

    // 获取待审批请假申请（只返回PENDING状态）
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingLeaveApplications() {
        List<LeaveRecordRequest> pendingLeaves = leaveService.getPendingLeaveApplications();

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "成功获取待审批请假列表");
        response.put("data", pendingLeaves);
        response.put("ITEMS", new HashMap<>());

        return ResponseEntity.ok(response);
    }

    // 获取历史请假记录（只返回APPROVED/REJECTED状态）
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getLeaveHistory() {
        List<LeaveRecordRequest> historyLeaves = leaveService.getLeaveHistory();

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "成功获取历史请假记录");
        response.put("data", historyLeaves);
        response.put("ITEMS", new HashMap<>());

        return ResponseEntity.ok(response);
    }
}
//    // 审批请假申请
//    @PostMapping("/approve")
//    public ResponseEntity<Void> approveLeaveApplication(
//            @RequestBody @Valid ApprovalRequestRequest request,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        Long userId = Long.parseLong(userDetails.getUsername()); // 改为Long
//        leaveService.processLeaveApproval(request, userId);
//        return ResponseEntity.ok().build();
//    }
