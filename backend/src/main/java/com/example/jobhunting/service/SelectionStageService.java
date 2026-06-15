package com.example.jobhunting.service;

import com.example.jobhunting.entity.JobApplication;
import com.example.jobhunting.entity.SelectionStage;
import com.example.jobhunting.entity.StageResult;
import com.example.jobhunting.exception.ResourceNotFoundException;
import com.example.jobhunting.repository.JobApplicationRepository;
import com.example.jobhunting.repository.SelectionStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 選考ステージのビジネスロジック。
 *
 * 設計理由:
 * - updateResult を updateStage とは別メソッドにした理由:
 *   面接が終わった後に「結果だけ入力する」操作は頻繁。専用にすることで
 *   API も PATCH /stages/{id}/result と表現でき、意図が明確になる。
 * - getUpcomingStages でサービス層がソートを担う理由:
 *   リポジトリは「今後のPENDINGステージ全件」を返し、上位 N 件への絞り込みや
 *   ソートはビジネス要件（件数や基準が変わりやすい）なのでサービス層に置く。
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SelectionStageService {

    private final SelectionStageRepository stageRepository;
    private final JobApplicationRepository applicationRepository;

    public List<SelectionStage> getStagesByApplication(Long applicationId) {
        return stageRepository.findByJobApplicationIdOrderByScheduledAtAsc(applicationId);
    }

    public SelectionStage getStageById(Long id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SelectionStage", id));
    }

    // 今後の未定ステージ（ホーム画面の「直近の選考」ウィジェット用）
    public List<SelectionStage> getUpcomingStages() {
        return stageRepository
                .findByResultAndScheduledAtAfter(StageResult.PENDING, LocalDateTime.now())
                .stream()
                .sorted(Comparator.comparing(SelectionStage::getScheduledAt))
                .toList();
    }

    // 期間指定取得（カレンダービュー用）
    public List<SelectionStage> getStagesByDateRange(LocalDateTime start, LocalDateTime end) {
        return stageRepository.findByScheduledAtBetween(start, end);
    }

    @Transactional
    public SelectionStage createStage(Long applicationId, SelectionStage stage) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", applicationId));
        stage.setJobApplication(application);
        return stageRepository.save(stage);
    }

    @Transactional
    public SelectionStage updateStage(Long id, SelectionStage updated) {
        SelectionStage stage = getStageById(id);
        stage.setStageType(updated.getStageType());
        stage.setStageNumber(updated.getStageNumber());
        stage.setScheduledAt(updated.getScheduledAt());
        stage.setLocation(updated.getLocation());
        stage.setResult(updated.getResult());
        stage.setNotes(updated.getNotes());
        return stageRepository.save(stage);
    }

    // 結果のみ更新（面接後の合否入力用）
    @Transactional
    public SelectionStage updateResult(Long id, StageResult result) {
        SelectionStage stage = getStageById(id);
        stage.setResult(result);
        return stageRepository.save(stage);
    }

    @Transactional
    public void deleteStage(Long id) {
        SelectionStage stage = getStageById(id);
        stageRepository.delete(stage);
    }
}
