package com.example.jobhunting.dto.request;

import com.example.jobhunting.entity.StageResult;
import jakarta.validation.constraints.NotNull;

/** PATCH /stages/{id}/result 用。面接後の合否入力専用。 */
public record StageResultRequest(
        @NotNull(message = "選考結果は必須です")
        StageResult result
) {}
