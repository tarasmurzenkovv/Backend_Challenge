package com.celonis.challenge.repositories;

import com.celonis.challenge.model.entities.SummationTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummationTaskRepository extends JpaRepository<SummationTaskEntity, String> {

    @Modifying
    @Query("update SummationTaskEntity set status = com.celonis.challenge.model.entities.SummationTaskStatus.FINISHED" +
            " where currentValue = limitValue ")
    void markTasksAsDone();

    @Modifying
    @Query("update SummationTaskEntity " +
            "set currentValue = currentValue + 1, " +
            "status = com.celonis.challenge.model.entities.SummationTaskStatus.IN_PROCESS " +
            "where status = com.celonis.challenge.model.entities.SummationTaskStatus.STARTED or " +
            "status = com.celonis.challenge.model.entities.SummationTaskStatus.IN_PROCESS")
    void updateTasksCurrentValues();

    @Modifying
    @Query("update SummationTaskEntity " +
            "set status = com.celonis.challenge.model.entities.SummationTaskStatus.STOPPED " +
            "where id = :taskId ")
    void stopTask(@Param("taskId") String taskId);

    @Query(value = "select id " +
            "from summation_task " +
            "where DATEDIFF('DAY', NOW(), started_date) >=7 " +
            "and task_status = 'CANCELLED' ", nativeQuery = true)
    List<String> findTaskIdsThatAreStoppedWeekAgo();

    @Modifying
    @Query("delete from SummationTaskEntity where id in :taskIds ")
    void deleteTasks(@Param("taskIds") List<String> taskIds);
}
