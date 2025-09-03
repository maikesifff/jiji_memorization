package com.jiji.repository;

import com.jiji.entity.Meaning;
import com.jiji.dto.MeaningDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeaningRepository extends JpaRepository<Meaning, Long> {

    // 根据单词ID查找释义
    List<Meaning> findByWordId(Long wordId);
    
    // 根据词性查找释义
    List<Meaning> findByPos(String pos);
    
    // 根据单词ID和词性查找释义
    List<Meaning> findByWordIdAndPos(Long wordId, String pos);
    
    // 分页搜索释义（在内容中搜索）
    Page<Meaning> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
    
    // 搜索释义（在内容中搜索，不分页）
    List<Meaning> findByContentContainingIgnoreCase(String keyword);
    
    // 根据关键词搜索（使用自定义查询，在内容和词性中搜索）
    @Query("SELECT m FROM Meaning m WHERE m.content LIKE %:keyword% OR m.pos LIKE %:keyword%")
    List<Meaning> findByKeyword(@Param("keyword") String keyword);
    
    // 根据单词ID和关键词搜索释义
    @Query("SELECT m FROM Meaning m WHERE m.wordId = :wordId AND (m.content LIKE %:keyword% OR m.pos LIKE %:keyword%)")
    List<Meaning> findByWordIdAndKeyword(@Param("wordId") Long wordId, @Param("keyword") String keyword);
    
    // 分页获取所有释义，包含单词信息
    @Query("SELECT new com.jiji.dto.MeaningDTO(m.id, m.wordId, w.wordText, m.pos, m.content, m.createdAt, m.updatedAt) " +
           "FROM Meaning m JOIN Word w ON m.wordId = w.id")
    Page<MeaningDTO> findAllWithWordInfo(Pageable pageable);
    
    // 分页搜索释义，包含单词信息
    @Query("SELECT new com.jiji.dto.MeaningDTO(m.id, m.wordId, w.wordText, m.pos, m.content, m.createdAt, m.updatedAt) " +
           "FROM Meaning m JOIN Word w ON m.wordId = w.id " +
           "WHERE LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.pos) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(w.wordText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<MeaningDTO> searchMeaningsWithWordInfo(@Param("keyword") String keyword, Pageable pageable);
}
