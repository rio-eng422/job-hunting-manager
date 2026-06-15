package com.example.jobhunting.service;

import com.example.jobhunting.entity.Reminder;
import com.example.jobhunting.entity.SelectionStage;
import com.example.jobhunting.exception.ResourceNotFoundException;
import com.example.jobhunting.repository.ReminderRepository;
import com.example.jobhunting.repository.SelectionStageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * リマインダーのビジネスロジック。送信バッチ処理を含む。
 *
 * 設計理由:
 * - processPendingReminders に @Scheduled を付けた理由:
 *   リマインドは「特定時刻が来たら自動送信」するもの。ユーザーが手動でトリガーするのではなく、
 *   アプリが定期的にチェックして送るため、バッチ処理をサービス層に同居させる。
 * - fixedDelay = 60_000（1分）にした理由:
 *   fixedRate だと処理に1分以上かかると多重起動する。fixedDelay は前の処理が終わってから
 *   次のカウントが始まるため、バッチ処理の多重起動を防ぐのに適している。
 * - sent フラグを更新して保存する理由:
 *   @Scheduled はトランザクション外から呼ばれるため @Transactional を付与。
 *   管理エンティティの dirty checking で個別 save() を呼ばなくてもよいが、
 *   意図を明確にするため明示的に呼ぶ。
 * - TODO: 実際の通知（メール・LINE・Slack）は Phase 5 以降で実装。
 *   現時点ではログ出力のみ行い、sent=true に更新することで二重送信を防ぐ。
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final SelectionStageRepository stageRepository;

    public List<Reminder> getRemindersByStage(Long stageId) {
        return reminderRepository.findBySelectionStageId(stageId);
    }

    public List<Reminder> getRemindersByApplication(Long applicationId) {
        return reminderRepository.findByApplicationId(applicationId);
    }

    @Transactional
    public Reminder createReminder(Long stageId, Reminder reminder) {
        SelectionStage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new ResourceNotFoundException("SelectionStage", stageId));
        reminder.setSelectionStage(stage);
        return reminderRepository.save(reminder);
    }

    @Transactional
    public Reminder updateReminder(Long id, Reminder updated) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", id));
        reminder.setRemindAt(updated.getRemindAt());
        reminder.setMessage(updated.getMessage());
        return reminderRepository.save(reminder);
    }

    @Transactional
    public void deleteReminder(Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", id));
        reminderRepository.delete(reminder);
    }

    // ========== バッチ処理 ==========

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void processPendingReminders() {
        List<Reminder> due = reminderRepository
                .findBySentFalseAndRemindAtLessThanEqual(LocalDateTime.now());

        if (due.isEmpty()) return;

        log.info("リマインド送信バッチ: {} 件を処理します", due.size());
        for (Reminder reminder : due) {
            // TODO: ここでメール / プッシュ通知 API を呼び出す
            log.info("[REMINDER] id={} stageId={} message={}",
                    reminder.getId(),
                    reminder.getSelectionStage().getId(),
                    reminder.getMessage());
            reminder.setSent(true);
            // @Transactional 管理下のため dirty checking でまとめて flush される
        }
    }
}
