package com.jiji.dto;

import com.jiji.entity.VocabularyNotebook;
import com.jiji.entity.VocabularyStatus;
import java.time.LocalDateTime;

public class VocabularyNotebookDTO {
    private Long id;
    private Long userId;
    private Long wordId;
    private String word;
    private String phonetic;
    private String americanPhonetic;
    private String britishPhonetic;
    private String pronUs;
    private String pronUk;
    private LocalDateTime createdAt;
    private Integer reviewCount;
    private LocalDateTime lastReviewedAt;
    private Integer difficultyLevel;
    private String notes;
    private VocabularyStatus status;
    private LocalDateTime updatedAt;
    private String meanings; // 格式化的词义信息

    // 构造函数
    public VocabularyNotebookDTO() {}


    public VocabularyNotebookDTO(VocabularyNotebook notebook, String word, String americanPhonetic, String britishPhonetic, String pronUs, String pronUk) {
        this.id = notebook.getId();
        this.userId = notebook.getUserId();
        this.wordId = notebook.getWordId();
        this.word = word;
        this.americanPhonetic = americanPhonetic;
        this.britishPhonetic = britishPhonetic;
        this.pronUs = pronUs;
        this.pronUk = pronUk;
        this.createdAt = notebook.getCreatedAt();
        this.reviewCount = notebook.getReviewCount();
        this.lastReviewedAt = notebook.getLastReviewedAt();
        this.difficultyLevel = notebook.getDifficultyLevel();
        this.notes = notebook.getNotes();
        this.status = notebook.getStatus();
        this.updatedAt = notebook.getUpdatedAt();
    }


    public VocabularyNotebookDTO(VocabularyNotebook notebook, String word, String americanPhonetic, String britishPhonetic, String pronUs, String pronUk, String meanings) {
        this.id = notebook.getId();
        this.userId = notebook.getUserId();
        this.wordId = notebook.getWordId();
        this.word = word;
        this.americanPhonetic = americanPhonetic;
        this.britishPhonetic = britishPhonetic;
        this.pronUs = pronUs;
        this.pronUk = pronUk;
        this.createdAt = notebook.getCreatedAt();
        this.reviewCount = notebook.getReviewCount();
        this.lastReviewedAt = notebook.getLastReviewedAt();
        this.difficultyLevel = notebook.getDifficultyLevel();
        this.notes = notebook.getNotes();
        this.status = notebook.getStatus();
        this.updatedAt = notebook.getUpdatedAt();
        this.meanings = meanings;
    }

    // Getters and Setters
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

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getAmericanPhonetic() {
        return americanPhonetic;
    }

    public void setAmericanPhonetic(String americanPhonetic) {
        this.americanPhonetic = americanPhonetic;
    }

    public String getBritishPhonetic() {
        return britishPhonetic;
    }

    public void setBritishPhonetic(String britishPhonetic) {
        this.britishPhonetic = britishPhonetic;
    }

    public String getPronUs() {
        return pronUs;
    }

    public void setPronUs(String pronUs) {
        this.pronUs = pronUs;
    }

    public String getPronUk() {
        return pronUk;
    }

    public void setPronUk(String pronUk) {
        this.pronUk = pronUk;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public LocalDateTime getLastReviewedAt() {
        return lastReviewedAt;
    }

    public void setLastReviewedAt(LocalDateTime lastReviewedAt) {
        this.lastReviewedAt = lastReviewedAt;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public VocabularyStatus getStatus() {
        return status;
    }

    public void setStatus(VocabularyStatus status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMeanings() {
        return meanings;
    }

    public void setMeanings(String meanings) {
        this.meanings = meanings;
    }
}
