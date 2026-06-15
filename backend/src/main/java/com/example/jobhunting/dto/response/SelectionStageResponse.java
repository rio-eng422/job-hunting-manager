package com.example.jobhunting.dto.response;

import com.example.jobhunting.entity.SelectionStage;
import com.example.jobhunting.entity.StageResult;
import com.example.jobhunting.entity.StageType;

import java.time.LocalDateTime;

/**
 * 注意: from() 内で company.getName() を呼ぶため、OSIV（Open Session In View）が
 * 有効（Spring Boot デフォルト: true）である必要がある。
 * 本番で OSIV を false にする場合は、SelectionStageRepository に
 * JOIN FETCH クエリを追加すること。
 */
public record SelectionStageResponse(
        Long id,
        Long jobApplicationId,
        String companyName,
        String jobTitle,
        StageType stageType,
        Integer stageNumber,
        LocalDateTime scheduledAt,
        String location,
        StageResult result,
        String notes,
        LocalDateTime createdAt
) {
    public static SelectionStageResponse from(SelectionStage stage) {
        return new SelectionStageResponse(
                stage.getId(),
                stage.getJobApplication().getId(),
                stage.getJobApplication().getCompany().getName(),
                stage.getJobApplication().getJobTitle(),
                stage.getStageType(),
                stage.getStageNumber(),
                stage.getScheduledAt(),
                stage.getLocation(),
                stage.getResult(),
                stage.getNotes(),
                stage.getCreatedAt()
        );
    }
}
