package com.example.jobhunting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 指定 ID のリソースが存在しない場合にスローする例外。
 *
 * 設計理由:
 * - @ResponseStatus(NOT_FOUND) を付けることで、この例外が Controller まで
 *   伝播したとき Spring が自動で HTTP 404 を返す。
 *   Phase 4 で GlobalExceptionHandler を追加すればより細かく制御できるが、
 *   まず最小コストで 404 が返るようにしておく。
 * - RuntimeException を継承: チェック例外にすると全 Service メソッドの
 *   シグネチャに throws が必要になり煩雑なため。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }
}
