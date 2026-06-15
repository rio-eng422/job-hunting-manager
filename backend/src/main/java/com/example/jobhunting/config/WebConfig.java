package com.example.jobhunting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 設定。React 開発サーバー（localhost:3000）からの API アクセスを許可する。
 *
 * 設計理由:
 * - @CrossOrigin を各コントローラーに付けるより、ここで一括設定する方が
 *   追加・変更・本番切り替えが1か所で完結する。
 * - 本番では allowedOrigins を実際のドメインに絞ること。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
