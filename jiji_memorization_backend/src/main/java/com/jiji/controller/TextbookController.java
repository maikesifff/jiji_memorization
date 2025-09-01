package com.jiji.controller;

import com.jiji.entity.Textbook;
import com.jiji.service.TextbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/textbooks")
public class TextbookController {
    
    @Autowired
    private TextbookService textbookService;
    
    @GetMapping
    public ResponseEntity<List<Textbook>> getAllTextbooks() {
        List<Textbook> textbooks = textbookService.getAllTextbooks();
        return ResponseEntity.ok(textbooks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Textbook> getTextbookById(@PathVariable Long id) {
        Textbook textbook = textbookService.getTextbookById(id);
        if (textbook != null) {
            return ResponseEntity.ok(textbook);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/grade/{gradeId}")
    public ResponseEntity<List<Textbook>> getTextbooksByGrade(@PathVariable Long gradeId) {
        List<Textbook> textbooks = textbookService.getTextbooksByGrade(gradeId);
        return ResponseEntity.ok(textbooks);
    }
    
    @PostMapping
    public ResponseEntity<Textbook> createTextbook(@RequestBody Textbook textbook) {
        Textbook createdTextbook = textbookService.createTextbook(textbook);
        return ResponseEntity.ok(createdTextbook);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Textbook> updateTextbook(@PathVariable Long id, @RequestBody Textbook textbook) {
        textbook.setId(id);
        Textbook updatedTextbook = textbookService.updateTextbook(textbook);
        if (updatedTextbook != null) {
            return ResponseEntity.ok(updatedTextbook);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTextbook(@PathVariable Long id) {
        boolean deleted = textbookService.deleteTextbook(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
