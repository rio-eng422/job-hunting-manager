package com.example.jobhunting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * リマインダー（選考日の事前通知）。
 *
 * 設計理由:
 * - JobApplication ではなく SelectionStage に紐づけた理由:
 *   リマインドは「面接が〇日後」のように具体的な予定日に対して設定するもの。
 *   応募全体ではなくステージの scheduledAt を起点として計算できるようにするため。
 * - sent フラグ: バッチ処理でリマインドを送信済みか判定するために使う。
 *   送信後に true にすることで二重送信を防ぐ。
 * - 1つのステージに複数のリマインドを設定可能（例: 1週間前と前日）。
 */
@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "selection_stage_id", nullable = false)
    private SelectionStage selectionStage;

    @NotNull
    @Column(name = "remind_at", nullable = false)
    private LocalDateTime remindAt;

    @Size(max = 200)
    @Column(length = 200)
    private String message;

    @Column(nullable = false)
    @Builder.Default
    private Boolean sent = false;
}
