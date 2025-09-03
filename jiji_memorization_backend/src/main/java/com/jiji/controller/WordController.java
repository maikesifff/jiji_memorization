package com.jiji.controller;

import com.jiji.entity.Word;
import com.jiji.service.WordService;
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
@RequestMapping("/api/words")
@CrossOrigin(origins = "*")
public class WordController {

    @Autowired
    private WordService wordService;

    // 分页获取所有单词
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllWords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<Word> wordPage = wordService.getAllWordsWithPagination(pageable);
            
            return PageResponseUtil.createPageResponse(wordPage, "获取单词列表成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取单词列表失败: " + e.getMessage());
        }
    }

    // 分页搜索单词
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchWords(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<Word> wordPage = wordService.searchWordsWithPagination(keyword, pageable);
            
            return PageResponseUtil.createPageResponse(wordPage, "搜索单词成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("搜索单词失败: " + e.getMessage());
        }
    }

    // 根据ID获取单词
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getWordById(@PathVariable Long id) {
        try {
            var word = wordService.getWordById(id);
            if (word.isPresent()) {
                return PageResponseUtil.createSuccessResponse(word.get(), "获取单词成功");
            } else {
                return PageResponseUtil.createErrorResponse("单词不存在");
            }
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取单词失败: " + e.getMessage());
        }
    }

    // 根据单词文本获取单词
    @GetMapping("/text/{wordText}")
    public ResponseEntity<Map<String, Object>> getWordByText(@PathVariable String wordText) {
        try {
            var word = wordService.getWordByText(wordText);
            if (word.isPresent()) {
                return PageResponseUtil.createSuccessResponse(word.get(), "获取单词成功");
            } else {
                return PageResponseUtil.createErrorResponse("单词不存在");
            }
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取单词失败: " + e.getMessage());
        }
    }

    // 创建新单词
    @PostMapping
    public ResponseEntity<Map<String, Object>> createWord(@RequestBody Word word) {
        try {
            if (wordService.existsByWordText(word.getWordText())) {
                return PageResponseUtil.createErrorResponse("单词已存在: " + word.getWordText());
            }
            Word createdWord = wordService.createWord(word);
            return PageResponseUtil.createSuccessResponse(createdWord, "创建单词成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("创建单词失败: " + e.getMessage());
        }
    }

    // 更新单词
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateWord(@PathVariable Long id, @RequestBody Word wordDetails) {
        try {
            Word updatedWord = wordService.updateWord(id, wordDetails);
            return PageResponseUtil.createSuccessResponse(updatedWord, "更新单词成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("更新单词失败: " + e.getMessage());
        }
    }

    // 删除单词
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteWord(@PathVariable Long id) {
        try {
            wordService.deleteWord(id);
            return PageResponseUtil.createSuccessResponse(null, "删除单词成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("删除单词失败: " + e.getMessage());
        }
    }

    // 根据音标查找单词
    @GetMapping("/american-phonetic/{americanPhonetic}")
    public ResponseEntity<Map<String, Object>> getWordsByAmericanPhonetic(@PathVariable String americanPhonetic) {
        try {
            var words = wordService.getWordsByAmericanPhonetic(americanPhonetic);
            return PageResponseUtil.createSuccessResponse(words, "获取单词成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取单词失败: " + e.getMessage());
        }
    }

    // 根据英音音标查找单词
    @GetMapping("/british-phonetic/{britishPhonetic}")
    public ResponseEntity<Map<String, Object>> getWordsByBritishPhonetic(@PathVariable String britishPhonetic) {
        try {
            var words = wordService.getWordsByBritishPhonetic(britishPhonetic);
            return PageResponseUtil.createSuccessResponse(words, "获取单词成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取单词失败: " + e.getMessage());
        }
    }

    // 根据英式发音查找单词
    @GetMapping("/pron-uk/{pronUk}")
    public ResponseEntity<Map<String, Object>> getWordsByPronUk(@PathVariable String pronUk) {
        try {
            var words = wordService.getWordsByPronUk(pronUk);
            return PageResponseUtil.createSuccessResponse(words, "获取单词成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取单词失败: " + e.getMessage());
        }
    }

    // 根据美式发音查找单词
    @GetMapping("/pron-us/{pronUs}")
    public ResponseEntity<Map<String, Object>> getWordsByPronUs(@PathVariable String pronUs) {
        try {
            var words = wordService.getWordsByPronUs(pronUs);
            return PageResponseUtil.createSuccessResponse(words, "获取单词成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取单词失败: " + e.getMessage());
        }
    }

    // 获取单词总数
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getWordCount() {
        try {
            long count = wordService.getWordCount();
            return PageResponseUtil.createCountResponse(count);
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取单词总数失败: " + e.getMessage());
        }
    }
}
