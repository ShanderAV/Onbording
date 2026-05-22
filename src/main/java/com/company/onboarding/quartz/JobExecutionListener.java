package com.company.onboarding.quartz;

import io.jmix.flowui.Notifications;
import jakarta.annotation.PostConstruct;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobExecutionListener extends JobListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobExecutionListener.class);
    private final Notifications notifications;

    @Autowired
    private Scheduler scheduler;

    public JobExecutionListener(Notifications notifications) {
        this.notifications = notifications;
    }

    @Override
    public String getName() {
        return "SampleJobExecutionListener";
    }

    @PostConstruct
    private void registerListener() {
        try {
            scheduler.getListenerManager().addJobListener(this);
        } catch (SchedulerException e) {
            log.error("Cannot register job listener", e);
        }
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context,
                               JobExecutionException jobException) {
        log.info("jobWasExecuted: name={}, context={}",
                context.getJobDetail().getKey().getName(), context);

    }
}
