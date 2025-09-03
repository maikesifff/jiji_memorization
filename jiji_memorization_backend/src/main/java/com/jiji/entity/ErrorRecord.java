package com.jiji.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "error_record",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_error_record_user_unit_word", columnNames = {"user_id", "unit_word_id"})
       },
       indexes = {
           @Index(name = "idx_error_record_user_id", columnList = "user_id"),
           @Index(name = "idx_error_record_unit_word_id", columnList = "unit_word_id")
       })
public class ErrorRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // 逻辑外键

    @Column(name = "unit_word_id", nullable = false)
    private Long unitWordId; // 逻辑外键

    @Column(name = "error_count", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 0")
    private Integer errorCount = 0;

    @Column(name = "correct_count", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 0")
    private Integer correctCount = 0;

    @Column(name = "last_answer_correct")
    private Boolean lastAnswerCorrect;

    @Column(name = "last_error_at")
    private LocalDateTime lastErrorAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.errorCount == null) {
            this.errorCount = 0;
        }
        if (this.correctCount == null) {
            this.correctCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getUnitWordId() { return unitWordId; }
    public void setUnitWordId(Long unitWordId) { this.unitWordId = unitWordId; }

    public Integer getErrorCount() { return errorCount; }
    public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }

    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }

    public Boolean getLastAnswerCorrect() { return lastAnswerCorrect; }
    public void setLastAnswerCorrect(Boolean lastAnswerCorrect) { this.lastAnswerCorrect = lastAnswerCorrect; }

    public LocalDateTime getLastErrorAt() { return lastErrorAt; }
    public void setLastErrorAt(LocalDateTime lastErrorAt) { this.lastErrorAt = lastErrorAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
