package com.asaki0019.enterprisemanagementsb.entities.performance;

import com.asaki0019.enterprisemanagementsb.entities.employee.Employee;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "performance_evaluations")
public class PerformanceEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer evaluationId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "evaluator_id", nullable = false)
    private Employee evaluator;

    @Column(name = "evaluation_date", nullable = false)
    private Date evaluationDate;

    @Column(name = "kpi_score", precision = 5)
    private Double kpiScore;

    @Column(name = "competency_score", precision = 5)
    private Double competencyScore;

    @Column(name = "overall_rating", length = 1)
    private String overallRating;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;
}