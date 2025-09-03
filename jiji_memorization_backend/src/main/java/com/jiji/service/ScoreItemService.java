package com.jiji.service;

import com.jiji.entity.ScoreItem;
import com.jiji.repository.ScoreItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScoreItemService {
    
    @Autowired
    private ScoreItemRepository scoreItemRepository;
    
    // 获取所有成绩项
    public List<ScoreItem> getAllScoreItems() {
        return scoreItemRepository.findAll();
    }
    
    // 根据ID获取成绩项
    public Optional<ScoreItem> getScoreItemById(Long id) {
        return scoreItemRepository.findById(id);
    }
    
    // 根据成绩报告ID获取成绩项
    public List<ScoreItem> getScoreItemsByReportId(Long reportId) {
        return scoreItemRepository.findByReportId(reportId);
    }
    
    // 根据单词ID获取成绩项
    public List<ScoreItem> getScoreItemsByWordId(Long wordId) {
        return scoreItemRepository.findByWordId(wordId);
    }
    
    // 根据成绩报告ID和单词ID获取成绩项
    public Optional<ScoreItem> getScoreItemByReportIdAndWordId(Long reportId, Long wordId) {
        return scoreItemRepository.findByReportIdAndWordId(reportId, wordId);
    }
    
    // 创建新成绩项
    public ScoreItem createScoreItem(ScoreItem scoreItem) {
        return scoreItemRepository.save(scoreItem);
    }
    
    // 更新成绩项
    public ScoreItem updateScoreItem(Long id, ScoreItem scoreItemDetails) {
        ScoreItem scoreItem = scoreItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("成绩项不存在，ID: " + id));
        
        scoreItem.setReportId(scoreItemDetails.getReportId());
        scoreItem.setWordId(scoreItemDetails.getWordId());
        scoreItem.setErrorCount(scoreItemDetails.getErrorCount());
        
        return scoreItemRepository.save(scoreItem);
    }
    
    // 删除成绩项
    public void deleteScoreItem(Long id) {
        if (!scoreItemRepository.existsById(id)) {
            throw new RuntimeException("成绩项不存在，ID: " + id);
        }
        scoreItemRepository.deleteById(id);
    }
    
    // 根据成绩报告ID删除所有成绩项
    public void deleteScoreItemsByReportId(Long reportId) {
        List<ScoreItem> scoreItems = scoreItemRepository.findByReportId(reportId);
        scoreItemRepository.deleteAll(scoreItems);
    }
    
    // 根据单词ID删除所有成绩项
    public void deleteScoreItemsByWordId(Long wordId) {
        List<ScoreItem> scoreItems = scoreItemRepository.findByWordId(wordId);
        scoreItemRepository.deleteAll(scoreItems);
    }
    
    // 获取成绩项总数
    public long getScoreItemCount() {
        return scoreItemRepository.count();
    }
    
    // 根据成绩报告ID获取成绩项总数
    public long getScoreItemCountByReportId(Long reportId) {
        return scoreItemRepository.findByReportId(reportId).size();
    }
    
    // 根据单词ID获取成绩项总数
    public long getScoreItemCountByWordId(Long wordId) {
        return scoreItemRepository.findByWordId(wordId).size();
    }
    
    // 计算成绩报告平均错误次数
    public double getScoreReportAverageErrorCount(Long reportId) {
        List<ScoreItem> scoreItems = scoreItemRepository.findByReportId(reportId);
        if (scoreItems.isEmpty()) {
            return 0.0;
        }
        
        double totalErrorCount = scoreItems.stream()
                .mapToDouble(ScoreItem::getErrorCount)
                .sum();
        return totalErrorCount / scoreItems.size();
    }
}
