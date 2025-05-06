package com.asaki0019.enterprisemanagementsb.entities.system;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "api_logs")
public class ApiLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @Column(name = "request_time", nullable = false)
    private Date requestTime;

    @Column(name = "response_status", nullable = false)
    private Integer responseStatus;

    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;
}