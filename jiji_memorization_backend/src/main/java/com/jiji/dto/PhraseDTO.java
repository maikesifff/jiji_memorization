package com.jiji.dto;

import java.time.LocalDateTime;

public class PhraseDTO {
    private Long id;
    private Long wordId;
    private String wordText;      // 单词文本

    private String phraseText;    // 短语内容
    private String translation;   // 翻译
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造函数
    public PhraseDTO() {}

    public PhraseDTO(Long id, Long wordId, String wordText, String phraseText, String translation, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.wordId = wordId;
        this.wordText = wordText;
        this.phraseText = phraseText;
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



    public String getPhraseText() { return phraseText; }
    public void setPhraseText(String phraseText) { this.phraseText = phraseText; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
