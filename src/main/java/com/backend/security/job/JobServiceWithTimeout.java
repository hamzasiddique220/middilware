package com.backend.security.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class JobServiceWithTimeout {

    private final AtomicBoolean isJobRunning = new AtomicBoolean(false);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void runJobWithTimeout() {
        if (isJobRunning.compareAndSet(false, true)) {
            try {
                System.out.println("Job started at: " + System.currentTimeMillis());

                // Submit the job to the executor service
                Future<?> future = executorService.submit(() -> {
                    try {
                        simulateLongRunningJob();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Job interrupted.");
                    }
                });

                // Wait for the job to complete with a timeout (e.g., 5 minutes)
                future.get(5, TimeUnit.MINUTES); // Timeout set to 5 minutes

                System.out.println("Job finished at: " + System.currentTimeMillis());
            } catch (TimeoutException e) {
                System.err.println("Job timed out.");
            } catch (Exception e) {
                System.err.println("Error during job execution: " + e.getMessage());
            } finally {
                isJobRunning.set(false); // Reset the flag after the job is done or timed out
            }
        } else {
            System.out.println("Job is already running, skipping execution.");
        }
    }

    // Simulate a long-running task (e.g., accessing external resources, processing large datasets)
    private void simulateLongRunningJob() throws InterruptedException {
        // Simulate a task that takes time (e.g., 10 seconds)
        TimeUnit.SECONDS.sleep(10); // You can adjust this value to test the timeout scenario
    }
}

