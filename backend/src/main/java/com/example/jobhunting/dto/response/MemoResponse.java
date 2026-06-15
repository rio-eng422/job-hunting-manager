package com.example.jobhunting.dto.response;

import com.example.jobhunting.entity.Memo;

import java.time.LocalDateTime;

public record MemoResponse(
        Long id,
        Long jobApplicationId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MemoResponse from(Memo memo) {
        return new MemoResponse(
                memo.getId(),
                memo.getJobApplication().getId(),
                memo.getContent(),
                memo.getCreatedAt(),
                memo.getUpdatedAt()
        );
    }
}
