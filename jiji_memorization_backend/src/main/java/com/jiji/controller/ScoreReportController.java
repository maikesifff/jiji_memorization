package com.jiji.controller;

import com.jiji.entity.ScoreReport;
import com.jiji.service.ScoreReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/score-reports")
@CrossOrigin(origins = "*")
public class ScoreReportController {
    
    @Autowired
    private ScoreReportService scoreReportService;
    
    // 获取所有成绩报告
    @GetMapping
    public ResponseEntity<List<ScoreReport>> getAllScoreReports() {
        List<ScoreReport> scoreReports = scoreReportService.getAllScoreReports();
        return ResponseEntity.ok(scoreReports);
    }
    
    // 根据ID获取成绩报告
    @GetMapping("/{id}")
    public ResponseEntity<ScoreReport> getScoreReportById(@PathVariable Long id) {
        return scoreReportService.getScoreReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据用户ID获取成绩报告
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ScoreReport>> getScoreReportsByUserId(@PathVariable Long userId) {
        List<ScoreReport> scoreReports = scoreReportService.getScoreReportsByUserId(userId);
        return ResponseEntity.ok(scoreReports);
    }
    
    // 根据创建时间范围获取成绩报告
    @GetMapping("/created-between")
    public ResponseEntity<List<ScoreReport>> getScoreReportsByCreatedAtBetween(
            @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        List<ScoreReport> scoreReports = scoreReportService.getScoreReportsByCreatedAtBetween(start, end);
        return ResponseEntity.ok(scoreReports);
    }
    
    // 根据用户ID和创建时间范围获取成绩报告
    @GetMapping("/user/{userId}/created-between")
    public ResponseEntity<List<ScoreReport>> getScoreReportsByUserIdAndCreatedAtBetween(
            @PathVariable Long userId, @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        List<ScoreReport> scoreReports = scoreReportService.getScoreReportsByUserIdAndCreatedAtBetween(userId, start, end);
        return ResponseEntity.ok(scoreReports);
    }
    
    // 创建新成绩报告
    @PostMapping
    public ResponseEntity<ScoreReport> createScoreReport(@RequestBody ScoreReport scoreReport) {
        try {
            ScoreReport createdScoreReport = scoreReportService.createScoreReport(scoreReport);
            return ResponseEntity.ok(createdScoreReport);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 更新成绩报告
    @PutMapping("/{id}")
    public ResponseEntity<ScoreReport> updateScoreReport(@PathVariable Long id, @RequestBody ScoreReport scoreReportDetails) {
        try {
            ScoreReport updatedScoreReport = scoreReportService.updateScoreReport(id, scoreReportDetails);
            return ResponseEntity.ok(updatedScoreReport);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 删除成绩报告
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScoreReport(@PathVariable Long id) {
        try {
            scoreReportService.deleteScoreReport(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据用户ID删除所有成绩报告
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteScoreReportsByUserId(@PathVariable Long userId) {
        try {
            scoreReportService.deleteScoreReportsByUserId(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 获取成绩报告总数
    @GetMapping("/count")
    public ResponseEntity<Long> getScoreReportCount() {
        long count = scoreReportService.getScoreReportCount();
        return ResponseEntity.ok(count);
    }
    
    // 根据用户ID获取成绩报告总数
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getScoreReportCountByUserId(@PathVariable Long userId) {
        long count = scoreReportService.getScoreReportCountByUserId(userId);
        return ResponseEntity.ok(count);
    }
}
