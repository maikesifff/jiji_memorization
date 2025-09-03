package com.jiji.service;

import com.jiji.entity.Unit;
import com.jiji.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnitService {
    
    @Autowired
    private UnitRepository unitRepository;
    
    // 获取所有单元
    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }
    
    // 分页获取所有单元
    public Page<Unit> getAllUnitsWithPagination(Pageable pageable) {
        return unitRepository.findAll(pageable);
    }
    
    // 分页搜索单元
    public Page<Unit> searchUnitsWithPagination(String keyword, Pageable pageable) {
        return unitRepository.searchUnits(keyword, pageable);
    }
    
    // 根据ID获取单元
    public Optional<Unit> getUnitById(Long id) {
        return unitRepository.findById(id);
    }
    
    // 根据教材ID获取单元
    public List<Unit> getUnitsByTextbookId(Long textbookId) {
        return unitRepository.findByTextbookIdOrderByCreatedAtAsc(textbookId);
    }
    
    // 创建单元
    public Unit createUnit(Unit unit) {
        // 检查同一教材内是否有重复的单元名
        if (unitRepository.existsByTextbookIdAndName(unit.getTextbookId(), unit.getName())) {
            throw new RuntimeException("该教材内已存在同名单元: " + unit.getName());
        }
        return unitRepository.save(unit);
    }
    
    // 更新单元
    public Unit updateUnit(Long id, Unit unitDetails) {
        Optional<Unit> unitOpt = unitRepository.findById(id);
        if (unitOpt.isPresent()) {
            Unit unit = unitOpt.get();
            
            // 检查更新后的名称是否在同一教材内重复（排除当前单元）
            if (!unit.getName().equals(unitDetails.getName()) || 
                !unit.getTextbookId().equals(unitDetails.getTextbookId())) {
                // 名称或教材发生变化时，检查是否有重复
                if (unitRepository.existsByTextbookIdAndName(unitDetails.getTextbookId(), unitDetails.getName())) {
                    throw new RuntimeException("该教材内已存在同名单元: " + unitDetails.getName());
                }
            }
            
            unit.setName(unitDetails.getName());
            unit.setTextbookId(unitDetails.getTextbookId());
            unit.setDescription(unitDetails.getDescription());
            return unitRepository.save(unit);
        }
        return null;
    }
    
    // 删除单元
    public boolean deleteUnit(Long id) {
        if (unitRepository.existsById(id)) {
            unitRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
