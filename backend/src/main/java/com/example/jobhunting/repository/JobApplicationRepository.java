package com.example.jobhunting.repository;

import com.example.jobhunting.entity.ApplicationStatus;
import com.example.jobhunting.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 応募エンティティのリポジトリ。就活管理の中核となるクエリ群。
 *
 * 設計理由:
 * - findByStatus に Pageable オーバーロードを持たせた理由:
 *   応募数が増えたときにリスト画面をページング対応できるよう最初から口だけ開けておく。
 *   サービス層はどちらのオーバーロードを呼ぶか選択できる。
 * - countGroupByStatus を @Query にした理由:
 *   Derived Query では GROUP BY + プロジェクション戻り値を表現できないため。
 *   JPQL で書くことで DB 方言に依存せず PostgreSQL/H2 双方で動く（テスト時に H2 を使える）。
 * - countByAppliedDateBetween: 「今月の応募数」をダッシュボードに表示するための集計。
 *   サービス層でその月の start/end を計算して渡す。
 */
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // 企業別の応募一覧
    List<JobApplication> findByCompanyId(Long companyId);

    // ステータスで絞り込み（全件）
    List<JobApplication> findByStatus(ApplicationStatus status);

    // ステータスで絞り込み（ページング対応）
    Page<JobApplication> findByStatus(ApplicationStatus status, Pageable pageable);

    // ダッシュボード: ステータス別件数集計
    @Query("SELECT a.status AS status, COUNT(a) AS count FROM JobApplication a GROUP BY a.status")
    List<ApplicationStatusCount> countGroupByStatus();

    // ダッシュボード: 期間内の応募件数（今月・先月など）
    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.appliedDate BETWEEN :start AND :end")
    long countByAppliedDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
