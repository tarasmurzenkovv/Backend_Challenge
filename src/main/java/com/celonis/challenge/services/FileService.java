package com.celonis.challenge.services;

import com.celonis.challenge.model.entities.ProjectGenerationTask;
import com.celonis.challenge.repositories.ProjectGenerationTaskRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;

@Service
// there are multiple ways to fix spring boot cyclic dependency issue
// 1. autowire bean in setter: cons autowiring via setter is less prefferable that via constructor
// 2. declare bean as @Lazy: no need for lazy functionality
// 3. use @PostConstruct : cons this annotation is used to lately init bean
// 4. implement ApplicationContextAware and InitializingBean: cons -- overkill
// 5. refactor -- I will use this option
public class FileService {
    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    public FileService(ProjectGenerationTaskRepository projectGenerationTaskRepository) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
    }

    // TODO: should be replaced by URL
    public FileSystemResource getTaskResult(String fileLocation) {
        File inputFile = new File(fileLocation);

        if (!inputFile.exists()) {
            throw new IllegalArgumentException(String.format("File with file location '%s' not generated yet", fileLocation));
        }

        return new FileSystemResource(inputFile);
    }

    public void storeResult(String taskId, URL url, ProjectGenerationTask projectGenerationTask) throws IOException {
        File outputFile = File.createTempFile(taskId, ".zip");
        outputFile.deleteOnExit();
        projectGenerationTask.setStorageLocation(outputFile.getAbsolutePath());
        projectGenerationTaskRepository.save(projectGenerationTask);
        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }
    }
}
