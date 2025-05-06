package com.asaki0019.enterprisemanagementsb.entities.employee;

import com.asaki0019.enterprisemanagementsb.entities.leave.LeaveRecord;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "leave_types")
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer typeId;

    @Column(name = "type_name", nullable = false, unique = true, length = 50)
    private String typeName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    
    @OneToMany(mappedBy = "leaveType")
    private List<LeaveRecord> leaveRecords;
}