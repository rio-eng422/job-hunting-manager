package com.example.jobhunting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 全エンティティ共通の監査フィールド。
 * @MappedSuperclass: 独立したテーブルを持たず、サブクラスのテーブルにカラムが追加される。
 * Setter を持たないのは、これらの値は Hibernate が自動管理するため。
 */
@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
