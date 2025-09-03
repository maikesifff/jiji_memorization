package com.jiji.service;

import com.jiji.entity.Phrase;
import com.jiji.dto.PhraseDTO;
import com.jiji.repository.PhraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PhraseService {
    
    @Autowired
    private PhraseRepository phraseRepository;
    
    // 获取所有短语
    public List<Phrase> getAllPhrases() {
        return phraseRepository.findAll();
    }
    
    // 分页获取所有短语
    public Page<Phrase> getAllPhrasesWithPagination(Pageable pageable) {
        return phraseRepository.findAll(pageable);
    }
    
    // 分页获取所有短语，包含单词信息
    public Page<PhraseDTO> getAllPhrasesWithWordInfo(Pageable pageable) {
        return phraseRepository.findAllWithWordInfo(pageable);
    }
    
    // 根据ID获取短语
    public Optional<Phrase> getPhraseById(Long id) {
        return phraseRepository.findById(id);
    }
    
    // 根据单词ID获取短语
    public List<Phrase> getPhrasesByWordId(Long wordId) {
        return phraseRepository.findByWordId(wordId);
    }
    
    // 根据短语内容获取短语
    public List<Phrase> getPhrasesByContent(String phraseText) {
        return phraseRepository.findByPhraseText(phraseText);
    }
    
    // 根据翻译获取短语
    public List<Phrase> getPhrasesByTranslation(String translation) {
        return phraseRepository.findByTranslation(translation);
    }
    
    // 创建新短语
    public Phrase createPhrase(Phrase phrase) {
        phrase.setCreatedAt(LocalDateTime.now());
        phrase.setUpdatedAt(LocalDateTime.now());
        return phraseRepository.save(phrase);
    }
    
    // 更新短语
    public Phrase updatePhrase(Long id, Phrase phraseDetails) {
        Phrase phrase = phraseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("短语不存在，ID: " + id));
        
        phrase.setWordId(phraseDetails.getWordId());
        phrase.setPhraseText(phraseDetails.getPhraseText());
        phrase.setTranslation(phraseDetails.getTranslation());
        phrase.setUpdatedAt(LocalDateTime.now());
        
        return phraseRepository.save(phrase);
    }
    
    // 删除短语
    public void deletePhrase(Long id) {
        if (!phraseRepository.existsById(id)) {
            throw new RuntimeException("短语不存在，ID: " + id);
        }
        phraseRepository.deleteById(id);
    }
    
    // 根据关键词搜索短语
    public List<Phrase> searchPhrases(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPhrases();
        }
        return phraseRepository.findByKeyword(keyword.trim());
    }
    
    // 分页搜索短语，包含单词信息
    public Page<PhraseDTO> searchPhrasesWithPagination(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPhrasesWithWordInfo(pageable);
        }
        return phraseRepository.searchPhrasesWithWordInfo(keyword.trim(), pageable);
    }
    
    // 根据单词ID和关键词搜索短语
    public List<Phrase> searchPhrasesByWordId(Long wordId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getPhrasesByWordId(wordId);
        }
        return phraseRepository.findByWordIdAndKeyword(wordId, keyword.trim());
    }
    
    // 获取短语总数
    public long getPhraseCount() {
        return phraseRepository.count();
    }
    
    // 根据单词ID获取短语总数
    public long getPhraseCountByWordId(Long wordId) {
        return phraseRepository.findByWordId(wordId).size();
    }
}
