package com.alamin.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Configuration
public class TaskSchedule  {

    @Scheduled(cron = "0 0 0 * * *")
    public void runAtMidnight() {
        System.out.println("Task is running at midnight!");
    }

    // run at midnight on the last day of the month
    @Scheduled(cron = "0 0 0 L * ?")
    public void runOnLastDayOfMonth() {
        System.out.println("Task is running on the last day of the month at midnight!");
    }
}
