package com.jiji.dto;

import java.time.LocalDateTime;

public class MeaningDTO {
    private Long id;
    private Long wordId;
    private String wordText;      // 单词文本

    private String pos;           // 词性
    private String content;       // 释义内容
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造函数
    public MeaningDTO() {}

    public MeaningDTO(Long id, Long wordId, String wordText, String pos, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.wordId = wordId;
        this.wordText = wordText;
        this.pos = pos;
        this.content = content;
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



    public String getPos() { return pos; }
    public void setPos(String pos) { this.pos = pos; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
