package com.asaki0019.enterprisemanagementsb.service.attendance;

import com.asaki0019.enterprisemanagementsb.core.exception.BusinessException;
import com.asaki0019.enterprisemanagementsb.request.attendance.ApprovalRequestRequest;
import com.asaki0019.enterprisemanagementsb.request.attendance.LeaveRecordRequest;
import com.asaki0019.enterprisemanagementsb.entities.attendance.LeaveRecord;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.asaki0019.enterprisemanagementsb.enums.LeaveType;
import com.asaki0019.enterprisemanagementsb.repositories.EmployeeRepository;
import com.asaki0019.enterprisemanagementsb.repositories.LeaveRecordRepository;
import com.asaki0019.enterprisemanagementsb.service.attendance.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {
    private final LeaveRecordRepository leaveRecordRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<LeaveRecordRequest> getPendingLeaveApplications() {
        List<LeaveRecord> records = leaveRecordRepository.findByStatus(ApprovalStatus.PENDING);
        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaveRecordRequest> getLeaveHistory() {
        List<LeaveRecord> records = leaveRecordRepository.findByStatusIn(
                Arrays.asList(ApprovalStatus.APPROVED, ApprovalStatus.REJECTED));
        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processLeaveApproval(ApprovalRequestRequest request, Long approverId) {
        LeaveRecord record = leaveRecordRepository.findById(request.getRecordId())
                .orElseThrow(() -> new BusinessException("请假记录不存在"));

        Employee approver = employeeRepository.findById(approverId.intValue())
                .orElseThrow(() -> new BusinessException("审批人不存在"));

        // 使用switch处理不同操作
        switch (request.getAction()) {
            case APPROVE:
                record.approve(approver, request.getComments());
                break;
            case REJECT:
                record.reject(approver, request.getComments());
                break;
        }

        leaveRecordRepository.save(record);
    }

    private LeaveRecordRequest convertToDTO(LeaveRecord record) {
        LeaveRecordRequest dto = new LeaveRecordRequest();
        dto.setLeaveId(record.getLeaveId());
        dto.setEmployeeId(record.getEmployee().getEmployeeId());
        dto.setEmployeeName(record.getEmployee().getFirstName() + " " + record.getEmployee().getLastName());
        dto.setLeaveType(record.getLeaveType());
        dto.setStartDate(record.getStartDate());
        dto.setEndDate(record.getEndDate());
        dto.setStatus(record.getStatus().name());
        dto.setApproverId(record.getApprover() != null ? record.getApprover().getEmployeeId() : null);
        dto.setApproverName(record.getApprover() != null ?
                record.getApprover().getFirstName() + " " + record.getApprover().getLastName() : null);
        return dto;
    }
}