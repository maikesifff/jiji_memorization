package com.jiji.service;

import com.jiji.entity.Word;
import com.jiji.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    // 分页获取所有单词
    public Page<Word> getAllWordsWithPagination(Pageable pageable) {
        return wordRepository.findAll(pageable);
    }

    // 分页搜索单词
    public Page<Word> searchWordsWithPagination(String keyword, Pageable pageable) {
        return wordRepository.findByWordTextContainingIgnoreCase(keyword, pageable);
    }

    // 获取所有单词
    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }

    // 根据ID获取单词
    public Optional<Word> getWordById(Long id) {
        return wordRepository.findById(id);
    }

    // 根据单词文本获取单词
    public Optional<Word> getWordByText(String wordText) {
        return wordRepository.findByWordText(wordText);
    }

    // 创建新单词
    public Word createWord(Word word) {
        if (wordRepository.existsByWordText(word.getWordText())) {
            throw new RuntimeException("单词已存在: " + word.getWordText());
        }
        
        word.setCreatedAt(LocalDateTime.now());
        word.setUpdatedAt(LocalDateTime.now());
        return wordRepository.save(word);
    }

    // 更新单词
    public Word updateWord(Long id, Word wordDetails) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("单词不存在，ID: " + id));
        
        // 检查新单词名是否与其他单词重复
        if (!word.getWordText().equals(wordDetails.getWordText()) && 
            wordRepository.existsByWordText(wordDetails.getWordText())) {
            throw new RuntimeException("单词名已存在: " + wordDetails.getWordText());
        }
        
        word.setWordText(wordDetails.getWordText());
        word.setAmericanPhonetic(wordDetails.getAmericanPhonetic());
        word.setBritishPhonetic(wordDetails.getBritishPhonetic());
        word.setPronUk(wordDetails.getPronUk());
        word.setPronUs(wordDetails.getPronUs());
        word.setUpdatedAt(LocalDateTime.now());
        
        return wordRepository.save(word);
    }

    // 删除单词
    public void deleteWord(Long id) {
        if (!wordRepository.existsById(id)) {
            throw new RuntimeException("单词不存在，ID: " + id);
        }
        wordRepository.deleteById(id);
    }

    // 根据关键词搜索单词
    public List<Word> searchWords(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllWords();
        }
        return wordRepository.findByKeyword(keyword.trim());
    }

    // 根据发音查找单词
    public List<Word> getWordsByPronUk(String pronUk) {
        return wordRepository.findByPronUk(pronUk);
    }

    // 根据美式发音查找单词
    public List<Word> getWordsByPronUs(String pronUs) {
        return wordRepository.findByPronUs(pronUs);
    }

    // 根据美音音标查找单词
    public List<Word> getWordsByAmericanPhonetic(String americanPhonetic) {
        return wordRepository.findByAmericanPhonetic(americanPhonetic);
    }
    
    // 根据英音音标查找单词
    public List<Word> getWordsByBritishPhonetic(String britishPhonetic) {
        return wordRepository.findByBritishPhonetic(britishPhonetic);
    }

    // 检查单词是否存在
    public boolean existsByWordText(String wordText) {
        return wordRepository.existsByWordText(wordText);
    }

    // 根据时间范围查找单词
    public List<Word> getWordsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return wordRepository.findByCreatedAtBetween(start, end);
    }

    // 获取单词总数
    public long getWordCount() {
        return wordRepository.count();
    }
}
