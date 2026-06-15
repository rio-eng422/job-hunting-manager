package com.example.jobhunting.dto;

import java.time.LocalDateTime;

/**
 * エラー時に全エンドポイントで返す統一レスポンス形式。
 * timestamp を含めることでクライアントのログ調査が楽になる。
 */
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {
    public ErrorResponse(int status, String message) {
        this(status, message, LocalDateTime.now());
    }
}
