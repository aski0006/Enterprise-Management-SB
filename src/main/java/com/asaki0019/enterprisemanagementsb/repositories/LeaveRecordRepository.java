package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.attendance.LeaveRecord;
import com.asaki0019.enterprisemanagementsb.enums.ApprovalStatus;
import com.asaki0019.enterprisemanagementsb.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Long> {
    List<LeaveRecord> findByStatus(ApprovalStatus status);

    List<LeaveRecord> findByEmployee_EmployeeIdAndStartDateBetween(Integer employeeId, LocalDate start, LocalDate end);

    List<LeaveRecord> findByStatusAndLeaveType(ApprovalStatus status, LeaveType leaveType);

    List<LeaveRecord> findByEmployee_EmployeeIdAndStatusIn(Integer employeeId, List<ApprovalStatus> statusList);

    List<LeaveRecord> findByStatusIn(List<ApprovalStatus> statusList);
}