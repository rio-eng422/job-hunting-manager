package com.example.jobhunting.controller;

import com.example.jobhunting.dto.request.ReminderRequest;
import com.example.jobhunting.dto.request.SelectionStageRequest;
import com.example.jobhunting.dto.request.StageResultRequest;
import com.example.jobhunting.dto.response.ReminderResponse;
import com.example.jobhunting.dto.response.SelectionStageResponse;
import com.example.jobhunting.service.ReminderService;
import com.example.jobhunting.service.SelectionStageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 選考ステージ + ネストしたリマインダーのエンドポイント。
 *
 * 設計理由:
 * - クラスレベルの @RequestMapping を使わず、メソッドごとにフルパスを書いた理由:
 *   ステージは /api/applications/{id}/stages（親リソース経由）と
 *   /api/stages/{id}（単体操作）の2種類のパスを持つ。
 *   クラスに1つしかプレフィックスを付けられないため、メソッドにフルパスを書く方がシンプル。
 * - GET /api/stages/upcoming を {id} より前に定義:
 *   Spring MVC はリテラル > パターン の優先度で解決するため順序不問だが、
 *   コードを読む人への配慮として先に書く。
 */
@RestController
@RequiredArgsConstructor
public class SelectionStageController {

    private final SelectionStageService stageService;
    private final ReminderService reminderService;

    // ── 応募に紐づくステージ ──────────────────────────────────────────

    @GetMapping("/api/applications/{applicationId}/stages")
    public List<SelectionStageResponse> getStagesByApplication(
            @PathVariable Long applicationId) {
        return stageService.getStagesByApplication(applicationId).stream()
                .map(SelectionStageResponse::from)
                .toList();
    }

    @PostMapping("/api/applications/{applicationId}/stages")
    @ResponseStatus(HttpStatus.CREATED)
    public SelectionStageResponse createStage(
            @PathVariable Long applicationId,
            @Valid @RequestBody SelectionStageRequest request) {
        return SelectionStageResponse.from(
                stageService.createStage(applicationId, request.toEntity()));
    }

    // ── ステージ単体操作 ──────────────────────────────────────────────

    @GetMapping("/api/stages/upcoming")
    public List<SelectionStageResponse> getUpcomingStages() {
        return stageService.getUpcomingStages().stream()
                .map(SelectionStageResponse::from)
                .toList();
    }

    @GetMapping("/api/stages/{id}")
    public SelectionStageResponse getStageById(@PathVariable Long id) {
        return SelectionStageResponse.from(stageService.getStageById(id));
    }

    @PutMapping("/api/stages/{id}")
    public SelectionStageResponse updateStage(
            @PathVariable Long id,
            @Valid @RequestBody SelectionStageRequest request) {
        return SelectionStageResponse.from(
                stageService.updateStage(id, request.toEntity()));
    }

    @PatchMapping("/api/stages/{id}/result")
    public SelectionStageResponse updateResult(
            @PathVariable Long id,
            @Valid @RequestBody StageResultRequest request) {
        return SelectionStageResponse.from(
                stageService.updateResult(id, request.result()));
    }

    @DeleteMapping("/api/stages/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStage(@PathVariable Long id) {
        stageService.deleteStage(id);
    }

    // ── ステージに紐づくリマインダー ──────────────────────────────────

    @GetMapping("/api/stages/{stageId}/reminders")
    public List<ReminderResponse> getRemindersByStage(@PathVariable Long stageId) {
        return reminderService.getRemindersByStage(stageId).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @PostMapping("/api/stages/{stageId}/reminders")
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderResponse createReminder(
            @PathVariable Long stageId,
            @Valid @RequestBody ReminderRequest request) {
        return ReminderResponse.from(
                reminderService.createReminder(stageId, request.toEntity()));
    }
}
