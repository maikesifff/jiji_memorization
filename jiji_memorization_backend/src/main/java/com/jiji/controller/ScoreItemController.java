package com.jiji.controller;

import com.jiji.entity.ScoreItem;
import com.jiji.service.ScoreItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/score-items")
@CrossOrigin(origins = "*")
public class ScoreItemController {
    
    @Autowired
    private ScoreItemService scoreItemService;
    
    // 获取所有成绩项
    @GetMapping
    public ResponseEntity<List<ScoreItem>> getAllScoreItems() {
        List<ScoreItem> scoreItems = scoreItemService.getAllScoreItems();
        return ResponseEntity.ok(scoreItems);
    }
    
    // 根据ID获取成绩项
    @GetMapping("/{id}")
    public ResponseEntity<ScoreItem> getScoreItemById(@PathVariable Long id) {
        return scoreItemService.getScoreItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据成绩报告ID获取成绩项
    @GetMapping("/report/{reportId}")
    public ResponseEntity<List<ScoreItem>> getScoreItemsByReportId(@PathVariable Long reportId) {
        List<ScoreItem> scoreItems = scoreItemService.getScoreItemsByReportId(reportId);
        return ResponseEntity.ok(scoreItems);
    }
    
    // 根据单词ID获取成绩项
    @GetMapping("/word/{wordId}")
    public ResponseEntity<List<ScoreItem>> getScoreItemsByWordId(@PathVariable Long wordId) {
        List<ScoreItem> scoreItems = scoreItemService.getScoreItemsByWordId(wordId);
        return ResponseEntity.ok(scoreItems);
    }
    
    // 根据成绩报告ID和单词ID获取成绩项
    @GetMapping("/report/{reportId}/word/{wordId}")
    public ResponseEntity<ScoreItem> getScoreItemByReportIdAndWordId(
            @PathVariable Long reportId, @PathVariable Long wordId) {
        return scoreItemService.getScoreItemByReportIdAndWordId(reportId, wordId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 创建新成绩项
    @PostMapping
    public ResponseEntity<ScoreItem> createScoreItem(@RequestBody ScoreItem scoreItem) {
        try {
            ScoreItem createdScoreItem = scoreItemService.createScoreItem(scoreItem);
            return ResponseEntity.ok(createdScoreItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 更新成绩项
    @PutMapping("/{id}")
    public ResponseEntity<ScoreItem> updateScoreItem(@PathVariable Long id, @RequestBody ScoreItem scoreItemDetails) {
        try {
            ScoreItem updatedScoreItem = scoreItemService.updateScoreItem(id, scoreItemDetails);
            return ResponseEntity.ok(updatedScoreItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 删除成绩项
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScoreItem(@PathVariable Long id) {
        try {
            scoreItemService.deleteScoreItem(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据成绩报告ID删除所有成绩项
    @DeleteMapping("/report/{reportId}")
    public ResponseEntity<Void> deleteScoreItemsByReportId(@PathVariable Long reportId) {
        try {
            scoreItemService.deleteScoreItemsByReportId(reportId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据单词ID删除所有成绩项
    @DeleteMapping("/word/{wordId}")
    public ResponseEntity<Void> deleteScoreItemsByWordId(@PathVariable Long wordId) {
        try {
            scoreItemService.deleteScoreItemsByWordId(wordId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 获取成绩项总数
    @GetMapping("/count")
    public ResponseEntity<Long> getScoreItemCount() {
        long count = scoreItemService.getScoreItemCount();
        return ResponseEntity.ok(count);
    }
    
    // 根据成绩报告ID获取成绩项总数
    @GetMapping("/report/{reportId}/count")
    public ResponseEntity<Long> getScoreItemCountByReportId(@PathVariable Long reportId) {
        long count = scoreItemService.getScoreItemCountByReportId(reportId);
        return ResponseEntity.ok(count);
    }
    
    // 根据单词ID获取成绩项总数
    @GetMapping("/word/{wordId}/count")
    public ResponseEntity<Long> getScoreItemCountByWordId(@PathVariable Long wordId) {
        long count = scoreItemService.getScoreItemCountByWordId(wordId);
        return ResponseEntity.ok(count);
    }
    
    // 获取成绩报告平均错误次数
    @GetMapping("/report/{reportId}/average-error-count")
    public ResponseEntity<Double> getScoreReportAverageErrorCount(@PathVariable Long reportId) {
        double averageErrorCount = scoreItemService.getScoreReportAverageErrorCount(reportId);
        return ResponseEntity.ok(averageErrorCount);
    }
}
