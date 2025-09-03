package com.jiji.controller;

import com.jiji.entity.UnitWord;
import com.jiji.dto.UnitWordDTO;
import com.jiji.service.UnitWordService;
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
import java.util.HashMap;

@RestController
@RequestMapping("/api/unit-words")
@CrossOrigin(origins = "*")
public class UnitWordController {
    
    @Autowired
    private UnitWordService unitWordService;
    
    // 测试端点 - 获取总数
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        try {
            long count = unitWordService.getUnitWordCount();
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("message", "测试成功");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "测试失败: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // 分页获取所有单元单词关联
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUnitWords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<UnitWordDTO> unitWordPage = unitWordService.getAllUnitWordsWithDetails(pageable);
            
            return PageResponseUtil.createPageResponse(unitWordPage, "获取单元单词关联列表成功");
        } catch (Exception e) {
            e.printStackTrace(); // 添加详细的错误堆栈
            return PageResponseUtil.createErrorResponse("获取单元单词关联列表失败: " + e.getMessage());
        }
    }
    
    // 分页搜索单元单词关联
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUnitWords(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<UnitWordDTO> unitWordPage = unitWordService.searchUnitWordsWithPagination(keyword, pageable);
            
            return PageResponseUtil.createPageResponse(unitWordPage, "搜索单元单词关联成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("搜索单元单词关联失败: " + e.getMessage());
        }
    }
    
    // 根据ID获取单元单词关联
    @GetMapping("/{id}")
    public ResponseEntity<UnitWord> getUnitWordById(@PathVariable Long id) {
        return unitWordService.getUnitWordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据单元ID分页获取关联的单词
    @GetMapping("/unit/{unitId}")
    public ResponseEntity<Map<String, Object>> getUnitWordsByUnitId(
            @PathVariable Long unitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<UnitWordDTO> unitWordPage = unitWordService.getUnitWordsByUnitIdWithDetails(unitId, pageable);
            
            return PageResponseUtil.createPageResponse(unitWordPage, "获取单元单词关联列表成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取单元单词关联列表失败: " + e.getMessage());
        }
    }
    
    // 根据单元ID分页搜索单元单词关联
    @GetMapping("/unit/{unitId}/search")
    public ResponseEntity<Map<String, Object>> searchUnitWordsByUnitId(
            @PathVariable Long unitId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<UnitWordDTO> unitWordPage = unitWordService.searchUnitWordsByUnitIdWithPagination(unitId, keyword, pageable);
            
            return PageResponseUtil.createPageResponse(unitWordPage, "搜索单元单词关联成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("搜索单元单词关联失败: " + e.getMessage());
        }
    }
    
    // 根据单词ID获取关联的单元
    @GetMapping("/word/{wordId}")
    public ResponseEntity<List<UnitWord>> getUnitWordsByWordId(@PathVariable Long wordId) {
        List<UnitWord> unitWords = unitWordService.getUnitWordsByWordId(wordId);
        return ResponseEntity.ok(unitWords);
    }
    
    // 检查单元和单词是否已关联
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkUnitWordExists(
            @RequestParam Long unitId, @RequestParam Long wordId) {
        boolean exists = unitWordService.existsByUnitIdAndWordId(unitId, wordId);
        return ResponseEntity.ok(exists);
    }
    
    // 创建新的单元单词关联
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUnitWord(@RequestBody UnitWord unitWord) {
        try {
            UnitWord createdUnitWord = unitWordService.createUnitWord(unitWord);
            return PageResponseUtil.createSuccessResponse(createdUnitWord, "创建单元单词关联成功");
        } catch (RuntimeException e) {
            return PageResponseUtil.createErrorResponse("创建单元单词关联失败: " + e.getMessage());
        }
    }
    
    // 更新单元单词关联
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUnitWord(@PathVariable Long id, @RequestBody UnitWord unitWordDetails) {
        try {
            UnitWord updatedUnitWord = unitWordService.updateUnitWord(id, unitWordDetails);
            return PageResponseUtil.createSuccessResponse(updatedUnitWord, "更新单元单词关联成功");
        } catch (RuntimeException e) {
            return PageResponseUtil.createErrorResponse("更新单元单词关联失败: " + e.getMessage());
        }
    }
    
    // 删除单元单词关联
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnitWord(@PathVariable Long id) {
        try {
            unitWordService.deleteUnitWord(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据单元ID删除所有关联
    @DeleteMapping("/unit/{unitId}")
    public ResponseEntity<Void> deleteUnitWordsByUnitId(@PathVariable Long unitId) {
        try {
            unitWordService.deleteUnitWordsByUnitId(unitId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据单词ID删除所有关联
    @DeleteMapping("/word/{wordId}")
    public ResponseEntity<Void> deleteUnitWordsByWordId(@PathVariable Long wordId) {
        try {
            unitWordService.deleteUnitWordsByWordId(wordId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 获取单元单词关联总数
    @GetMapping("/count")
    public ResponseEntity<Long> getUnitWordCount() {
        long count = unitWordService.getUnitWordCount();
        return ResponseEntity.ok(count);
    }
    
    // 根据单元ID获取关联单词总数
    @GetMapping("/unit/{unitId}/count")
    public ResponseEntity<Long> getUnitWordCountByUnitId(@PathVariable Long unitId) {
        long count = unitWordService.getUnitWordCountByUnitId(unitId);
        return ResponseEntity.ok(count);
    }
    
    // 根据单词ID获取关联单元总数
    @GetMapping("/word/{wordId}/count")
    public ResponseEntity<Long> getUnitWordCountByWordId(@PathVariable Long wordId) {
        long count = unitWordService.getUnitWordCountByWordId(wordId);
        return ResponseEntity.ok(count);
    }
}
