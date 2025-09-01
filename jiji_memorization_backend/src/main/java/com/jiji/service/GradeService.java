package com.jiji.service;

import com.jiji.entity.Grade;
import com.jiji.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {
    
    @Autowired
    private GradeRepository gradeRepository;
    
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }
    
    public Grade getGradeById(Long id) {
        Optional<Grade> grade = gradeRepository.findById(id);
        return grade.orElse(null);
    }
    
    public Grade createGrade(Grade grade) {
        return gradeRepository.save(grade);
    }
    
    public Grade updateGrade(Grade grade) {
        if (gradeRepository.existsById(grade.getId())) {
            return gradeRepository.save(grade);
        }
        return null;
    }
    
    public boolean deleteGrade(Long id) {
        if (gradeRepository.existsById(id)) {
            gradeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
