package com.jiji.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "word")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word_text", nullable = false, length = 100)
    private String wordText;

    @Column(name = "phonetic", length = 100)
    private String phonetic; // 音标

    @Column(name = "pron_uk", columnDefinition = "TEXT")
    private String pronUk; // 英音发音（编码后的语音文件）

    @Column(name = "pron_us", columnDefinition = "TEXT")
    private String pronUs; // 美音发音（编码后的语音文件）

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

    public String getWordText() { return wordText; }
    public void setWordText(String wordText) { this.wordText = wordText; }

    public String getPhonetic() { return phonetic; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }

    public String getPronUk() { return pronUk; }
    public void setPronUk(String pronUk) { this.pronUk = pronUk; }

    public String getPronUs() { return pronUs; }
    public void setPronUs(String pronUs) { this.pronUs = pronUs; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
