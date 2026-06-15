package com.example.jobhunting.dto.request;

import com.example.jobhunting.entity.Company;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequest(
        @NotBlank(message = "企業名は必須です")
        @Size(max = 100, message = "企業名は100文字以内で入力してください")
        String name,

        @Size(max = 50, message = "業種は50文字以内で入力してください")
        String industry,

        @Size(max = 200, message = "WebサイトURLは200文字以内で入力してください")
        String website,

        String notes
) {
    public Company toEntity() {
        return Company.builder()
                .name(name)
                .industry(industry)
                .website(website)
                .notes(notes)
                .build();
    }
}
