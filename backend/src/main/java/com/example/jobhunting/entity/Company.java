package com.example.jobhunting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 企業マスタ。
 *
 * 設計理由:
 * - 企業情報を JobApplication から分離することで、同じ企業への複数応募でも
 *   企業名・業種などを重複管理せずに済む（正規化）。
 * - applications は LAZY ロード: 企業一覧を取得するだけなら選考情報は不要なので
 *   N+1 を避けるため遅延ロードをデフォルトにする。
 * - CascadeType.ALL + orphanRemoval: 企業を削除したら紐づく応募も連鎖削除する。
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 50)
    @Column(length = 50)
    private String industry;

    @Size(max = 200)
    @Column(length = 200)
    private String website;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JobApplication> applications = new ArrayList<>();
}
