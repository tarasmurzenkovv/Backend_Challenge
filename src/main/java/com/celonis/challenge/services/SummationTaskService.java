package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.dto.SummationTaskDto;
import com.celonis.challenge.model.entities.SummationTaskEntity;
import com.celonis.challenge.repositories.SummationTaskRepository;
import com.celonis.challenge.services.mapper.SummationTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SummationTaskService {
    private final SummationTaskRepository summationTaskRepository;
    private final SummationTaskMapper summationTaskMapper;
    // TODO: add validations for nullable id
    // TODO: add validations for nullable status
    // TODO: add validations for unique initial and limit values
    @Transactional
    public SummationTaskDto createTask(SummationTaskDto taskDto) {
        SummationTaskEntity entity = summationTaskMapper.createEntity(taskDto);
        SummationTaskEntity savedTask = summationTaskRepository.save(entity);
        return summationTaskMapper.createDto(savedTask);
    }

    @Transactional
    public void cancelTask(String taskId) {
        summationTaskRepository.stopTask(taskId);
    }
    @Transactional(readOnly = true)
    public int findResult(String taskId) {
        return summationTaskRepository.findById(taskId)
                .map(SummationTaskEntity::getCurrentValue)
                .orElseThrow(() -> new NotFoundException(String.format("Task with id '%s' is not found", taskId)));
    }

    @Transactional(readOnly = true)
    public List<SummationTaskDto> findAll() {
        return summationTaskRepository.findAll()
                .stream()
                .map(summationTaskMapper::createDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearFinishedWeekAgoTasks() {
        List<String> taskIdsThatAreStoppedWeekAgo = summationTaskRepository.findTaskIdsThatAreStoppedWeekAgo();
        summationTaskRepository.deleteTasks(taskIdsThatAreStoppedWeekAgo);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateCurrentResults() {
        summationTaskRepository.updateTasksCurrentValues();
        summationTaskRepository.markTasksAsDone();
    }
}

