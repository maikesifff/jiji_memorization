package com.jiji.service;

import com.jiji.entity.ScoreReport;
import com.jiji.repository.ScoreReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScoreReportService {
    
    @Autowired
    private ScoreReportRepository scoreReportRepository;
    
    // 获取所有成绩报告
    public List<ScoreReport> getAllScoreReports() {
        return scoreReportRepository.findAll();
    }
    
    // 根据ID获取成绩报告
    public Optional<ScoreReport> getScoreReportById(Long id) {
        return scoreReportRepository.findById(id);
    }
    
    // 根据用户ID获取成绩报告
    public List<ScoreReport> getScoreReportsByUserId(Long userId) {
        return scoreReportRepository.findByUserId(userId);
    }
    
    // 根据创建时间范围获取成绩报告
    public List<ScoreReport> getScoreReportsByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return scoreReportRepository.findByCreatedAtBetween(start, end);
    }
    
    // 根据用户ID和创建时间范围获取成绩报告
    public List<ScoreReport> getScoreReportsByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end) {
        return scoreReportRepository.findByUserIdAndCreatedAtBetween(userId, start, end);
    }
    
    // 创建新成绩报告
    public ScoreReport createScoreReport(ScoreReport scoreReport) {
        scoreReport.setCreatedAt(LocalDateTime.now());
        scoreReport.setUpdatedAt(LocalDateTime.now());
        return scoreReportRepository.save(scoreReport);
    }
    
    // 更新成绩报告
    public ScoreReport updateScoreReport(Long id, ScoreReport scoreReportDetails) {
        ScoreReport scoreReport = scoreReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("成绩报告不存在，ID: " + id));
        
        scoreReport.setUserId(scoreReportDetails.getUserId());
        scoreReport.setReportName(scoreReportDetails.getReportName());
        scoreReport.setReportDesc(scoreReportDetails.getReportDesc());
        scoreReport.setUpdatedAt(LocalDateTime.now());
        
        return scoreReportRepository.save(scoreReport);
    }
    
    // 删除成绩报告
    public void deleteScoreReport(Long id) {
        if (!scoreReportRepository.existsById(id)) {
            throw new RuntimeException("成绩报告不存在，ID: " + id);
        }
        scoreReportRepository.deleteById(id);
    }
    
    // 根据用户ID删除所有成绩报告
    public void deleteScoreReportsByUserId(Long userId) {
        List<ScoreReport> scoreReports = scoreReportRepository.findByUserId(userId);
        scoreReportRepository.deleteAll(scoreReports);
    }
    
    // 获取成绩报告总数
    public long getScoreReportCount() {
        return scoreReportRepository.count();
    }
    
    // 根据用户ID获取成绩报告总数
    public long getScoreReportCountByUserId(Long userId) {
        return scoreReportRepository.findByUserId(userId).size();
    }
}
