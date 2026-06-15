package com.example.jobhunting.controller;

import com.example.jobhunting.dto.response.DashboardResponse;
import com.example.jobhunting.dto.response.SelectionStageResponse;
import com.example.jobhunting.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ダッシュボード集計 API。
 *
 * 設計理由:
 * - Service 層の DashboardSummary（エンティティを含む）をここで DTO に変換する。
 *   SelectionStage → SelectionStageResponse の変換はコントローラー責務。
 *   サービス層が DTO を知らなくてよい状態を保つ。
 */
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final JobApplicationService jobApplicationService;

    @GetMapping("/api/dashboard")
    public DashboardResponse getDashboard() {
        var summary = jobApplicationService.getDashboardSummary();
        return new DashboardResponse(
                summary.countByStatus(),
                summary.totalApplications(),
                summary.thisMonthApplications(),
                summary.upcomingStages().stream()
                        .map(SelectionStageResponse::from)
                        .toList()
        );
    }
}
