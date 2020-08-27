package com.example.kafka.schedule;

import com.example.kafka.document.PendingEvents;
import com.example.kafka.event.LibraryEvent;
import com.example.kafka.producer.LibraryEventProducer;
import com.example.kafka.repository.PendingEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Optional;
import java.util.TimeZone;

public class Scheduling implements SchedulingConfigurer {

    private ScheduledTaskRegistrar scheduledTaskRegistrar;

    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private PendingEventRepository pendingEvents;

    @Autowired
    private LibraryEventProducer producer;

    private String cron = "0 * * ? * *";

    public Scheduling(ScheduledTaskRegistrar scheduledTaskRegistrar, ThreadPoolTaskScheduler taskScheduler) {
        this.scheduledTaskRegistrar = scheduledTaskRegistrar;
        this.taskScheduler = taskScheduler;
        configureTasks(scheduledTaskRegistrar);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.scheduledTaskRegistrar.setScheduler(taskScheduler);
        CronTrigger croneTrigger = null;
        try {
            croneTrigger = new CronTrigger(cron, TimeZone.getDefault());
            this.scheduledTaskRegistrar.getScheduler().schedule(() -> scheduleCron(), croneTrigger);
        } catch (IllegalArgumentException err) {
            throw new IllegalArgumentException("Invalid CRON expression " + err.getMessage(), err);
        }
    }

    public void scheduleCron()  {
        Optional<LibraryEvent> lastCreated = Optional.ofNullable(pendingEvents.findLastCreated());
        if(lastCreated.isPresent()){
            try {
                producer.sendLibraryPendingEvent(lastCreated.get());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
