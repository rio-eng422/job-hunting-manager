package com.example.jobhunting.repository;

import com.example.jobhunting.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * メモのリポジトリ。
 *
 * 設計理由:
 * - OrderByCreatedAtDesc: メモは新しいものを先頭に表示するのが自然（SNS のタイムライン的UI）。
 *   ソート方向をメソッド名に含めることでサービス層に Collections.sort を書かなくて済む。
 * - メソッドが1つだけなのは意図的: Memo は「応募に属する自由記述」であり、
 *   ステータスや日付での絞り込みは不要なためシンプルに保つ。
 */
public interface MemoRepository extends JpaRepository<Memo, Long> {

    // 応募に紐づくメモ一覧（新しい順）
    List<Memo> findByJobApplicationIdOrderByCreatedAtDesc(Long jobApplicationId);
}
