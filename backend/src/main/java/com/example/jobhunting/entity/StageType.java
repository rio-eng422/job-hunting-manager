package com.example.jobhunting.entity;

/**
 * 個々の選考ステージの種別。
 * ApplicationStatus とは独立して管理することで、
 * 同一応募内に複数回の面接を記録できる。
 */
public enum StageType {
    DOCUMENT_SCREENING,  // 書類選考
    APTITUDE_TEST,       // 適性検査
    GROUP_DISCUSSION,    // グループディスカッション
    FIRST_INTERVIEW,     // 1次面接
    SECOND_INTERVIEW,    // 2次面接
    THIRD_INTERVIEW,     // 3次面接
    FINAL_INTERVIEW,     // 最終面接
    OFFER                // 内定通知
}
