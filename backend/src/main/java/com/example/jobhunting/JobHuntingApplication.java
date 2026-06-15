package com.example.jobhunting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // ReminderService の @Scheduled バッチを有効化
public class JobHuntingApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobHuntingApplication.class, args);
    }
}
