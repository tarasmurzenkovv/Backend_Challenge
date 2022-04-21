package com.celonis.challenge.controllers;

import com.celonis.challenge.model.dto.SummationTaskDto;
import com.celonis.challenge.services.SummationTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class SummationTaskController {
    private final SummationTaskService summationTaskService;

    @PostMapping("/summation-task")
    public ResponseEntity<SummationTaskDto> create(@RequestBody SummationTaskDto taskDto) {
        SummationTaskDto summationTaskDto = summationTaskService.createTask(taskDto);
        return created(URI.create(
                        format("/api/tasks//summation-task/%s", taskDto.getTaskId())))
                .body(summationTaskDto);
    }

    @GetMapping("/summation-task/all")
    @ResponseStatus(OK)
    public List<SummationTaskDto> findAll() {
        return summationTaskService.findAll();
    }

    @GetMapping("/summation-task/{taskId}/result")
    @ResponseStatus(OK)
    public int getExecutionResult(@PathVariable @NotEmpty String taskId) {
        return summationTaskService.findResult(taskId);
    }

    @DeleteMapping("/summation-task/{taskId}")
    @ResponseStatus(NO_CONTENT)
    public void cancel(@PathVariable String taskId) {
        summationTaskService.cancelTask(taskId);
    }
}
