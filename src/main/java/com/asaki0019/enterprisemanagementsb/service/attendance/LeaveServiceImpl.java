package com.asaki0019.enterprisemanagementsb.service.attendance;

import com.asaki0019.enterprisemanagementsb.entities.attendance.LeaveRecord;
import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.asaki0019.enterprisemanagementsb.enums.LeaveType;
import com.asaki0019.enterprisemanagementsb.repositories.LeaveRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveRecordRepository leaveRecordRepository;

    @Override
    @Transactional
    public LeaveRecord applyLeave(LeaveRecord leaveRecord) {
        leaveRecord.setStatus(ApprovalStatus.PENDING);
        return leaveRecordRepository.save(leaveRecord);
    }

    @Override
    public List<LeaveRecord> getEmployeeLeaveRecords(Employee employee) {
        return leaveRecordRepository.findByEmployeeOrderByStartDateDesc(employee);
    }

    @Override
    public List<LeaveRecord> getLeaveRecordsByType(LeaveType leaveType, Employee employee) {
        return leaveRecordRepository.findByLeaveTypeAndEmployee(leaveType, employee);
    }

    @Override
    public Page<LeaveRecord> getLeaveRecordsByStatus(ApprovalStatus status, Pageable pageable) {
        return leaveRecordRepository.findByStatus(status, pageable);
    }

    @Override
    public List<LeaveRecord> getLeaveRecordsByDateRange(Employee employee, LocalDate startDate, LocalDate endDate) {
        return leaveRecordRepository.findByEmployeeAndDateRange(employee, startDate, endDate);
    }
} 