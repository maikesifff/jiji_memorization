package com.jiji.controller;

import com.jiji.entity.Meaning;
import com.jiji.service.MeaningService;
import com.jiji.util.PartOfSpeechValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jiji.dto.MeaningDTO;
import com.jiji.util.PageResponseUtil;

@RestController
@RequestMapping("/api/meanings")
@CrossOrigin(origins = "*")
public class MeaningController {
    
    @Autowired
    private MeaningService meaningService;
    
    // 分页获取所有释义
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMeanings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            // 使用新的方法，返回包含单词信息的DTO
            Page<MeaningDTO> meaningPage = meaningService.getAllMeaningsWithWordInfo(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", meaningPage.getContent());
            response.put("currentPage", meaningPage.getNumber());
            response.put("totalItems", meaningPage.getTotalElements());
            response.put("totalPages", meaningPage.getTotalPages());
            response.put("size", meaningPage.getSize());
            response.put("hasNext", meaningPage.hasNext());
            response.put("hasPrevious", meaningPage.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取释义列表失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 根据ID获取释义
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMeaningById(@PathVariable Long id) {
        try {
            var meaning = meaningService.findById(id);
            if (meaning.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("data", meaning.get());
                response.put("message", "获取释义成功");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "释义不存在");
                errorResponse.put("status", "error");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取释义失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 根据单词ID获取释义
    @GetMapping("/word/{wordId}")
    public ResponseEntity<Map<String, Object>> getMeaningsByWordId(@PathVariable Long wordId) {
        try {
            List<Meaning> meanings = meaningService.findByWordId(wordId);
            Map<String, Object> response = new HashMap<>();
            response.put("data", meanings);
            response.put("message", "获取释义成功");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取释义失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 根据词性获取释义
    @GetMapping("/pos/{pos}")
    public ResponseEntity<Map<String, Object>> getMeaningsByPos(@PathVariable String pos) {
        try {
            List<Meaning> meanings = meaningService.findByPos(pos);
            Map<String, Object> response = new HashMap<>();
            response.put("data", meanings);
            response.put("message", "获取释义成功");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取释义失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 根据单词ID和词性获取释义
    @GetMapping("/word/{wordId}/pos/{pos}")
    public ResponseEntity<Map<String, Object>> getMeaningsByWordIdAndPos(
            @PathVariable Long wordId, @PathVariable String pos) {
        try {
            List<Meaning> meanings = meaningService.findByWordIdAndPos(wordId, pos);
            Map<String, Object> response = new HashMap<>();
            response.put("data", meanings);
            response.put("message", "获取释义成功");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取释义失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 创建新释义
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMeaning(@RequestBody Meaning meaning) {
        try {
            Meaning createdMeaning = meaningService.createMeaning(meaning);
            Map<String, Object> response = new HashMap<>();
            response.put("data", createdMeaning);
            response.put("message", "创建释义成功");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "创建释义失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 更新释义
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMeaning(@PathVariable Long id, @RequestBody Meaning meaningDetails) {
        try {
            Meaning updatedMeaning = meaningService.updateMeaning(id, meaningDetails);
            if (updatedMeaning != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("data", updatedMeaning);
                response.put("message", "更新释义成功");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "释义不存在");
                errorResponse.put("status", "error");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "更新释义失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 删除释义
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMeaning(@PathVariable Long id) {
        try {
            boolean deleted = meaningService.deleteMeaning(id);
            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "删除释义成功");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "释义不存在");
                errorResponse.put("status", "error");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "删除释义失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 分页搜索释义
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMeanings(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<MeaningDTO> meaningPage = meaningService.searchMeaningsWithWordInfo(keyword, pageable);
            
            return PageResponseUtil.createPageResponse(meaningPage, "搜索释义成功");
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "搜索释义失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 获取释义总数
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getMeaningCount() {
        try {
            long count = meaningService.getMeaningCount();
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取释义总数失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 根据单词ID获取释义总数
    @GetMapping("/word/{wordId}/count")
    public ResponseEntity<Map<String, Object>> getMeaningCountByWordId(@PathVariable Long wordId) {
        try {
            List<Meaning> meanings = meaningService.findByWordId(wordId);
            long count = meanings.size();
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取释义总数失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 获取所有有效的词性类型
    @GetMapping("/pos-types")
    public ResponseEntity<Map<String, Object>> getValidPosTypes() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("data", PartOfSpeechValidator.getPosChineseMap());
            response.put("message", "获取词性类型成功");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取词性类型失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
