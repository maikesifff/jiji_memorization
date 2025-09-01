package com.jiji.controller;

import com.jiji.entity.Unit;
import com.jiji.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/units")
public class UnitController {
    
    @Autowired
    private UnitService unitService;
    
    @GetMapping
    public ResponseEntity<List<Unit>> getAllUnits() {
        List<Unit> units = unitService.getAllUnits();
        return ResponseEntity.ok(units);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Unit> getUnitById(@PathVariable Long id) {
        Unit unit = unitService.getUnitById(id);
        if (unit != null) {
            return ResponseEntity.ok(unit);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/textbook/{textbookId}")
    public ResponseEntity<List<Unit>> getUnitsByTextbook(@PathVariable Long textbookId) {
        List<Unit> units = unitService.getUnitsByTextbook(textbookId);
        return ResponseEntity.ok(units);
    }
    
    @GetMapping("/{unitId}/progress/{userId}")
    public ResponseEntity<Map<String, Object>> getUnitProgress(@PathVariable Long unitId, @PathVariable Long userId) {
        Map<String, Object> progress = unitService.getUnitProgress(unitId, userId);
        return ResponseEntity.ok(progress);
    }
    
    @PostMapping
    public ResponseEntity<Unit> createUnit(@RequestBody Unit unit) {
        Unit createdUnit = unitService.createUnit(unit);
        return ResponseEntity.ok(createdUnit);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Unit> updateUnit(@PathVariable Long id, @RequestBody Unit unit) {
        unit.setId(id);
        Unit updatedUnit = unitService.updateUnit(unit);
        if (updatedUnit != null) {
            return ResponseEntity.ok(updatedUnit);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        boolean deleted = unitService.deleteUnit(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
