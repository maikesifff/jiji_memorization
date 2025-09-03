package com.jiji.controller;

import com.jiji.entity.Textbook;
import com.jiji.service.TextbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.jiji.util.PageResponseUtil;

@RestController
@RequestMapping("/api/textbooks")
public class TextbookController {
    
    @Autowired
    private TextbookService textbookService;
    
    // 分页获取所有教材
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTextbooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Textbook> textbookPage = textbookService.getAllTextbooksWithPagination(pageable);

            return PageResponseUtil.createPageResponse(textbookPage, "获取教材列表成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取教材列表失败: " + e.getMessage());
        }
    }
    
    // 分页搜索教材
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTextbooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Textbook> textbookPage = textbookService.searchTextbooksWithPagination(keyword, pageable);

            return PageResponseUtil.createPageResponse(textbookPage, "搜索教材成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("搜索教材失败: " + e.getMessage());
        }
    }
    
    // 获取所有教材（用于下拉选择，不分页）
    @GetMapping("/all")
    public ResponseEntity<List<Textbook>> getAllTextbooksForSelect() {
        List<Textbook> textbooks = textbookService.getAllTextbooks();
        return ResponseEntity.ok(textbooks);
    }
    
    // 根据ID获取教材
    @GetMapping("/{id}")
    public ResponseEntity<Textbook> getTextbookById(@PathVariable Long id) {
        return textbookService.getTextbookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据名称获取教材
    @GetMapping("/name/{name}")
    public ResponseEntity<Textbook> getTextbookByName(@PathVariable String name) {
        Textbook textbook = textbookService.getTextbookByName(name);
        if (textbook != null) {
            return ResponseEntity.ok(textbook);
        }
        return ResponseEntity.notFound().build();
    }
    
    // 根据年级获取教材
    @GetMapping("/grade/{grade}")
    public ResponseEntity<List<Textbook>> getTextbooksByGrade(@PathVariable String grade) {
        List<Textbook> textbooks = textbookService.getTextbooksByGrade(grade);
        return ResponseEntity.ok(textbooks);
    }
    
    // 根据出版社获取教材
    @GetMapping("/publisher/{publisher}")
    public ResponseEntity<List<Textbook>> getTextbooksByPublisher(@PathVariable String publisher) {
        List<Textbook> textbooks = textbookService.getTextbooksByPublisher(publisher);
        return ResponseEntity.ok(textbooks);
    }
    
    // 创建教材
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTextbook(@RequestBody Textbook textbook) {
        try {
            // 检查教材名称是否已存在
            if (textbookService.existsByName(textbook.getName())) {
                return PageResponseUtil.createErrorResponse("教材名称已存在: " + textbook.getName());
            }
            Textbook createdTextbook = textbookService.createTextbook(textbook);
            return PageResponseUtil.createSuccessResponse(createdTextbook, "创建教材成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("创建教材失败: " + e.getMessage());
        }
    }
    
    // 更新教材
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTextbook(@PathVariable Long id, @RequestBody Textbook textbookDetails) {
        try {
            Textbook updatedTextbook = textbookService.updateTextbook(id, textbookDetails);
            if (updatedTextbook != null) {
                return PageResponseUtil.createSuccessResponse(updatedTextbook, "更新教材成功");
            }
            return PageResponseUtil.createErrorResponse("教材不存在");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("更新教材失败: " + e.getMessage());
        }
    }
    
    // 删除教材
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTextbook(@PathVariable Long id) {
        if (textbookService.deleteTextbook(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
