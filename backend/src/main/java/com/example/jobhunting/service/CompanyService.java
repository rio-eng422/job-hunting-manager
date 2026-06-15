package com.example.jobhunting.service;

import com.example.jobhunting.entity.Company;
import com.example.jobhunting.exception.ResourceNotFoundException;
import com.example.jobhunting.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 企業マスタのビジネスロジック。
 *
 * 設計理由:
 * - クラス全体に @Transactional(readOnly = true) を付ける理由:
 *   読み取り専用トランザクションは Hibernate の flush を省略できるため高速。
 *   誤って読み取りメソッド内で save() しても DB 反映されないという安全弁にもなる。
 *   書き込みメソッドには個別に @Transactional で上書きする。
 * - @RequiredArgsConstructor: final フィールドを引数に取るコンストラクタを Lombok が生成。
 *   Spring がそのコンストラクタを使って DI する（@Autowired 不要）。
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<Company> getAllCompanies() {
        return companyRepository.findAllByOrderByNameAsc();
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }

    public List<Company> searchCompanies(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Company createCompany(Company company) {
        if (companyRepository.existsByName(company.getName())) {
            throw new IllegalArgumentException("同名の企業が既に登録されています: " + company.getName());
        }
        return companyRepository.save(company);
    }

    @Transactional
    public Company updateCompany(Long id, Company updated) {
        Company company = getCompanyById(id);
        company.setName(updated.getName());
        company.setIndustry(updated.getIndustry());
        company.setWebsite(updated.getWebsite());
        company.setNotes(updated.getNotes());
        // save() は不要だが明示的に書くことで意図を示す
        return companyRepository.save(company);
    }

    @Transactional
    public void deleteCompany(Long id) {
        Company company = getCompanyById(id); // 存在確認を兼ねる
        companyRepository.delete(company);
    }
}
