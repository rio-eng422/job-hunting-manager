package com.example.jobhunting.exception;

import com.example.jobhunting.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全コントローラーで発生した例外を一元ハンドリングする。
 *
 * 設計理由:
 * - @RestControllerAdvice: @ControllerAdvice + @ResponseBody の合成。
 *   例外をキャッチして JSON レスポンスを返すのに適している。
 * - 個別コントローラーに try-catch を書かない:
 *   例外処理が Controller に散らばると、エラーフォーマットが一貫しなくなる。
 *   ここに集約することで「どこで何を返すか」が一か所で把握できる。
 * - 汎用 Exception ハンドラは最後のセーフティネット:
 *   想定外の例外をユーザーにスタックトレースごと返さず、
 *   ログに詳細を残しながら 500 を返す。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, ex.getMessage()));
    }

    // @Valid による Bean Validation 失敗
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "サーバーエラーが発生しました"));
    }
}
