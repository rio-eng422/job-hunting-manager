package com.example.jobhunting.controller;

import com.example.jobhunting.dto.request.JobApplicationRequest;
import com.example.jobhunting.dto.request.StatusUpdateRequest;
import com.example.jobhunting.dto.response.JobApplicationResponse;
import com.example.jobhunting.entity.ApplicationStatus;
import com.example.jobhunting.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 応募リソースの CRUD。
 *
 * 設計理由:
 * - GET /api/applications?status=xxx: クエリパラメータで絞り込み。
 *   status が null なら全件、指定あれば絞り込み。1エンドポイントで2ユースケースをカバー。
 * - PATCH /api/applications/{id}/status: ステータス変更専用エンドポイント。
 *   PUT で全フィールドを送り直すより、変更したいフィールドだけ PATCH する方が
 *   クライアントの実装が楽でトラフィックも少ない。
 */
@RestController
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @GetMapping("/api/applications")
    public List<JobApplicationResponse> getApplications(
            @RequestParam(required = false) ApplicationStatus status) {
        var apps = (status != null)
                ? jobApplicationService.getApplicationsByStatus(status)
                : jobApplicationService.getAllApplications();
        return apps.stream().map(JobApplicationResponse::from).toList();
    }

    @GetMapping("/api/applications/{id}")
    public JobApplicationResponse getApplicationById(@PathVariable Long id) {
        return JobApplicationResponse.from(jobApplicationService.getApplicationById(id));
    }

    @PostMapping("/api/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplicationResponse createApplication(
            @Valid @RequestBody JobApplicationRequest request) {
        var saved = jobApplicationService.createApplication(
                request.companyId(), request.toEntity());
        return JobApplicationResponse.from(saved);
    }

    @PutMapping("/api/applications/{id}")
    public JobApplicationResponse updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationRequest request) {
        return JobApplicationResponse.from(
                jobApplicationService.updateApplication(id, request.toEntity()));
    }

    @PatchMapping("/api/applications/{id}/status")
    public JobApplicationResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return JobApplicationResponse.from(
                jobApplicationService.updateStatus(id, request.status()));
    }

    @DeleteMapping("/api/applications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApplication(@PathVariable Long id) {
        jobApplicationService.deleteApplication(id);
    }
}
