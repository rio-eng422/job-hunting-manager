package com.example.jobhunting.controller;

import com.example.jobhunting.dto.request.ReminderRequest;
import com.example.jobhunting.dto.response.ReminderResponse;
import com.example.jobhunting.service.ReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** リマインダーの更新・削除（作成・一覧は SelectionStageController で処理）。 */
@RestController
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PutMapping("/api/reminders/{id}")
    public ReminderResponse updateReminder(
            @PathVariable Long id,
            @Valid @RequestBody ReminderRequest request) {
        return ReminderResponse.from(
                reminderService.updateReminder(id, request.toEntity()));
    }

    @DeleteMapping("/api/reminders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
    }
}
