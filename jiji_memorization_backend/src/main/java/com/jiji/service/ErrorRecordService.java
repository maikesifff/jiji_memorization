package com.jiji.service;

import com.jiji.entity.ErrorRecord;
import com.jiji.repository.ErrorRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ErrorRecordService {
    
    @Autowired
    private ErrorRecordRepository errorRecordRepository;
    
    // 获取所有错误记录
    public List<ErrorRecord> getAllErrorRecords() {
        return errorRecordRepository.findAll();
    }
    
    // 根据ID获取错误记录
    public Optional<ErrorRecord> getErrorRecordById(Long id) {
        return errorRecordRepository.findById(id);
    }
    
    // 根据用户ID获取错误记录
    public List<ErrorRecord> getErrorRecordsByUserId(Long userId) {
        return errorRecordRepository.findByUserId(userId);
    }
    
    // 根据单元单词ID获取错误记录
    public List<ErrorRecord> getErrorRecordsByUnitWordId(Long unitWordId) {
        return errorRecordRepository.findByUnitWordId(unitWordId);
    }

    // 根据用户ID和单元ID获取错误记录
    public List<ErrorRecord> getErrorRecordsByUserIdAndUnitId(Long userId, Long unitId) {
        return errorRecordRepository.findByUserIdAndUnitId(userId, unitId);
    }
    
    // 根据用户ID和单元单词ID获取错误记录
    public Optional<ErrorRecord> getErrorRecordByUserIdAndUnitWordId(Long userId, Long unitWordId) {
        return errorRecordRepository.findByUserIdAndUnitWordId(userId, unitWordId);
    }
    
    // 创建新的错误记录
    public ErrorRecord createErrorRecord(ErrorRecord errorRecord) {
        errorRecord.setCreatedAt(LocalDateTime.now());
        errorRecord.setUpdatedAt(LocalDateTime.now());
        return errorRecordRepository.save(errorRecord);
    }
    
    // 更新错误记录
    public ErrorRecord updateErrorRecord(Long id, ErrorRecord errorRecordDetails) {
        ErrorRecord errorRecord = errorRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("错误记录不存在，ID: " + id));
        
        errorRecord.setUserId(errorRecordDetails.getUserId());
        errorRecord.setUnitWordId(errorRecordDetails.getUnitWordId());
        errorRecord.setErrorCount(errorRecordDetails.getErrorCount());
        errorRecord.setCorrectCount(errorRecordDetails.getCorrectCount());
        errorRecord.setLastAnswerCorrect(errorRecordDetails.getLastAnswerCorrect());
        errorRecord.setLastErrorAt(errorRecordDetails.getLastErrorAt());
        errorRecord.setUpdatedAt(LocalDateTime.now());
        
        return errorRecordRepository.save(errorRecord);
    }
    
    // 删除错误记录
    public void deleteErrorRecord(Long id) {
        if (!errorRecordRepository.existsById(id)) {
            throw new RuntimeException("错误记录不存在，ID: " + id);
        }
        errorRecordRepository.deleteById(id);
    }
    
    // 根据用户ID删除所有错误记录
    public void deleteErrorRecordsByUserId(Long userId) {
        List<ErrorRecord> errorRecords = errorRecordRepository.findByUserId(userId);
        errorRecordRepository.deleteAll(errorRecords);
    }
    
    // 根据单元单词ID删除所有错误记录
    public void deleteErrorRecordsByUnitWordId(Long unitWordId) {
        List<ErrorRecord> errorRecords = errorRecordRepository.findByUnitWordId(unitWordId);
        errorRecordRepository.deleteAll(errorRecords);
    }
    
    // 获取错误记录总数
    public long getErrorRecordCount() {
        return errorRecordRepository.count();
    }
    
    // 根据用户ID获取错误记录总数
    public long getErrorRecordCountByUserId(Long userId) {
        return errorRecordRepository.findByUserId(userId).size();
    }
    
    // 根据单元单词ID获取错误记录总数
    public long getErrorRecordCountByUnitWordId(Long unitWordId) {
        return errorRecordRepository.findByUnitWordId(unitWordId).size();
    }
    
    // 增加错误次数
    public ErrorRecord incrementErrorCount(Long userId, Long unitWordId) {
        Optional<ErrorRecord> existingRecord = errorRecordRepository.findByUserIdAndUnitWordId(userId, unitWordId);
        
        if (existingRecord.isPresent()) {
            ErrorRecord record = existingRecord.get();
            record.setErrorCount(record.getErrorCount() + 1);
            record.setLastErrorAt(LocalDateTime.now());
            record.setLastAnswerCorrect(false);
            record.setUpdatedAt(LocalDateTime.now());
            return errorRecordRepository.save(record);
        } else {
            ErrorRecord newRecord = new ErrorRecord();
            newRecord.setUserId(userId);
            newRecord.setUnitWordId(unitWordId);
            newRecord.setErrorCount(1);
            newRecord.setCorrectCount(0);
            newRecord.setLastAnswerCorrect(false);
            newRecord.setLastErrorAt(LocalDateTime.now());
            newRecord.setCreatedAt(LocalDateTime.now());
            newRecord.setUpdatedAt(LocalDateTime.now());
            return errorRecordRepository.save(newRecord);
        }
    }

    // 增加正确次数
    public ErrorRecord incrementCorrectCount(Long userId, Long unitWordId) {
        Optional<ErrorRecord> existingRecord = errorRecordRepository.findByUserIdAndUnitWordId(userId, unitWordId);
        
        if (existingRecord.isPresent()) {
            ErrorRecord record = existingRecord.get();
            record.setCorrectCount(record.getCorrectCount() + 1);
            record.setLastAnswerCorrect(true);
            record.setUpdatedAt(LocalDateTime.now());
            return errorRecordRepository.save(record);
        } else {
            ErrorRecord newRecord = new ErrorRecord();
            newRecord.setUserId(userId);
            newRecord.setUnitWordId(unitWordId);
            newRecord.setErrorCount(0);
            newRecord.setCorrectCount(1);
            newRecord.setLastAnswerCorrect(true);
            newRecord.setCreatedAt(LocalDateTime.now());
            newRecord.setUpdatedAt(LocalDateTime.now());
            return errorRecordRepository.save(newRecord);
        }
    }

    // 记录答题结果（统一方法）
    public ErrorRecord recordAnswer(Long userId, Long unitWordId, boolean isCorrect) {
        if (isCorrect) {
            return incrementCorrectCount(userId, unitWordId);
        } else {
            return incrementErrorCount(userId, unitWordId);
        }
    }
}
