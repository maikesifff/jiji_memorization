package com.jiji.service;

import com.jiji.entity.Unit;
import com.jiji.repository.UnitRepository;
import com.jiji.repository.UnitWordRepository;
import com.jiji.repository.ErrorRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UnitService {
    
    @Autowired
    private UnitRepository unitRepository;
    
    @Autowired
    private UnitWordRepository unitWordRepository;
    
    @Autowired
    private ErrorRecordRepository errorRecordRepository;
    
    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }
    
    public Unit getUnitById(Long id) {
        Optional<Unit> unit = unitRepository.findById(id);
        return unit.orElse(null);
    }
    
    public List<Unit> getUnitsByTextbook(Long textbookId) {
        return unitRepository.findByTextbookId(textbookId);
    }
    
    public Map<String, Object> getUnitProgress(Long unitId, Long userId) {
        Map<String, Object> progress = new HashMap<>();
        
        // 获取单元总单词数
        Long totalWords = unitWordRepository.countByUnitId(unitId);
        
        // 获取用户已学习的单词数（在错误记录表中的记录数）
        Long learnedWords = errorRecordRepository.countByUnitIdAndUserId(unitId, userId);
        
        progress.put("totalWords", totalWords);
        progress.put("learnedWords", learnedWords);
        
        return progress;
    }
    
    public Unit createUnit(Unit unit) {
        return unitRepository.save(unit);
    }
    
    public Unit updateUnit(Unit unit) {
        if (unitRepository.existsById(unit.getId())) {
            return unitRepository.save(unit);
        }
        return null;
    }
    
    public boolean deleteUnit(Long id) {
        if (unitRepository.existsById(id)) {
            unitRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
