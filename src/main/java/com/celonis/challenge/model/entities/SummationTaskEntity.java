package com.celonis.challenge.model.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "summation_task")
@Getter
@Setter
public class SummationTaskEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "initial_value", nullable = false)
    private int initialValue;

    @Column(name = "current_value", nullable = false)
    private int currentValue;

    @Column(name = "limit_value", nullable = false)
    private int limitValue;

    @Column(name = "started_date", nullable = false)
    private LocalDate dateStarted = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    private SummationTaskStatus status = SummationTaskStatus.STARTED;
}
