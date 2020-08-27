package com.example.kafka.configuration;

import com.example.kafka.schedule.Scheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class ContextConfigurationScheduling {

    @Bean
    public ScheduledTaskRegistrar scheduledTaskRegistrar() {
        return new ScheduledTaskRegistrar();
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolPendingSavings");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public Scheduling scheduling(ScheduledTaskRegistrar scheduledTaskRegistrar, ThreadPoolTaskScheduler taskScheduler) {
        return new Scheduling(scheduledTaskRegistrar, taskScheduler);
    }
}
