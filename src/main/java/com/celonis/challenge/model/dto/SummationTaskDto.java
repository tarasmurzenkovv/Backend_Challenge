package com.celonis.challenge.model.dto;

import com.celonis.challenge.model.entities.SummationTaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummationTaskDto {
    private String taskId;
    private Integer initialValue, limitValue, currentValue;
    private SummationTaskStatus summationTaskStatus = SummationTaskStatus.STARTED;
}
