package com.example.jobhunting.dto.response;

import com.example.jobhunting.entity.Company;

import java.time.LocalDateTime;

public record CompanyResponse(
        Long id,
        String name,
        String industry,
        String website,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CompanyResponse from(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getIndustry(),
                company.getWebsite(),
                company.getNotes(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}
