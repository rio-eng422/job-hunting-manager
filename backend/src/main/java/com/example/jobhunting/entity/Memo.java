package com.example.jobhunting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * メモ（応募に紐づく自由記述ノート）。
 *
 * 設計理由:
 * - JobApplication に直接メモ文字列を持たせると1応募1メモに限定される。
 *   時系列で複数のメモを残せるよう独立エンティティにした。
 * - SelectionStage の notes と使い分け:
 *   notes は面接固有の記録（質問内容・手応えなど）、
 *   Memo は応募全体に対する自由なメモ（OB訪問の感想、志望動機の下書きなど）。
 * - createdAt（BaseEntity）で時系列ソートが可能。
 */
@Entity
@Table(name = "memos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Memo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
