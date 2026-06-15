package com.example.jobhunting.dto.request;

import com.example.jobhunting.entity.SelectionStage;
import com.example.jobhunting.entity.StageResult;
import com.example.jobhunting.entity.StageType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SelectionStageRequest(
        @NotNull(message = "選考種別は必須です")
        StageType stageType,

        Integer stageNumber,

        LocalDateTime scheduledAt,

        @Size(max = 200, message = "場所は200文字以内で入力してください")
        String location,

        StageResult result,     // null → PENDING をデフォルト適用

        String notes
) {
    public SelectionStage toEntity() {
        return SelectionStage.builder()
                .stageType(stageType)
                .stageNumber(stageNumber)
                .scheduledAt(scheduledAt)
                .location(location)
                .result(result != null ? result : StageResult.PENDING)
                .notes(notes)
                .build();
    }
}
