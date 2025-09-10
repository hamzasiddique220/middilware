package com.backend.security.config;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class JobInitializer {

    private final JobScheduler jobScheduler;

    public JobInitializer(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void scheduleJob() {
        jobScheduler.enqueue(() -> System.out.println("🚀 JobRunr 7.3.2 is working!"));
    }
}

