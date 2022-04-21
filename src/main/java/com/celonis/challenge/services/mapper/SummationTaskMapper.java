package com.celonis.challenge.services.mapper;

import com.celonis.challenge.model.dto.SummationTaskDto;
import com.celonis.challenge.model.entities.SummationTaskEntity;
import com.celonis.challenge.services.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummationTaskMapper {
    private final TimeService timeService;
    public SummationTaskEntity createEntity(SummationTaskDto taskDto) {
        SummationTaskEntity summationTaskEntity = new SummationTaskEntity();
        summationTaskEntity.setInitialValue(taskDto.getInitialValue());
        summationTaskEntity.setLimitValue(taskDto.getLimitValue());
        summationTaskEntity.setDateStarted(timeService.currentDate());
        return summationTaskEntity;
    }

    public SummationTaskDto createDto(SummationTaskEntity summationTaskEntity) {
        SummationTaskDto summationTaskDto = new SummationTaskDto();
        summationTaskDto.setTaskId(summationTaskEntity.getId());
        summationTaskDto.setCurrentValue(summationTaskEntity.getCurrentValue());
        summationTaskDto.setInitialValue(summationTaskEntity.getInitialValue());
        summationTaskDto.setLimitValue(summationTaskEntity.getLimitValue());
        summationTaskDto.setSummationTaskStatus(summationTaskEntity.getStatus());
        return summationTaskDto;
    }
}
