package com.example.jobhunting.controller;

import com.example.jobhunting.dto.request.CompanyRequest;
import com.example.jobhunting.dto.response.CompanyResponse;
import com.example.jobhunting.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/api/companies")
    public List<CompanyResponse> getAllCompanies() {
        return companyService.getAllCompanies().stream()
                .map(CompanyResponse::from)
                .toList();
    }

    // /search は {id} より先に定義: Spring MVC はリテラルを優先するので順序不問だが明示する
    @GetMapping("/api/companies/search")
    public List<CompanyResponse> searchCompanies(@RequestParam String name) {
        return companyService.searchCompanies(name).stream()
                .map(CompanyResponse::from)
                .toList();
    }

    @GetMapping("/api/companies/{id}")
    public CompanyResponse getCompanyById(@PathVariable Long id) {
        return CompanyResponse.from(companyService.getCompanyById(id));
    }

    @PostMapping("/api/companies")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse createCompany(@Valid @RequestBody CompanyRequest request) {
        return CompanyResponse.from(companyService.createCompany(request.toEntity()));
    }

    @PutMapping("/api/companies/{id}")
    public CompanyResponse updateCompany(@PathVariable Long id,
                                          @Valid @RequestBody CompanyRequest request) {
        return CompanyResponse.from(companyService.updateCompany(id, request.toEntity()));
    }

    @DeleteMapping("/api/companies/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
    }
}
