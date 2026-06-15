package com.example.jobhunting.service;

import com.example.jobhunting.entity.ApplicationStatus;
import com.example.jobhunting.entity.Company;
import com.example.jobhunting.entity.JobApplication;
import com.example.jobhunting.entity.StageResult;
import com.example.jobhunting.exception.ResourceNotFoundException;
import com.example.jobhunting.repository.ApplicationStatusCount;
import com.example.jobhunting.repository.CompanyRepository;
import com.example.jobhunting.repository.JobApplicationRepository;
import com.example.jobhunting.repository.SelectionStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 応募管理のビジネスロジック。就活管理ツールの中核サービス。
 *
 * 設計理由:
 * - createApplication(Long companyId, ...) で会社 ID を受け取る理由:
 *   クライアントは会社 ID だけ知っていれば良く、サービス層が Company エンティティを
 *   ロードして紐づけることで、Controller が JPA エンティティの内部構造を知らなくて済む。
 * - updateStatus を updateApplication とは別メソッドにした理由:
 *   「ステータスだけ変える」操作は頻繁に起こる（Kanban ドラッグ&ドロップなど）。
 *   専用メソッドにしておくと API 設計も PATCH /applications/{id}/status と
 *   PUT /applications/{id} に自然に分けられる。
 * - getDashboardSummary に @Transactional(readOnly = true) を使う理由:
 *   複数リポジトリを呼ぶため、同一トランザクション内で一貫したスナップショットを読む。
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final SelectionStageRepository stageRepository;

    public List<JobApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    public JobApplication getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", id));
    }

    public List<JobApplication> getApplicationsByCompany(Long companyId) {
        return applicationRepository.findByCompanyId(companyId);
    }

    public List<JobApplication> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status);
    }

    @Transactional
    public JobApplication createApplication(Long companyId, JobApplication application) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));
        application.setCompany(company);
        return applicationRepository.save(application);
    }

    @Transactional
    public JobApplication updateApplication(Long id, JobApplication updated) {
        JobApplication application = getApplicationById(id);
        application.setJobTitle(updated.getJobTitle());
        application.setStatus(updated.getStatus());
        application.setAppliedDate(updated.getAppliedDate());
        return applicationRepository.save(application);
    }

    // ステータスのみ更新（Kanban 操作・選考通過/不合格 の即時反映用）
    @Transactional
    public JobApplication updateStatus(Long id, ApplicationStatus status) {
        JobApplication application = getApplicationById(id);
        application.setStatus(status);
        return applicationRepository.save(application);
    }

    @Transactional
    public void deleteApplication(Long id) {
        JobApplication application = getApplicationById(id);
        applicationRepository.delete(application);
    }

    public DashboardSummary getDashboardSummary() {
        // ステータス別件数をプロジェクションから Map に変換
        Map<ApplicationStatus, Long> countByStatus = applicationRepository.countGroupByStatus()
                .stream()
                .collect(Collectors.toMap(
                        ApplicationStatusCount::getStatus,
                        ApplicationStatusCount::getCount
                ));

        long total = applicationRepository.count();

        // 今月の応募件数
        LocalDate firstOfMonth = YearMonth.now().atDay(1);
        LocalDate lastOfMonth  = YearMonth.now().atEndOfMonth();
        long thisMonth = applicationRepository.countByAppliedDateBetween(firstOfMonth, lastOfMonth);

        // 直近の未定ステージ（予定日の近い順に最大 5 件）
        var upcomingStages = stageRepository
                .findByResultAndScheduledAtAfter(StageResult.PENDING, LocalDateTime.now())
                .stream()
                .sorted(Comparator.comparing(s -> s.getScheduledAt()))
                .limit(5)
                .toList();

        return new DashboardSummary(countByStatus, total, thisMonth, upcomingStages);
    }
}
