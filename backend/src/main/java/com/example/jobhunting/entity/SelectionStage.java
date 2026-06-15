package com.example.jobhunting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 選考ステージ（1次面接・2次面接・書類選考など個々のイベント）。
 *
 * 設計理由:
 * - JobApplication から分離した理由: 1つの応募に対して選考が複数回発生し、
 *   それぞれに日時・場所・結果・メモが必要なため。JobApplication に直接持つと
 *   カラムが爆発する（interview1Date, interview1Result, interview2Date...）。
 * - stageNumber: 同一 stageType が繰り返される場合（2回の1次面接など）の識別用。
 * - scheduledAt を LocalDateTime にしたのは、面接は時刻まで管理が必要なため
 *   （Reminder の remindAt と整合させる）。
 * - result のデフォルト PENDING: ステージを登録した時点では結果は未定のため。
 */
@Entity
@Table(name = "selection_stages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectionStage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage_type", nullable = false, length = 30)
    private StageType stageType;

    @Column(name = "stage_number")
    private Integer stageNumber;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Size(max = 200)
    @Column(length = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StageResult result = StageResult.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "selectionStage", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();
}
