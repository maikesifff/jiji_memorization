package com.jiji.repository;

import com.jiji.entity.ScoreReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScoreReportRepository extends JpaRepository<ScoreReport, Long> {
    
    // 根据用户ID查找成绩报告
    List<ScoreReport> findByUserId(Long userId);
    
    // 根据创建时间范围查找成绩报告
    List<ScoreReport> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // 根据用户ID和创建时间范围查找成绩报告
    List<ScoreReport> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
