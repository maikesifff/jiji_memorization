package com.jiji.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "unit_word",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_unit_word_unit_id_word_id", columnNames = {"unit_id", "word_id"})
       },
       indexes = {
           @Index(name = "idx_unit_word_unit_id", columnList = "unit_id"),
           @Index(name = "idx_unit_word_word_id", columnList = "word_id")
       })
public class UnitWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_id", nullable = false)
    private Long unitId; // 逻辑外键

    @Column(name = "word_id", nullable = false)
    private Long wordId; // 逻辑外键

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
