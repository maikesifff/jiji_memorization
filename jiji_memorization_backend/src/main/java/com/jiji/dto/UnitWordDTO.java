package com.jiji.dto;

import java.time.LocalDateTime;

public class UnitWordDTO {
    private Long id;
    private Long unitId;
    private String unitName;
    private Long textbookId;
    private String textbookName;
    private String textbookGrade;
    private String textbookPublisher;
    private Long wordId;
    private String wordText;
    private String phonetic;      // 音标（优先显示美音，如果没有则显示英音）
    private LocalDateTime createdAt;

    // 无参构造函数（Hibernate 需要）
    public UnitWordDTO() {}
    
    public UnitWordDTO(Long id, Long unitId, String unitName, Long textbookId, String textbookName, 
                      String textbookGrade, String textbookPublisher, Long wordId, String wordText, 
                      String phonetic, LocalDateTime createdAt) {
        this.id = id;
        this.unitId = unitId;
        this.unitName = unitName;
        this.textbookId = textbookId;
        this.textbookName = textbookName;
        this.textbookGrade = textbookGrade;
        this.textbookPublisher = textbookPublisher;
        this.wordId = wordId;
        this.wordText = wordText;
        this.phonetic = phonetic;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public Long getTextbookId() { return textbookId; }
    public void setTextbookId(Long textbookId) { this.textbookId = textbookId; }

    public String getTextbookName() { return textbookName; }
    public void setTextbookName(String textbookName) { this.textbookName = textbookName; }

    public String getTextbookGrade() { return textbookGrade; }
    public void setTextbookGrade(String textbookGrade) { this.textbookGrade = textbookGrade; }

    public String getTextbookPublisher() { return textbookPublisher; }
    public void setTextbookPublisher(String textbookPublisher) { this.textbookPublisher = textbookPublisher; }

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }

    public String getWordText() { return wordText; }
    public void setWordText(String wordText) { this.wordText = wordText; }

    public String getPhonetic() { return phonetic; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
