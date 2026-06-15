package com.example.jobhunting.entity;

/**
 * 各選考ステージの結果。
 * SelectionStage が確定するまでは PENDING のまま保持し、
 * 結果判明後に PASSED / FAILED / CANCELLED に更新する。
 */
public enum StageResult {
    PENDING,   // 結果待ち
    PASSED,    // 通過
    FAILED,    // 不合格
    CANCELLED  // 中止・辞退
}
