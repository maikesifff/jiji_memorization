package com.jiji.controller;

import com.jiji.entity.Sentence;
import com.jiji.dto.SentenceDTO;
import com.jiji.service.SentenceService;
import com.jiji.util.PageResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sentences")
@CrossOrigin(origins = "*")
public class SentenceController {
    
    @Autowired
    private SentenceService sentenceService;
    
    // 分页获取所有句子
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSentences(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // 使用新的方法，返回包含单词信息的DTO
            Page<SentenceDTO> sentencePage = sentenceService.getAllSentencesWithWordInfo(pageable);

            return PageResponseUtil.createPageResponse(sentencePage, "获取例句列表成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取例句列表失败: " + e.getMessage());
        }
    }
    
    // 根据ID获取句子
    @GetMapping("/{id}")
    public ResponseEntity<Sentence> getSentenceById(@PathVariable Long id) {
        return sentenceService.getSentenceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据单词ID获取句子
    @GetMapping("/word/{wordId}")
    public ResponseEntity<List<Sentence>> getSentencesByWordId(@PathVariable Long wordId) {
        List<Sentence> sentences = sentenceService.getSentencesByWordId(wordId);
        return ResponseEntity.ok(sentences);
    }
    
    // 根据句子内容获取句子
    @GetMapping("/content/{sentenceText}")
    public ResponseEntity<List<Sentence>> getSentencesByContent(@PathVariable String sentenceText) {
        List<Sentence> sentences = sentenceService.getSentencesByContent(sentenceText);
        return ResponseEntity.ok(sentences);
    }
    
    // 根据翻译获取句子
    @GetMapping("/translation/{translation}")
    public ResponseEntity<List<Sentence>> getSentencesByTranslation(@PathVariable String translation) {
        List<Sentence> sentences = sentenceService.getSentencesByTranslation(translation);
        return ResponseEntity.ok(sentences);
    }
    
    // 创建新句子
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSentence(@RequestBody Sentence sentence) {
        try {
            Sentence createdSentence = sentenceService.createSentence(sentence);
            return PageResponseUtil.createSuccessResponse(createdSentence, "创建例句成功");
        } catch (RuntimeException e) {
            return PageResponseUtil.createErrorResponse("创建例句失败: " + e.getMessage());
        }
    }
    
    // 更新句子
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSentence(@PathVariable Long id, @RequestBody Sentence sentenceDetails) {
        try {
            Sentence updatedSentence = sentenceService.updateSentence(id, sentenceDetails);
            return PageResponseUtil.createSuccessResponse(updatedSentence, "更新例句成功");
        } catch (RuntimeException e) {
            return PageResponseUtil.createErrorResponse("更新例句失败: " + e.getMessage());
        }
    }
    
    // 删除句子
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSentence(@PathVariable Long id) {
        try {
            sentenceService.deleteSentence(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 搜索句子
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchSentences(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<SentenceDTO> sentencePage = sentenceService.searchSentencesWithPagination(keyword, pageable);

            return PageResponseUtil.createPageResponse(sentencePage, "搜索例句成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("搜索例句失败: " + e.getMessage());
        }
    }
    
    // 根据单词ID搜索句子
    @GetMapping("/word/{wordId}/search")
    public ResponseEntity<List<Sentence>> searchSentencesByWordId(
            @PathVariable Long wordId, @RequestParam(required = false) String keyword) {
        List<Sentence> sentences = sentenceService.searchSentencesByWordId(wordId, keyword);
        return ResponseEntity.ok(sentences);
    }
    
    // 获取句子总数
    @GetMapping("/count")
    public ResponseEntity<Long> getSentenceCount() {
        long count = sentenceService.getSentenceCount();
        return ResponseEntity.ok(count);
    }
    
    // 根据单词ID获取句子总数
    @GetMapping("/word/{wordId}/count")
    public ResponseEntity<Long> getSentenceCountByWordId(@PathVariable Long wordId) {
        long count = sentenceService.getSentenceCountByWordId(wordId);
        return ResponseEntity.ok(count);
    }
}
