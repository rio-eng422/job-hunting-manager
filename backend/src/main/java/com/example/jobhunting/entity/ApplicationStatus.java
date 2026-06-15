package com.example.jobhunting.entity;

/**
 * 応募全体のステータス。
 * DB には文字列で保存（@Enumerated(EnumType.STRING)）するため、
 * 順序を変えても既存データが壊れない。
 */
public enum ApplicationStatus {
    INTERESTED,          // 気になる（応募前）
    APPLIED,             // 応募済み
    DOCUMENT_SCREENING,  // 書類選考中
    FIRST_INTERVIEW,     // 1次面接
    SECOND_INTERVIEW,    // 2次面接
    THIRD_INTERVIEW,     // 3次面接
    FINAL_INTERVIEW,     // 最終面接
    OFFER_RECEIVED,      // 内定
    ACCEPTED,            // 承諾
    REJECTED,            // 不合格
    WITHDRAWN            // 辞退
}
