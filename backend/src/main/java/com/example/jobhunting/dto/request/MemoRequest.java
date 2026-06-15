package com.example.jobhunting.dto.request;

import com.example.jobhunting.entity.Memo;
import jakarta.validation.constraints.NotBlank;

public record MemoRequest(
        @NotBlank(message = "メモ内容は必須です")
        String content
) {
    public Memo toEntity() {
        return Memo.builder()
                .content(content)
                .build();
    }
}
