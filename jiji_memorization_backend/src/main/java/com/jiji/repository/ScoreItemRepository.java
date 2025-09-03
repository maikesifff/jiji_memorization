package com.jiji.repository;

import com.jiji.entity.ScoreItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreItemRepository extends JpaRepository<ScoreItem, Long> {
    
    // 根据成绩报告ID查找成绩项
    List<ScoreItem> findByReportId(Long reportId);
    
    // 根据单词ID查找成绩项
    List<ScoreItem> findByWordId(Long wordId);
    
    // 根据成绩报告ID和单词ID查找成绩项
    Optional<ScoreItem> findByReportIdAndWordId(Long reportId, Long wordId);
}
