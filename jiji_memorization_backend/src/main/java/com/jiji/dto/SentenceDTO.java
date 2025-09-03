package com.jiji.dto;

import java.time.LocalDateTime;

public class SentenceDTO {
    private Long id;
    private Long wordId;
    private String wordText;      // 单词文本

    private String sentenceText;  // 例句内容
    private String translation;   // 翻译
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造函数
    public SentenceDTO() {}

    public SentenceDTO(Long id, Long wordId, String wordText, String sentenceText, String translation, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.wordId = wordId;
        this.wordText = wordText;
        this.sentenceText = sentenceText;
        this.translation = translation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }

    public String getWordText() { return wordText; }
    public void setWordText(String wordText) { this.wordText = wordText; }



    public String getSentenceText() { return sentenceText; }
    public void setSentenceText(String sentenceText) { this.sentenceText = sentenceText; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
