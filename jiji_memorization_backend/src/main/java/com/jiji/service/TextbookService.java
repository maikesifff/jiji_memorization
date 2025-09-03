package com.jiji.service;

import com.jiji.entity.Textbook;
import com.jiji.repository.TextbookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TextbookService {
    
    @Autowired
    private TextbookRepository textbookRepository;
    
    // 获取所有教材
    public List<Textbook> getAllTextbooks() {
        return textbookRepository.findAll();
    }
    
    // 分页获取所有教材
    public Page<Textbook> getAllTextbooksWithPagination(Pageable pageable) {
        return textbookRepository.findAll(pageable);
    }
    
    // 分页搜索教材
    public Page<Textbook> searchTextbooksWithPagination(String keyword, Pageable pageable) {
        return textbookRepository.searchTextbooks(keyword, pageable);
    }
    
    // 根据ID获取教材
    public Optional<Textbook> getTextbookById(Long id) {
        return textbookRepository.findById(id);
    }
    
    // 根据名称获取教材
    public Textbook getTextbookByName(String name) {
        return textbookRepository.findByName(name);
    }
    
    // 根据年级获取教材
    public List<Textbook> getTextbooksByGrade(String grade) {
        return textbookRepository.findByGrade(grade);
    }
    
    // 根据出版社获取教材
    public List<Textbook> getTextbooksByPublisher(String publisher) {
        return textbookRepository.findByPublisher(publisher);
    }
    
    // 创建教材
    public Textbook createTextbook(Textbook textbook) {
        return textbookRepository.save(textbook);
    }
    
    // 更新教材
    public Textbook updateTextbook(Long id, Textbook textbookDetails) {
        Optional<Textbook> textbookOpt = textbookRepository.findById(id);
        if (textbookOpt.isPresent()) {
            Textbook textbook = textbookOpt.get();
            textbook.setName(textbookDetails.getName());
            textbook.setGrade(textbookDetails.getGrade());
            textbook.setPublisher(textbookDetails.getPublisher());
            textbook.setDescription(textbookDetails.getDescription());
            return textbookRepository.save(textbook);
        }
        return null;
    }
    
    // 删除教材
    public boolean deleteTextbook(Long id) {
        if (textbookRepository.existsById(id)) {
            textbookRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // 检查教材名称是否存在
    public boolean existsByName(String name) {
        return textbookRepository.existsByName(name);
    }
}
