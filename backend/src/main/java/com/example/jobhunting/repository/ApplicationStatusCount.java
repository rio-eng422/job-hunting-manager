package com.example.jobhunting.repository;

import com.example.jobhunting.entity.ApplicationStatus;

/**
 * ダッシュボード集計用プロジェクション。
 *
 * 設計理由:
 * - @Query の GROUP BY 結果を Object[] で返すと呼び出し側でキャストが必要になり壊れやすい。
 *   インターフェース型プロジェクションにすることで Spring Data が自動でマッピングし、
 *   型安全なまま集計値を取り出せる。
 * - getXxx() のメソッド名が @Query の SELECT 句の AS エイリアスと対応する。
 */
public interface ApplicationStatusCount {
    ApplicationStatus getStatus();
    Long getCount();
}
