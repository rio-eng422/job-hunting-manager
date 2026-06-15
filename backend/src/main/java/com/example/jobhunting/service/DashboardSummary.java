package com.example.jobhunting.service;

import com.example.jobhunting.entity.ApplicationStatus;
import com.example.jobhunting.entity.SelectionStage;

import java.util.List;
import java.util.Map;

/**
 * ダッシュボード集計結果をまとめる値オブジェクト。
 *
 * 設計理由:
 * - Java record を使う: フィールド・コンストラクタ・equals・hashCode・toString が
 *   自動生成され、不変オブジェクトとして扱える。集計結果は書き換えないため record が適切。
 * - Service 層に置いた理由: Controller が必要とする集計ロジックを持つのはサービス層の責務。
 *   Phase 4 で API 用 DTO に詰め替えるが、その変換もサービスの外（Controller or Mapper）で行う。
 */
public record DashboardSummary(
        Map<ApplicationStatus, Long> countByStatus,
        long totalApplications,
        long thisMonthApplications,
        List<SelectionStage> upcomingStages
) {}
