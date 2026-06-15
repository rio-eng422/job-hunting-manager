package com.example.jobhunting.dto.response;

import com.example.jobhunting.entity.ApplicationStatus;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        Map<ApplicationStatus, Long> countByStatus,
        long totalApplications,
        long thisMonthApplications,
        List<SelectionStageResponse> upcomingStages
) {}
