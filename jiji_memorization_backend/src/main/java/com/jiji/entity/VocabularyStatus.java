package com.jiji.entity;

public enum VocabularyStatus {
    NEW("新词"),
    LEARNING("学习中"),
    MASTERED("已掌握");
    
    private final String description;
    
    VocabularyStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
