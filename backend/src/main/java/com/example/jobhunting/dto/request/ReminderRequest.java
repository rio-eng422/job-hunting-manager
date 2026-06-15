package com.example.jobhunting.dto.request;

import com.example.jobhunting.entity.Reminder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ReminderRequest(
        @NotNull(message = "リマインド日時は必須です")
        LocalDateTime remindAt,

        @Size(max = 200, message = "メッセージは200文字以内で入力してください")
        String message
) {
    public Reminder toEntity() {
        return Reminder.builder()
                .remindAt(remindAt)
                .message(message)
                .build();
    }
}
