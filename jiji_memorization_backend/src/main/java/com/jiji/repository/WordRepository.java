package com.jiji.repository;

import com.jiji.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    // 根据单词文本查找
    Optional<Word> findByWordText(String wordText);
    
    // 检查单词是否存在
    boolean existsByWordText(String wordText);
    
    // 分页搜索单词（包含关键词）
    Page<Word> findByWordTextContainingIgnoreCase(String keyword, Pageable pageable);
    
    // 搜索单词（包含关键词，不分页）
    List<Word> findByWordTextContainingIgnoreCase(String keyword);
    
    // 根据音标查找
    List<Word> findByAmericanPhonetic(String americanPhonetic);
    List<Word> findByBritishPhonetic(String britishPhonetic);
    
    // 根据英式发音查找
    List<Word> findByPronUk(String pronUk);
    
    // 根据美式发音查找
    List<Word> findByPronUs(String pronUs);
    
    // 根据关键词搜索（使用自定义查询）
    @Query("SELECT w FROM Word w WHERE w.wordText LIKE %:keyword% OR w.americanPhonetic LIKE %:keyword% OR w.britishPhonetic LIKE %:keyword%")
    List<Word> findByKeyword(@Param("keyword") String keyword);
    
    // 根据时间范围查找
    List<Word> findByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
