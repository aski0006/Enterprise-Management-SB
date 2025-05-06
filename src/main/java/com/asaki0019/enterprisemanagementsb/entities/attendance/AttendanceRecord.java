package com.asaki0019.enterprisemanagementsb.entities.attendance;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attendanceId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "record_date", nullable = false)
    private Date recordDate;

    @Column(name = "clock_in")
    private Date clockIn;

    @Column(name = "clock_out")
    private Date clockOut;

    @Column(name = "status", nullable = false, length = 10)
    private String status;
}