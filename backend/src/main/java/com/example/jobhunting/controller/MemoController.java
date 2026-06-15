package com.example.jobhunting.controller;

import com.example.jobhunting.dto.request.MemoRequest;
import com.example.jobhunting.dto.response.MemoResponse;
import com.example.jobhunting.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @GetMapping("/api/applications/{applicationId}/memos")
    public List<MemoResponse> getMemosByApplication(@PathVariable Long applicationId) {
        return memoService.getMemosByApplication(applicationId).stream()
                .map(MemoResponse::from)
                .toList();
    }

    @PostMapping("/api/applications/{applicationId}/memos")
    @ResponseStatus(HttpStatus.CREATED)
    public MemoResponse createMemo(
            @PathVariable Long applicationId,
            @Valid @RequestBody MemoRequest request) {
        return MemoResponse.from(
                memoService.createMemo(applicationId, request.toEntity()));
    }

    @PutMapping("/api/memos/{id}")
    public MemoResponse updateMemo(
            @PathVariable Long id,
            @Valid @RequestBody MemoRequest request) {
        return MemoResponse.from(
                memoService.updateMemo(id, request.toEntity()));
    }

    @DeleteMapping("/api/memos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMemo(@PathVariable Long id) {
        memoService.deleteMemo(id);
    }
}
