package com.jiji.controller;

import com.jiji.dto.VocabularyNotebookDTO;
import com.jiji.entity.VocabularyNotebook;
import com.jiji.entity.VocabularyStatus;
import com.jiji.service.VocabularyNotebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vocabulary-notebook")
public class VocabularyNotebookController {
    
    @Autowired
    private VocabularyNotebookService vocabularyNotebookService;
    
    // 添加单词到生词本
    @PostMapping("/add")
    public ResponseEntity<?> addWordToNotebook(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long wordId = Long.valueOf(request.get("wordId").toString());
            
            VocabularyNotebook notebook = vocabularyNotebookService.addWordToNotebook(userId, wordId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "单词已添加到生词本");
            response.put("notebook", notebook);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 从生词本中移除单词
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeWordFromNotebook(@RequestParam Long userId, @RequestParam Long wordId) {
        try {
            vocabularyNotebookService.removeWordFromNotebook(userId, wordId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "单词已从生词本中移除");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 获取用户的生词本
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserNotebook(@PathVariable Long userId) {
        try {
            List<VocabularyNotebookDTO> notebook = vocabularyNotebookService.getUserNotebookWithWords(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("notebook", notebook);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 获取用户的生词本（分页）
    @GetMapping("/user/{userId}/page")
    public ResponseEntity<?> getUserNotebookPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            Page<VocabularyNotebookDTO> notebookPage = vocabularyNotebookService.getUserNotebookWithWords(userId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("content", notebookPage.getContent());
            response.put("totalElements", notebookPage.getTotalElements());
            response.put("totalPages", notebookPage.getTotalPages());
            response.put("currentPage", notebookPage.getNumber());
            response.put("size", notebookPage.getSize());
            response.put("hasNext", notebookPage.hasNext());
            response.put("hasPrevious", notebookPage.hasPrevious());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 根据状态获取用户的生词本
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<?> getUserNotebookByStatus(@PathVariable Long userId, @PathVariable String status) {
        try {
            VocabularyStatus vocabularyStatus = VocabularyStatus.valueOf(status.toUpperCase());
            List<VocabularyNotebook> notebook = vocabularyNotebookService.getUserNotebookByStatus(userId, vocabularyStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("notebook", notebook);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 根据状态获取用户的生词本（分页）
    @GetMapping("/user/{userId}/status/{status}/page")
    public ResponseEntity<?> getUserNotebookByStatusPage(
            @PathVariable Long userId,
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            VocabularyStatus vocabularyStatus = VocabularyStatus.valueOf(status.toUpperCase());
            Page<VocabularyNotebookDTO> notebookPage = vocabularyNotebookService.getUserNotebookWithWordsByStatus(userId, vocabularyStatus, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("content", notebookPage.getContent());
            response.put("totalElements", notebookPage.getTotalElements());
            response.put("totalPages", notebookPage.getTotalPages());
            response.put("currentPage", notebookPage.getNumber());
            response.put("size", notebookPage.getSize());
            response.put("hasNext", notebookPage.hasNext());
            response.put("hasPrevious", notebookPage.hasPrevious());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 更新单词复习记录
    @PutMapping("/review")
    public ResponseEntity<?> updateReview(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long wordId = Long.valueOf(request.get("wordId").toString());
            Boolean correct = Boolean.valueOf(request.get("correct").toString());
            
            VocabularyNotebook notebook = vocabularyNotebookService.updateReview(userId, wordId, correct);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "复习记录已更新");
            response.put("notebook", notebook);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 更新单词难度等级
    @PutMapping("/difficulty")
    public ResponseEntity<?> updateDifficultyLevel(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long wordId = Long.valueOf(request.get("wordId").toString());
            Integer difficultyLevel = Integer.valueOf(request.get("difficultyLevel").toString());
            
            VocabularyNotebook notebook = vocabularyNotebookService.updateDifficultyLevel(userId, wordId, difficultyLevel);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "难度等级已更新");
            response.put("notebook", notebook);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 更新单词笔记
    @PutMapping("/notes")
    public ResponseEntity<?> updateNotes(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long wordId = Long.valueOf(request.get("wordId").toString());
            String notes = request.get("notes").toString();
            
            VocabularyNotebook notebook = vocabularyNotebookService.updateNotes(userId, wordId, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "笔记已更新");
            response.put("notebook", notebook);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 获取需要复习的单词
    @GetMapping("/user/{userId}/review")
    public ResponseEntity<?> getWordsForReview(@PathVariable Long userId) {
        try {
            List<VocabularyNotebook> wordsForReview = vocabularyNotebookService.getWordsForReview(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("wordsForReview", wordsForReview);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 获取生词本统计信息
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getNotebookStats(@PathVariable Long userId) {
        try {
            VocabularyNotebookService.VocabularyNotebookStats stats = vocabularyNotebookService.getNotebookStats(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("stats", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 检查单词是否在生词本中
    @GetMapping("/check")
    public ResponseEntity<?> checkWordInNotebook(@RequestParam Long userId, @RequestParam Long wordId) {
        try {
            boolean isInNotebook = vocabularyNotebookService.isWordInNotebook(userId, wordId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("isInNotebook", isInNotebook);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
