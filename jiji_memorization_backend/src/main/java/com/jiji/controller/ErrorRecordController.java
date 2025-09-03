package com.jiji.controller;

import com.jiji.entity.ErrorRecord;
import com.jiji.service.ErrorRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/error-records")
@CrossOrigin(origins = "*")
public class ErrorRecordController {
    
    @Autowired
    private ErrorRecordService errorRecordService;
    
    // 获取所有错误记录
    @GetMapping
    public ResponseEntity<List<ErrorRecord>> getAllErrorRecords() {
        List<ErrorRecord> errorRecords = errorRecordService.getAllErrorRecords();
        return ResponseEntity.ok(errorRecords);
    }
    
    // 根据ID获取错误记录
    @GetMapping("/{id}")
    public ResponseEntity<ErrorRecord> getErrorRecordById(@PathVariable Long id) {
        return errorRecordService.getErrorRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据用户ID获取错误记录
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ErrorRecord>> getErrorRecordsByUserId(@PathVariable Long userId) {
        List<ErrorRecord> errorRecords = errorRecordService.getErrorRecordsByUserId(userId);
        return ResponseEntity.ok(errorRecords);
    }
    
    // 根据单元单词ID获取错误记录
    @GetMapping("/unit-word/{unitWordId}")
    public ResponseEntity<List<ErrorRecord>> getErrorRecordsByUnitWordId(@PathVariable Long unitWordId) {
        List<ErrorRecord> errorRecords = errorRecordService.getErrorRecordsByUnitWordId(unitWordId);
        return ResponseEntity.ok(errorRecords);
    }

    // 获取用户在指定单元的学习记录
    @GetMapping("/user/{userId}/unit-word/unit/{unitId}")
    public ResponseEntity<List<ErrorRecord>> getErrorRecordsByUserIdAndUnitId(
            @PathVariable Long userId, @PathVariable Long unitId) {
        List<ErrorRecord> errorRecords = errorRecordService.getErrorRecordsByUserIdAndUnitId(userId, unitId);
        return ResponseEntity.ok(errorRecords);
    }
    
    // 根据用户ID和单元单词ID获取错误记录
    @GetMapping("/user/{userId}/unit-word/{unitWordId}")
    public ResponseEntity<ErrorRecord> getErrorRecordByUserIdAndUnitWordId(
            @PathVariable Long userId, @PathVariable Long unitWordId) {
        return errorRecordService.getErrorRecordByUserIdAndUnitWordId(userId, unitWordId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 创建新的错误记录
    @PostMapping
    public ResponseEntity<ErrorRecord> createErrorRecord(@RequestBody ErrorRecord errorRecord) {
        try {
            ErrorRecord createdErrorRecord = errorRecordService.createErrorRecord(errorRecord);
            return ResponseEntity.ok(createdErrorRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 更新错误记录
    @PutMapping("/{id}")
    public ResponseEntity<ErrorRecord> updateErrorRecord(@PathVariable Long id, @RequestBody ErrorRecord errorRecordDetails) {
        try {
            ErrorRecord updatedErrorRecord = errorRecordService.updateErrorRecord(id, errorRecordDetails);
            return ResponseEntity.ok(updatedErrorRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 删除错误记录
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteErrorRecord(@PathVariable Long id) {
        try {
            errorRecordService.deleteErrorRecord(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据用户ID删除所有错误记录
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteErrorRecordsByUserId(@PathVariable Long userId) {
        try {
            errorRecordService.deleteErrorRecordsByUserId(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据单元单词ID删除所有错误记录
    @DeleteMapping("/unit-word/{unitWordId}")
    public ResponseEntity<Void> deleteErrorRecordsByUnitWordId(@PathVariable Long unitWordId) {
        try {
            errorRecordService.deleteErrorRecordsByUnitWordId(unitWordId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 增加错误次数
    @PostMapping("/increment")
    public ResponseEntity<ErrorRecord> incrementErrorCount(
            @RequestParam Long userId, @RequestParam Long unitWordId) {
        try {
            ErrorRecord errorRecord = errorRecordService.incrementErrorCount(userId, unitWordId);
            return ResponseEntity.ok(errorRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 增加正确次数
    @PostMapping("/increment-correct")
    public ResponseEntity<ErrorRecord> incrementCorrectCount(
            @RequestParam Long userId, @RequestParam Long unitWordId) {
        try {
            ErrorRecord errorRecord = errorRecordService.incrementCorrectCount(userId, unitWordId);
            return ResponseEntity.ok(errorRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 记录答题结果（统一方法）
    @PostMapping("/record-answer")
    public ResponseEntity<ErrorRecord> recordAnswer(
            @RequestParam Long userId, @RequestParam Long unitWordId, @RequestParam boolean isCorrect) {
        try {
            ErrorRecord errorRecord = errorRecordService.recordAnswer(userId, unitWordId, isCorrect);
            return ResponseEntity.ok(errorRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 获取错误记录总数
    @GetMapping("/count")
    public ResponseEntity<Long> getErrorRecordCount() {
        long count = errorRecordService.getErrorRecordCount();
        return ResponseEntity.ok(count);
    }
    
    // 根据用户ID获取错误记录总数
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getErrorRecordCountByUserId(@PathVariable Long userId) {
        long count = errorRecordService.getErrorRecordCountByUserId(userId);
        return ResponseEntity.ok(count);
    }
    
    // 根据单元单词ID获取错误记录总数
    @GetMapping("/unit-word/{unitWordId}/count")
    public ResponseEntity<Long> getErrorRecordCountByUnitWordId(@PathVariable Long unitWordId) {
        long count = errorRecordService.getErrorRecordCountByUnitWordId(unitWordId);
        return ResponseEntity.ok(count);
    }
}
