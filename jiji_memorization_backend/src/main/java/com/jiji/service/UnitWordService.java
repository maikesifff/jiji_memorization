package com.jiji.service;

import com.jiji.entity.UnitWord;
import com.jiji.dto.UnitWordDTO;
import com.jiji.repository.UnitWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UnitWordService {
    
    @Autowired
    private UnitWordRepository unitWordRepository;
    
    // 获取所有单元单词关联
    public List<UnitWord> getAllUnitWords() {
        return unitWordRepository.findAll();
    }
    
    // 分页获取所有单元单词关联
    public Page<UnitWord> getAllUnitWordsWithPagination(Pageable pageable) {
        return unitWordRepository.findAll(pageable);
    }
    
    // 分页获取所有单元单词关联（包含详细信息）
    public Page<UnitWordDTO> getAllUnitWordsWithDetails(Pageable pageable) {
        return unitWordRepository.findAllUnitWordsWithDetails(pageable);
    }
    
    // 分页搜索单元单词关联
    public Page<UnitWordDTO> searchUnitWordsWithPagination(String keyword, Pageable pageable) {
        return unitWordRepository.searchUnitWords(keyword, pageable);
    }
    
    // 根据单元ID分页搜索单元单词关联
    public Page<UnitWordDTO> searchUnitWordsByUnitIdWithPagination(Long unitId, String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getUnitWordsByUnitIdWithDetails(unitId, pageable);
        }
        // 使用新的查询方法，返回UnitWordDTO
        return unitWordRepository.searchUnitWordsByUnitIdWithDetails(unitId, keyword.trim(), pageable);
    }
    
    // 根据ID获取单元单词关联
    public Optional<UnitWord> getUnitWordById(Long id) {
        return unitWordRepository.findById(id);
    }
    
    // 根据单元ID获取关联的单词
    public List<UnitWord> getUnitWordsByUnitId(Long unitId) {
        return unitWordRepository.findByUnitId(unitId);
    }
    
    // 根据单元ID分页获取关联的单词
    public Page<UnitWord> getUnitWordsByUnitIdWithPagination(Long unitId, Pageable pageable) {
        return unitWordRepository.findByUnitId(unitId, pageable);
    }
    
    // 根据单元ID分页获取关联的单词（包含详细信息）
    public Page<UnitWordDTO> getUnitWordsByUnitIdWithDetails(Long unitId, Pageable pageable) {
        return unitWordRepository.findUnitWordsByUnitIdWithDetails(unitId, pageable);
    }
    
    // 根据单词ID获取关联的单元
    public List<UnitWord> getUnitWordsByWordId(Long wordId) {
        return unitWordRepository.findByWordId(wordId);
    }
    
    // 检查单元和单词是否已关联
    public boolean existsByUnitIdAndWordId(Long unitId, Long wordId) {
        return unitWordRepository.existsByUnitIdAndWordId(unitId, wordId);
    }
    
    // 创建新的单元单词关联
    public UnitWord createUnitWord(UnitWord unitWord) {
        // 检查是否已存在相同的关联
        if (unitWordRepository.existsByUnitIdAndWordId(unitWord.getUnitId(), unitWord.getWordId())) {
            throw new RuntimeException("该单元和单词的关联已存在");
        }
        
        unitWord.setCreatedAt(LocalDateTime.now());
        return unitWordRepository.save(unitWord);
    }
    
    // 更新单元单词关联
    public UnitWord updateUnitWord(Long id, UnitWord unitWordDetails) {
        UnitWord unitWord = unitWordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("单元单词关联不存在，ID: " + id));
        
        // 检查新的关联是否与其他记录重复
        if (!(unitWord.getUnitId().equals(unitWordDetails.getUnitId()) && 
              unitWord.getWordId().equals(unitWordDetails.getWordId())) &&
            unitWordRepository.existsByUnitIdAndWordId(unitWordDetails.getUnitId(), unitWordDetails.getWordId())) {
            throw new RuntimeException("该单元和单词的关联已存在");
        }
        
        unitWord.setUnitId(unitWordDetails.getUnitId());
        unitWord.setWordId(unitWordDetails.getWordId());
        
        return unitWordRepository.save(unitWord);
    }
    
    // 删除单元单词关联
    public void deleteUnitWord(Long id) {
        if (!unitWordRepository.existsById(id)) {
            throw new RuntimeException("单元单词关联不存在，ID: " + id);
        }
        unitWordRepository.deleteById(id);
    }
    
    // 根据单元ID删除所有关联
    public void deleteUnitWordsByUnitId(Long unitId) {
        List<UnitWord> unitWords = unitWordRepository.findByUnitId(unitId);
        unitWordRepository.deleteAll(unitWords);
    }
    
    // 根据单词ID删除所有关联
    public void deleteUnitWordsByWordId(Long wordId) {
        List<UnitWord> unitWords = unitWordRepository.findByWordId(wordId);
        unitWordRepository.deleteAll(unitWords);
    }
    
    // 获取单元单词关联总数
    public long getUnitWordCount() {
        return unitWordRepository.count();
    }
    
    // 根据单元ID获取关联单词总数
    public long getUnitWordCountByUnitId(Long unitId) {
        return unitWordRepository.countByUnitId(unitId);
    }
    
    // 根据单词ID获取关联单元总数
    public long getUnitWordCountByWordId(Long wordId) {
        return unitWordRepository.findByWordId(wordId).size();
    }
}
