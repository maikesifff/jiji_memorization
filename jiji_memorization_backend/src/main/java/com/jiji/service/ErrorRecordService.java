package com.jiji.service;

import com.jiji.entity.ErrorRecord;
import com.jiji.repository.ErrorRecordRepository;
import com.jiji.repository.UnitWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
@Transactional
public class ErrorRecordService {
    
    @Autowired
    private ErrorRecordRepository errorRecordRepository;
    
    @Autowired
    private UnitWordRepository unitWordRepository;
    
    // 记录答题结果
    public ErrorRecord recordAnswer(Long userId, Long unitWordId, boolean isCorrect) {
        // 查找或创建记录
        Optional<ErrorRecord> existingRecord = errorRecordRepository.findByUserIdAndUnitWordId(userId, unitWordId);
        ErrorRecord record;
        
        if (existingRecord.isPresent()) {
            record = existingRecord.get();
        } else {
            record = new ErrorRecord(userId, unitWordId);
        }
        
        // 更新记录
        if (isCorrect) {
            record.recordCorrectAnswer();
        } else {
            record.recordIncorrectAnswer();
        }
        
        return errorRecordRepository.save(record);
    }
    
    // 根据用户ID和单词ID记录答题结果（需要先查找unitWordId）
    public ErrorRecord recordAnswerByWordId(Long userId, Long wordId, Long unitId, boolean isCorrect) {
        // 查找unitWordId
        Long unitWordId = unitWordRepository.findByUnitId(unitId).stream()
                .filter(uw -> uw.getWordId().equals(wordId))
                .findFirst()
                .map(uw -> uw.getId())
                .orElse(null);
        
        if (unitWordId == null) {
            throw new RuntimeException("未找到单元单词关联: unitId=" + unitId + ", wordId=" + wordId);
        }
        
        return recordAnswer(userId, unitWordId, isCorrect);
    }
    
    // 获取用户的答题记录
    public Optional<ErrorRecord> getRecord(Long userId, Long unitWordId) {
        return errorRecordRepository.findByUserIdAndUnitWordId(userId, unitWordId);
    }
    
    // 获取用户的所有答题记录
    public List<ErrorRecord> getUserRecords(Long userId) {
        return errorRecordRepository.findByUserId(userId);
    }
    
    // 获取用户在指定单元的答题记录
    public List<ErrorRecord> getUserRecordsByUnit(Long userId, Long unitId) {
        return errorRecordRepository.findByUserIdAndUnitId(userId, unitId);
    }
    
    // 获取用户统计信息
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        Long correctCount = errorRecordRepository.countCorrectWordsByUserId(userId);
        Long incorrectCount = errorRecordRepository.countIncorrectWordsByUserId(userId);
        
        stats.put("correctWords", correctCount);
        stats.put("incorrectWords", incorrectCount);
        stats.put("totalWords", correctCount + incorrectCount);
        
        return stats;
    }
    
    // 获取用户在指定单元的统计信息
    public Map<String, Object> getUserUnitStats(Long userId, Long unitId) {
        Map<String, Object> stats = new HashMap<>();
        
        Long correctCount = errorRecordRepository.countCorrectWordsByUserIdAndUnitId(userId, unitId);
        Long incorrectCount = errorRecordRepository.countIncorrectWordsByUserIdAndUnitId(userId, unitId);
        
        stats.put("correctWords", correctCount);
        stats.put("incorrectWords", incorrectCount);
        stats.put("totalWords", correctCount + incorrectCount);
        
        return stats;
    }
    
    // 检查用户是否已经答对过某个单词
    public boolean isWordCorrect(Long userId, Long unitWordId) {
        return errorRecordRepository.isWordCorrectByUser(userId, unitWordId);
    }
    
    // 检查用户是否已经答错过某个单词
    public boolean isWordIncorrect(Long userId, Long unitWordId) {
        return errorRecordRepository.isWordIncorrectByUser(userId, unitWordId);
    }
    
    // 获取用户答对的单词列表（用于排序）
    public List<Long> getCorrectWordIds(Long userId, Long unitId) {
        return errorRecordRepository.findByUserIdAndUnitId(userId, unitId).stream()
                .filter(ErrorRecord::isCorrect)
                .map(ErrorRecord::getUnitWordId)
                .toList();
    }
    
    // 获取用户答错的单词列表（用于排序）
    public List<Long> getIncorrectWordIds(Long userId, Long unitId) {
        return errorRecordRepository.findByUserIdAndUnitId(userId, unitId).stream()
                .filter(record -> !record.isCorrect())
                .map(ErrorRecord::getUnitWordId)
                .toList();
    }
    
    // 删除用户的答题记录
    public void deleteUserRecord(Long userId, Long unitWordId) {
        errorRecordRepository.findByUserIdAndUnitWordId(userId, unitWordId)
                .ifPresent(errorRecordRepository::delete);
    }
    
    // 删除用户的所有答题记录
    public void deleteAllUserRecords(Long userId) {
        errorRecordRepository.findByUserId(userId)
                .forEach(errorRecordRepository::delete);
    }
}