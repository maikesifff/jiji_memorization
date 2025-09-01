package com.jiji.service;

import com.jiji.entity.Textbook;
import com.jiji.repository.TextbookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TextbookService {
    
    @Autowired
    private TextbookRepository textbookRepository;
    
    public List<Textbook> getAllTextbooks() {
        return textbookRepository.findAll();
    }
    
    public Textbook getTextbookById(Long id) {
        Optional<Textbook> textbook = textbookRepository.findById(id);
        return textbook.orElse(null);
    }
    
    public List<Textbook> getTextbooksByGrade(Long gradeId) {
        return textbookRepository.findByGradeId(gradeId);
    }
    
    public Textbook createTextbook(Textbook textbook) {
        return textbookRepository.save(textbook);
    }
    
    public Textbook updateTextbook(Textbook textbook) {
        if (textbookRepository.existsById(textbook.getId())) {
            return textbookRepository.save(textbook);
        }
        return null;
    }
    
    public boolean deleteTextbook(Long id) {
        if (textbookRepository.existsById(id)) {
            textbookRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
