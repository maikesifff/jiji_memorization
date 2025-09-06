package com.jiji.controller;

import com.jiji.entity.ErrorRecord;
import com.jiji.service.ErrorRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/error-records")
@CrossOrigin(origins = "*")
public class ErrorRecordController {
    
    @Autowired
    private ErrorRecordService errorRecordService;
    
    // 记录答题结果
    @PostMapping("/record")
    public ResponseEntity<Map<String, Object>> recordAnswer(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long unitWordId = Long.valueOf(request.get("unitWordId").toString());
            Boolean isCorrect = Boolean.valueOf(request.get("isCorrect").toString());
            
            ErrorRecord record = errorRecordService.recordAnswer(userId, unitWordId, isCorrect);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "答题记录已保存");
            response.put("record", record);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "保存答题记录失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 根据单词ID记录答题结果
    @PostMapping("/record-by-word")
    public ResponseEntity<Map<String, Object>> recordAnswerByWord(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long wordId = Long.valueOf(request.get("wordId").toString());
            Long unitId = Long.valueOf(request.get("unitId").toString());
            Boolean isCorrect = Boolean.valueOf(request.get("isCorrect").toString());
            
            ErrorRecord record = errorRecordService.recordAnswerByWordId(userId, wordId, unitId, isCorrect);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "答题记录已保存");
            response.put("record", record);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "保存答题记录失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 获取用户的答题记录
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ErrorRecord>> getUserRecords(@PathVariable Long userId) {
        List<ErrorRecord> records = errorRecordService.getUserRecords(userId);
        return ResponseEntity.ok(records);
    }
    
    // 获取用户在指定单元的答题记录
    @GetMapping("/user/{userId}/unit/{unitId}")
    public ResponseEntity<List<ErrorRecord>> getUserUnitRecords(@PathVariable Long userId, @PathVariable Long unitId) {
        List<ErrorRecord> records = errorRecordService.getUserRecordsByUnit(userId, unitId);
        return ResponseEntity.ok(records);
    }
    
    // 获取用户统计信息
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable Long userId) {
        Map<String, Object> stats = errorRecordService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }
    
    // 获取用户在指定单元的统计信息
    @GetMapping("/user/{userId}/unit/{unitId}/stats")
    public ResponseEntity<Map<String, Object>> getUserUnitStats(@PathVariable Long userId, @PathVariable Long unitId) {
        Map<String, Object> stats = errorRecordService.getUserUnitStats(userId, unitId);
        return ResponseEntity.ok(stats);
    }
    
    // 检查单词答题状态
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkWordStatus(
            @RequestParam Long userId,
            @RequestParam Long unitWordId) {
        try {
            boolean isCorrect = errorRecordService.isWordCorrect(userId, unitWordId);
            boolean isIncorrect = errorRecordService.isWordIncorrect(userId, unitWordId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isCorrect", isCorrect);
            response.put("isIncorrect", isIncorrect);
            response.put("isAnswered", isCorrect || isIncorrect);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "检查答题状态失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 获取用户答对的单词ID列表
    @GetMapping("/user/{userId}/unit/{unitId}/correct")
    public ResponseEntity<List<Long>> getCorrectWordIds(@PathVariable Long userId, @PathVariable Long unitId) {
        List<Long> correctWordIds = errorRecordService.getCorrectWordIds(userId, unitId);
        return ResponseEntity.ok(correctWordIds);
    }
    
    // 获取用户答错的单词ID列表
    @GetMapping("/user/{userId}/unit/{unitId}/incorrect")
    public ResponseEntity<List<Long>> getIncorrectWordIds(@PathVariable Long userId, @PathVariable Long unitId) {
        List<Long> incorrectWordIds = errorRecordService.getIncorrectWordIds(userId, unitId);
        return ResponseEntity.ok(incorrectWordIds);
    }
    
    // 删除用户的答题记录
    @DeleteMapping("/user/{userId}/unit-word/{unitWordId}")
    public ResponseEntity<Map<String, Object>> deleteUserRecord(@PathVariable Long userId, @PathVariable Long unitWordId) {
        try {
            errorRecordService.deleteUserRecord(userId, unitWordId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "答题记录已删除");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "删除答题记录失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}