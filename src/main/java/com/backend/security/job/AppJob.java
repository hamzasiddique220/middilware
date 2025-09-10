package com.backend.security.job;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.backend.security.service.operation.OperationExecuterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppJob {

    private final OperationExecuterService operationExecuterService;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    private static boolean isJobRunning = false;


    @Async
    @Recurring(id = "App_01", cron = "*/1 * * * *")
    @Job(name = "Execute operation in thread")
    public void executeOperation() {
        if (isJobRunning) {
            log.warn("Job is already running, skipping this execution.");
            return;
        }

        isJobRunning = true;
        log.debug("Executing important operation " + dateFormat.format(new Date()));
        try {
            log.info("Job started at: {}", LocalDateTime.now());
            operationExecuterService.getImportantOperationByPriorityForExecution();
            Thread.sleep(10000);
            log.info("Job ended at: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error executing job: {}", e.getMessage(), e);
        } finally {
            isJobRunning = false;
        }
    }
    
}

