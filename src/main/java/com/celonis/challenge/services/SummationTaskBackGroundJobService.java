package com.celonis.challenge.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.SECONDS;

@Service
@RequiredArgsConstructor
public class SummationTaskBackGroundJobService {
    private final TaskScheduler summationTaskScheduler;
    private final SummationTaskService summationTaskService;
    @EventListener(ContextRefreshedEvent.class)
    public void cleanStoppedWeekAgoTask() {
        summationTaskScheduler.schedule(summationTaskService::clearFinishedWeekAgoTasks,
                new PeriodicTrigger(1, SECONDS));
    }
    @EventListener(ContextRefreshedEvent.class)
    public void executeSummation() {
        summationTaskScheduler.schedule(summationTaskService::updateCurrentResults,
                new PeriodicTrigger(1, SECONDS));
    }
}

