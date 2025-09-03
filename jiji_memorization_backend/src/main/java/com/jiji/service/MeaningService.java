package com.jiji.service;

import com.jiji.entity.Meaning;
import com.jiji.dto.MeaningDTO;
import com.jiji.repository.MeaningRepository;
import com.jiji.util.PartOfSpeechValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeaningService {

    @Autowired
    private MeaningRepository meaningRepository;

    // 分页获取所有释义
    public Page<Meaning> getAllMeaningsWithPagination(Pageable pageable) {
        return meaningRepository.findAll(pageable);
    }
    
    // 分页获取所有释义，包含单词信息
    public Page<MeaningDTO> getAllMeaningsWithWordInfo(Pageable pageable) {
        return meaningRepository.findAllWithWordInfo(pageable);
    }

    // 分页搜索释义
    public Page<Meaning> searchMeaningsWithPagination(String keyword, Pageable pageable) {
        return meaningRepository.findByContentContainingIgnoreCase(keyword, pageable);
    }
    
    // 分页搜索释义，包含单词信息
    public Page<MeaningDTO> searchMeaningsWithWordInfo(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMeaningsWithWordInfo(pageable);
        }
        return meaningRepository.searchMeaningsWithWordInfo(keyword.trim(), pageable);
    }

    // 创建释义
    public Meaning createMeaning(Meaning meaning) {
        // 验证词性
        if (meaning.getPos() != null && !meaning.getPos().trim().isEmpty()) {
            if (!PartOfSpeechValidator.isValid(meaning.getPos())) {
                throw new RuntimeException("无效的词性类型: " + meaning.getPos() + 
                    "。有效类型包括: " + String.join(", ", PartOfSpeechValidator.getValidPosTypes()));
            }
        }
        return meaningRepository.save(meaning);
    }

    // 根据ID查找释义
    public Optional<Meaning> findById(Long id) {
        return meaningRepository.findById(id);
    }

    // 根据单词ID查找释义
    public List<Meaning> findByWordId(Long wordId) {
        return meaningRepository.findByWordId(wordId);
    }

    // 根据词性查找释义
    public List<Meaning> findByPos(String pos) {
        return meaningRepository.findByPos(pos);
    }

    // 根据单词ID和词性查找释义
    public List<Meaning> findByWordIdAndPos(Long wordId, String pos) {
        return meaningRepository.findByWordIdAndPos(wordId, pos);
    }

    // 根据关键词搜索释义
    public List<Meaning> findByKeyword(String keyword) {
        return meaningRepository.findByKeyword(keyword);
    }

    // 根据单词ID和关键词搜索释义
    public List<Meaning> findByWordIdAndKeyword(Long wordId, String keyword) {
        return meaningRepository.findByWordIdAndKeyword(wordId, keyword);
    }

    // 获取所有释义（不分页，用于小数据集）
    public List<Meaning> getAllMeanings() {
        return meaningRepository.findAll();
    }

    // 搜索释义（不分页，用于小数据集）
    public List<Meaning> searchMeanings(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return meaningRepository.findAll();
        }
        return meaningRepository.findByContentContainingIgnoreCase(keyword);
    }

    // 更新释义
    public Meaning updateMeaning(Long id, Meaning meaningDetails) {
        Optional<Meaning> optionalMeaning = meaningRepository.findById(id);
        if (optionalMeaning.isPresent()) {
            Meaning meaning = optionalMeaning.get();
            meaning.setWordId(meaningDetails.getWordId());
            
            // 验证词性
            if (meaningDetails.getPos() != null && !meaningDetails.getPos().trim().isEmpty()) {
                if (!PartOfSpeechValidator.isValid(meaningDetails.getPos())) {
                    throw new RuntimeException("无效的词性类型: " + meaningDetails.getPos() + 
                        "。有效类型包括: " + String.join(", ", PartOfSpeechValidator.getValidPosTypes()));
                }
            }
            meaning.setPos(meaningDetails.getPos());
            meaning.setContent(meaningDetails.getContent());
            return meaningRepository.save(meaning);
        }
        return null;
    }

    // 删除释义
    public boolean deleteMeaning(Long id) {
        if (meaningRepository.existsById(id)) {
            meaningRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 获取释义总数
    public long getMeaningCount() {
        return meaningRepository.count();
    }
}
