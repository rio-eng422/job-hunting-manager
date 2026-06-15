package com.example.jobhunting.dto.response;

import com.example.jobhunting.entity.Reminder;

import java.time.LocalDateTime;

public record ReminderResponse(
        Long id,
        Long selectionStageId,
        LocalDateTime remindAt,
        String message,
        boolean sent,
        LocalDateTime createdAt
) {
    public static ReminderResponse from(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getSelectionStage().getId(),
                reminder.getRemindAt(),
                reminder.getMessage(),
                reminder.getSent(),
                reminder.getCreatedAt()
        );
    }
}
