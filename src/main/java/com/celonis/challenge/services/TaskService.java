package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.entities.ProjectGenerationTask;
import com.celonis.challenge.repositories.ProjectGenerationTaskRepository;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
public class TaskService {

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    private final FileService fileService;

    public TaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository,
                       FileService fileService) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
        this.fileService = fileService;
    }

    public List<ProjectGenerationTask> listTasks() {
        return projectGenerationTaskRepository.findAll();
    }

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        projectGenerationTask.setId(null);
        projectGenerationTask.setCreationDate(new Date());
        return projectGenerationTaskRepository.save(projectGenerationTask);
    }

    public ProjectGenerationTask update(String taskId, ProjectGenerationTask projectGenerationTask) {
        ProjectGenerationTask existing = getTask(taskId);
        existing.setCreationDate(projectGenerationTask.getCreationDate());
        existing.setName(projectGenerationTask.getName());
        return projectGenerationTaskRepository.save(existing);
    }

    public void delete(String taskId) {
        projectGenerationTaskRepository.deleteById(taskId);
    }

    public void executeTask(String taskId) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
        if (url == null) {
            throw new NotFoundException(String.format("Zip file not found for task '%s'", taskId));
        }
        try {
            ProjectGenerationTask projectGenerationTask = projectGenerationTaskRepository.getOne(taskId);
            fileService.storeResult(taskId, url, projectGenerationTask);
        } catch (Exception e) {
            throw new InternalException(e);
        }
    }

    public ProjectGenerationTask getTask(String taskId) {
        return projectGenerationTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(String.format("Task with id '%s' is not found", taskId)));
    }
}
