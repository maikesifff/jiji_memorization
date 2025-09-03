package com.jiji.repository;

import com.jiji.entity.Textbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextbookRepository extends JpaRepository<Textbook, Long> {
    
    // 根据名称查找教材
    Textbook findByName(String name);
    
    // 根据名称检查是否存在
    boolean existsByName(String name);
    
    // 根据年级查找教材
    List<Textbook> findByGrade(String grade);
    
    // 根据出版社查找教材
    List<Textbook> findByPublisher(String publisher);
    
    // 搜索教材（名称、年级、出版社、描述）
    @Query("SELECT t FROM Textbook t WHERE " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.grade) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.publisher) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Textbook> searchTextbooks(@Param("keyword") String keyword, Pageable pageable);
}
