package com.jiji.controller;

import com.jiji.entity.Unit;
import com.jiji.service.UnitService;
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
@RequestMapping("/api/units")
@CrossOrigin(origins = "*")
public class UnitController {
    
    @Autowired
    private UnitService unitService;
    
    // 分页获取所有单元
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUnits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Unit> unitPage = unitService.getAllUnitsWithPagination(pageable);

            return PageResponseUtil.createPageResponse(unitPage, "获取单元列表成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取单元列表失败: " + e.getMessage());
        }
    }
    
    // 分页搜索单元
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUnits(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Unit> unitPage = unitService.searchUnitsWithPagination(keyword, pageable);

            return PageResponseUtil.createPageResponse(unitPage, "搜索单元成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("搜索单元失败: " + e.getMessage());
        }
    }

    // 获取所有单元（用于下拉选择，非分页）
    @GetMapping("/all")
    public ResponseEntity<List<Unit>> getAllUnitsForSelect() {
        try {
            List<Unit> units = unitService.getAllUnits();
            return ResponseEntity.ok(units);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据ID获取单元
    @GetMapping("/{id}")
    public ResponseEntity<Unit> getUnitById(@PathVariable Long id) {
        return unitService.getUnitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据教材ID获取单元
    @GetMapping("/textbook/{textbookId}")
    public ResponseEntity<List<Unit>> getUnitsByTextbookId(@PathVariable Long textbookId) {
        List<Unit> units = unitService.getUnitsByTextbookId(textbookId);
        return ResponseEntity.ok(units);
    }
    
    // 创建单元
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUnit(@RequestBody Unit unit) {
        try {
            Unit createdUnit = unitService.createUnit(unit);
            return PageResponseUtil.createSuccessResponse(createdUnit, "创建单元成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("创建单元失败: " + e.getMessage());
        }
    }
    
    // 更新单元
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUnit(@PathVariable Long id, @RequestBody Unit unitDetails) {
        try {
            Unit updatedUnit = unitService.updateUnit(id, unitDetails);
            if (updatedUnit != null) {
                return PageResponseUtil.createSuccessResponse(updatedUnit, "更新单元成功");
            }
            return PageResponseUtil.createErrorResponse("单元不存在");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("更新单元失败: " + e.getMessage());
        }
    }
    
    // 删除单元
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        if (unitService.deleteUnit(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

