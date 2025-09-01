package com.jiji.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sentence", indexes = {
        @Index(name = "idx_sentence_word_id", columnList = "word_id")
})
public class Sentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word_id", nullable = false)
    private Long wordId; // 逻辑外键

    @Column(name = "sentence_text", nullable = false, length = 500)
    private String sentenceText;

    @Column(name = "translation", length = 500)
    private String translation;

    @Column(name = "pron", columnDefinition = "TEXT")
    private String pron; // 发音（编码后的语音文件）

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }

    public String getSentenceText() { return sentenceText; }
    public void setSentenceText(String sentenceText) { this.sentenceText = sentenceText; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public String getPron() { return pron; }
    public void setPron(String pron) { this.pron = pron; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
