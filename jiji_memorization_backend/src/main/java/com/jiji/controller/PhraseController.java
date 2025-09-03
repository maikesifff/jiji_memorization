package com.jiji.controller;

import com.jiji.entity.Phrase;
import com.jiji.dto.PhraseDTO;
import com.jiji.service.PhraseService;
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
@RequestMapping("/api/phrases")
@CrossOrigin(origins = "*")
public class PhraseController {
    
    @Autowired
    private PhraseService phraseService;
    
    // 分页获取所有短语
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPhrases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            // 使用新的方法，返回包含单词信息的DTO
            Page<PhraseDTO> phrasePage = phraseService.getAllPhrasesWithWordInfo(pageable);
            
            return PageResponseUtil.createPageResponse(phrasePage, "获取短语列表成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取短语列表失败: " + e.getMessage());
        }
    }
    
    // 根据ID获取短语
    @GetMapping("/{id}")
    public ResponseEntity<Phrase> getPhraseById(@PathVariable Long id) {
        return phraseService.getPhraseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据单词ID获取短语
    @GetMapping("/word/{wordId}")
    public ResponseEntity<List<Phrase>> getPhrasesByWordId(@PathVariable Long wordId) {
        List<Phrase> phrases = phraseService.getPhrasesByWordId(wordId);
        return ResponseEntity.ok(phrases);
    }
    
    // 根据短语内容获取短语
    @GetMapping("/content/{phraseContent}")
    public ResponseEntity<List<Phrase>> getPhrasesByContent(@PathVariable String phraseContent) {
        List<Phrase> phrases = phraseService.getPhrasesByContent(phraseContent);
        return ResponseEntity.ok(phrases);
    }
    
    // 根据翻译获取短语
    @GetMapping("/translation/{translation}")
    public ResponseEntity<List<Phrase>> getPhrasesByTranslation(@PathVariable String translation) {
        List<Phrase> phrases = phraseService.getPhrasesByTranslation(translation);
        return ResponseEntity.ok(phrases);
    }
    
    // 创建新短语
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPhrase(@RequestBody Phrase phrase) {
        try {
            Phrase createdPhrase = phraseService.createPhrase(phrase);
            return PageResponseUtil.createSuccessResponse(createdPhrase, "创建短语成功");
        } catch (RuntimeException e) {
            return PageResponseUtil.createErrorResponse("创建短语失败: " + e.getMessage());
        }
    }
    
    // 更新短语
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePhrase(@PathVariable Long id, @RequestBody Phrase phraseDetails) {
        try {
            Phrase updatedPhrase = phraseService.updatePhrase(id, phraseDetails);
            return PageResponseUtil.createSuccessResponse(updatedPhrase, "更新短语成功");
        } catch (RuntimeException e) {
            return PageResponseUtil.createErrorResponse("更新短语失败: " + e.getMessage());
        }
    }
    
    // 删除短语
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhrase(@PathVariable Long id) {
        try {
            phraseService.deletePhrase(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 搜索短语
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPhrases(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<PhraseDTO> phrasePage = phraseService.searchPhrasesWithPagination(keyword, pageable);

            return PageResponseUtil.createPageResponse(phrasePage, "搜索短语成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("搜索短语失败: " + e.getMessage());
        }
    }
    
    // 根据单词ID搜索短语
    @GetMapping("/word/{wordId}/search")
    public ResponseEntity<List<Phrase>> searchPhrasesByWordId(
            @PathVariable Long wordId, @RequestParam(required = false) String keyword) {
        List<Phrase> phrases = phraseService.searchPhrasesByWordId(wordId, keyword);
        return ResponseEntity.ok(phrases);
    }
    
    // 获取短语总数
    @GetMapping("/count")
    public ResponseEntity<Long> getPhraseCount() {
        long count = phraseService.getPhraseCount();
        return ResponseEntity.ok(count);
    }
    
    // 根据单词ID获取短语总数
    @GetMapping("/word/{wordId}/count")
    public ResponseEntity<Long> getPhraseCountByWordId(@PathVariable Long wordId) {
        long count = phraseService.getPhraseCountByWordId(wordId);
        return ResponseEntity.ok(count);
    }
}
