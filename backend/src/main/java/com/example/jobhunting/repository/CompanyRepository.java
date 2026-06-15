package com.example.jobhunting.repository;

import com.example.jobhunting.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 企業マスタのリポジトリ。
 *
 * 設計理由:
 * - JpaRepository を継承することで findById / save / delete など
 *   基本 CRUD と Page/Sort 対応の findAll が自動生成される。
 * - メソッド名規約（Derived Query）を使うことで @Query を書かずに
 *   SQL を Spring Data に生成させる。名前が仕様書になるため可読性が高い。
 * - existsByName: 登録前の重複チェック用。count クエリより軽量（EXISTS に変換される）。
 */
public interface CompanyRepository extends JpaRepository<Company, Long> {

    // 企業名の部分一致検索（大文字小文字無視）
    List<Company> findByNameContainingIgnoreCase(String name);

    // 同名企業の重複チェック
    boolean existsByName(String name);

    // 名前の昇順で全件取得（企業選択ドロップダウン用）
    List<Company> findAllByOrderByNameAsc();
}
