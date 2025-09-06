package com.jiji.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "error_record", 
       uniqueConstraints = @UniqueConstraint(name = "uk_error_record_user_unit_word", 
                                           columnNames = {"user_id", "unit_word_id"}))
public class ErrorRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "unit_word_id", nullable = false)
    private Long unitWordId;
    
    @Column(name = "correct_count", nullable = false, columnDefinition = "int default 0")
    private Integer correctCount = 0;
    
    @Column(name = "error_count", nullable = false, columnDefinition = "int default 0")
    private Integer errorCount = 0;
    
    @Column(name = "last_answer_correct")
    private Boolean lastAnswerCorrect;
    
    @Column(name = "last_error_at")
    private LocalDateTime lastErrorAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public ErrorRecord() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public ErrorRecord(Long userId, Long unitWordId) {
        this();
        this.userId = userId;
        this.unitWordId = unitWordId;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getUnitWordId() {
        return unitWordId;
    }
    
    public void setUnitWordId(Long unitWordId) {
        this.unitWordId = unitWordId;
    }
    
    public Integer getCorrectCount() {
        return correctCount;
    }
    
    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }
    
    public Integer getErrorCount() {
        return errorCount;
    }
    
    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }
    
    public Boolean getLastAnswerCorrect() {
        return lastAnswerCorrect;
    }
    
    public void setLastAnswerCorrect(Boolean lastAnswerCorrect) {
        this.lastAnswerCorrect = lastAnswerCorrect;
    }
    
    public LocalDateTime getLastErrorAt() {
        return lastErrorAt;
    }
    
    public void setLastErrorAt(LocalDateTime lastErrorAt) {
        this.lastErrorAt = lastErrorAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // 业务方法
    public void recordCorrectAnswer() {
        this.correctCount++;
        this.lastAnswerCorrect = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void recordIncorrectAnswer() {
        this.errorCount++;
        this.lastAnswerCorrect = false;
        this.lastErrorAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isCorrect() {
        return this.errorCount == 0;
    }
    
    public int getTotalAnswers() {
        return this.correctCount + this.errorCount;
    }
}