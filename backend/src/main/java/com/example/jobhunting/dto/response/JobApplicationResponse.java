package com.example.jobhunting.dto.response;

import com.example.jobhunting.entity.ApplicationStatus;
import com.example.jobhunting.entity.JobApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record JobApplicationResponse(
        Long id,
        Long companyId,
        String companyName,   // 一覧表示で JOIN を避けるために埋め込む
        String jobTitle,
        ApplicationStatus status,
        LocalDate appliedDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static JobApplicationResponse from(JobApplication app) {
        return new JobApplicationResponse(
                app.getId(),
                app.getCompany().getId(),
                app.getCompany().getName(),
                app.getJobTitle(),
                app.getStatus(),
                app.getAppliedDate(),
                app.getCreatedAt(),
                app.getUpdatedAt()
        );
    }
}
