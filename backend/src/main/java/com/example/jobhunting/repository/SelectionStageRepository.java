package com.example.jobhunting.repository;

import com.example.jobhunting.entity.SelectionStage;
import com.example.jobhunting.entity.StageResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 選考ステージのリポジトリ。
 *
 * 設計理由:
 * - findByJobApplicationIdOrderByScheduledAtAsc:
 *   面接一覧は「日付順」が自然な表示順なので ORDER BY をメソッド名に埋め込む。
 *   サービス側でソートロジックを書かなくて済む。
 * - findByScheduledAtBetween:
 *   カレンダービューや週間スケジュール表示のために期間で取得できる口を用意する。
 *   start/end は呼び出し元（サービス層）が「今週の月曜〜日曜」などを計算して渡す。
 * - findByResultAndScheduledAtAfter:
 *   「結果待ち（PENDING）かつ未来の日程」= これからある選考の一覧。
 *   リマインダー生成バッチや「直近の選考」ウィジェットに使う。
 */
public interface SelectionStageRepository extends JpaRepository<SelectionStage, Long> {

    // 応募に紐づくステージ一覧（登録順）
    List<SelectionStage> findByJobApplicationId(Long jobApplicationId);

    // 応募に紐づくステージ一覧（予定日昇順）
    List<SelectionStage> findByJobApplicationIdOrderByScheduledAtAsc(Long jobApplicationId);

    // 期間内の選考（カレンダー表示・週間スケジュール用）
    List<SelectionStage> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);

    // 特定の結果かつ指定日時以降のステージ（直近の未定選考一覧）
    List<SelectionStage> findByResultAndScheduledAtAfter(StageResult result, LocalDateTime after);
}
