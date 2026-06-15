package com.example.jobhunting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 応募エンティティ（就活の中心モデル）。
 *
 * 設計理由:
 * - クラス名を Application ではなく JobApplication にしたのは、
 *   Spring の ApplicationContext と名前が衝突するのを避けるため。
 * - status は「現在どのステージにいるか」を示す概要フィールド。
 *   実際の各ステージ詳細は SelectionStage で管理し、冗長に見えるが
 *   ダッシュボード集計やリスト表示の効率化のために保持する。
 * - appliedDate を LocalDate（日付のみ）にしたのは、応募は日単位で管理すれば十分なため。
 */
@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Size(max = 100)
    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    @OneToMany(mappedBy = "jobApplication", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SelectionStage> selectionStages = new ArrayList<>();

    @OneToMany(mappedBy = "jobApplication", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Memo> memos = new ArrayList<>();
}
