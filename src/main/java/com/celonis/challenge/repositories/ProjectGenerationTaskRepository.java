package com.celonis.challenge.repositories;

import com.celonis.challenge.model.entities.ProjectGenerationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectGenerationTaskRepository extends JpaRepository<ProjectGenerationTask, String> {
}
