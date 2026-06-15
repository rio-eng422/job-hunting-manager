package com.example.jobhunting.dto.request;

import com.example.jobhunting.entity.ApplicationStatus;
import com.example.jobhunting.entity.JobApplication;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record JobApplicationRequest(
        @NotNull(message = "企業IDは必須です")
        Long companyId,

        @Size(max = 100, message = "職種名は100文字以内で入力してください")
        String jobTitle,

        ApplicationStatus status,   // null → APPLIED をデフォルト適用

        LocalDate appliedDate
) {
    public JobApplication toEntity() {
        return JobApplication.builder()
                .jobTitle(jobTitle)
                .status(status != null ? status : ApplicationStatus.APPLIED)
                .appliedDate(appliedDate)
                .build();
    }
}
