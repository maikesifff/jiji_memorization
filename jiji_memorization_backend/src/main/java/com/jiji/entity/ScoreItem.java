package com.jiji.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "score_item",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_score_item_report_word", columnNames = {"report_id", "word_id"})
       },
       indexes = {
           @Index(name = "idx_score_item_report_id", columnList = "report_id"),
           @Index(name = "idx_score_item_word_id", columnList = "word_id")
       })
public class ScoreItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_id", nullable = false)
    private Long reportId; // 逻辑外键

    @Column(name = "word_id", nullable = false)
    private Long wordId; // 逻辑外键

    @Column(name = "error_count", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 0")
    private Integer errorCount = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }

    public Integer getErrorCount() { return errorCount; }
    public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }
}
