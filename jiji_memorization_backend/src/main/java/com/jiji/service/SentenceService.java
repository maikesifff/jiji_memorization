package com.jiji.service;

import com.jiji.entity.Sentence;
import com.jiji.dto.SentenceDTO;
import com.jiji.repository.SentenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SentenceService {
    
    @Autowired
    private SentenceRepository sentenceRepository;
    
    // 获取所有句子
    public List<Sentence> getAllSentences() {
        return sentenceRepository.findAll();
    }
    
    // 分页获取所有句子
    public Page<Sentence> getAllSentencesWithPagination(Pageable pageable) {
        return sentenceRepository.findAll(pageable);
    }
    
    // 分页获取所有句子，包含单词信息
    public Page<SentenceDTO> getAllSentencesWithWordInfo(Pageable pageable) {
        return sentenceRepository.findAllWithWordInfo(pageable);
    }
    
    // 根据ID获取句子
    public Optional<Sentence> getSentenceById(Long id) {
        return sentenceRepository.findById(id);
    }
    
    // 根据单词ID获取句子
    public List<Sentence> getSentencesByWordId(Long wordId) {
        return sentenceRepository.findByWordId(wordId);
    }
    
    // 根据句子内容获取句子
    public List<Sentence> getSentencesByContent(String sentenceText) {
        return sentenceRepository.findBySentenceText(sentenceText);
    }
    
    // 根据翻译获取句子
    public List<Sentence> getSentencesByTranslation(String translation) {
        return sentenceRepository.findByTranslation(translation);
    }
    
    // 创建新句子
    public Sentence createSentence(Sentence sentence) {
        sentence.setCreatedAt(LocalDateTime.now());
        sentence.setUpdatedAt(LocalDateTime.now());
        return sentenceRepository.save(sentence);
    }
    
    // 更新句子
    public Sentence updateSentence(Long id, Sentence sentenceDetails) {
        Sentence sentence = sentenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("句子不存在，ID: " + id));
        
        sentence.setWordId(sentenceDetails.getWordId());
        sentence.setSentenceText(sentenceDetails.getSentenceText());
        sentence.setTranslation(sentenceDetails.getTranslation());
        sentence.setUpdatedAt(LocalDateTime.now());
        
        return sentenceRepository.save(sentence);
    }
    
    // 删除句子
    public void deleteSentence(Long id) {
        if (!sentenceRepository.existsById(id)) {
            throw new RuntimeException("句子不存在，ID: " + id);
        }
        sentenceRepository.deleteById(id);
    }
    
    // 根据关键词搜索句子
    public List<Sentence> searchSentences(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSentences();
        }
        return sentenceRepository.findByKeyword(keyword.trim());
    }
    
    // 分页搜索句子，包含单词信息
    public Page<SentenceDTO> searchSentencesWithPagination(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSentencesWithWordInfo(pageable);
        }
        return sentenceRepository.searchSentencesWithWordInfo(keyword.trim(), pageable);
    }
    
    // 根据单词ID和关键词搜索句子
    public List<Sentence> searchSentencesByWordId(Long wordId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getSentencesByWordId(wordId);
        }
        return sentenceRepository.findByWordIdAndKeyword(wordId, keyword.trim());
    }
    
    // 获取句子总数
    public long getSentenceCount() {
        return sentenceRepository.count();
    }
    
    // 根据单词ID获取句子总数
    public long getSentenceCountByWordId(Long wordId) {
        return sentenceRepository.findByWordId(wordId).size();
    }
}
