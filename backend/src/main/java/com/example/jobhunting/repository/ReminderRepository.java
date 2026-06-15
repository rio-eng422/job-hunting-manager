package com.example.jobhunting.repository;

import com.example.jobhunting.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * リマインダーのリポジトリ。
 *
 * 設計理由:
 * - findBySentFalseAndRemindAtLessThanEqual:
 *   「未送信（sent=false）かつ送信時刻が現在以前」= 今すぐ送るべきリマインダー。
 *   スケジューラー（@Scheduled バッチ）がこのメソッドを定期的に呼び出し、
 *   該当件数分だけ通知を送って sent=true に更新する。
 *   LessThanEqual（<=）にした理由: Before（<）だと同一秒に登録したリマインドが漏れる。
 * - findByApplicationId の @Query:
 *   Reminder → SelectionStage → JobApplication という2段階の結合を
 *   Derived Query では表現できないため JPQL で明示する。
 *   応募詳細画面でその応募に関わる全リマインダーを一覧表示するのに使う。
 */
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    // 選考ステージに紐づくリマインダー一覧
    List<Reminder> findBySelectionStageId(Long selectionStageId);

    // バッチ送信対象: 未送信かつ送信時刻が現在以前
    List<Reminder> findBySentFalseAndRemindAtLessThanEqual(LocalDateTime now);

    // 応募に紐づく全リマインダー（2段階結合）
    @Query("SELECT r FROM Reminder r WHERE r.selectionStage.jobApplication.id = :applicationId ORDER BY r.remindAt ASC")
    List<Reminder> findByApplicationId(@Param("applicationId") Long applicationId);
}
