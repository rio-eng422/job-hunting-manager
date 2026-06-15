package com.example.jobhunting.dto.request;

import com.example.jobhunting.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

/** PATCH /applications/{id}/status 用。ステータス1フィールドだけ更新する。 */
public record StatusUpdateRequest(
        @NotNull(message = "ステータスは必須です")
        ApplicationStatus status
) {}
