package com.jiji.repository;

import com.jiji.entity.UnitWord;
import com.jiji.dto.UnitWordDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitWordRepository extends JpaRepository<UnitWord, Long> {
    
    // 根据单元ID查找关联
    List<UnitWord> findByUnitId(Long unitId);
    
    // 根据单元ID分页查找关联
    Page<UnitWord> findByUnitId(Long unitId, Pageable pageable);
    
    // 根据单词ID查找关联
    List<UnitWord> findByWordId(Long wordId);
    
    // 检查单元和单词是否已关联
    boolean existsByUnitIdAndWordId(Long unitId, Long wordId);
    
    @Query("SELECT COUNT(uw) FROM UnitWord uw WHERE uw.unitId = :unitId")
    Long countByUnitId(@Param("unitId") Long unitId);
    
    // 获取所有单元单词关联（包含单元、教材、单词信息）
    @Query("SELECT new com.jiji.dto.UnitWordDTO(uw.id, uw.unitId, u.name, u.textbookId, t.name, t.grade, t.publisher, uw.wordId, w.wordText, COALESCE(w.americanPhonetic, w.britishPhonetic), uw.createdAt) " +
           "FROM UnitWord uw " +
           "JOIN Unit u ON uw.unitId = u.id " +
           "JOIN Word w ON uw.wordId = w.id " +
           "JOIN Textbook t ON u.textbookId = t.id")
    Page<UnitWordDTO> findAllUnitWordsWithDetails(Pageable pageable);
    
    // 根据单元ID获取单元单词关联（包含单元、教材、单词信息）
    @Query("SELECT new com.jiji.dto.UnitWordDTO(uw.id, uw.unitId, u.name, u.textbookId, t.name, t.grade, t.publisher, uw.wordId, w.wordText, COALESCE(w.americanPhonetic, w.britishPhonetic), uw.createdAt) " +
           "FROM UnitWord uw " +
           "JOIN Unit u ON uw.unitId = u.id " +
           "JOIN Word w ON uw.wordId = w.id " +
           "JOIN Textbook t ON u.textbookId = t.id " +
           "WHERE uw.unitId = :unitId")
    Page<UnitWordDTO> findUnitWordsByUnitIdWithDetails(@Param("unitId") Long unitId, Pageable pageable);
    
         // 搜索单元单词关联（通过单元名称和单词文本）
     @Query("SELECT new com.jiji.dto.UnitWordDTO(uw.id, uw.unitId, u.name, u.textbookId, t.name, t.grade, t.publisher, uw.wordId, w.wordText, COALESCE(w.americanPhonetic, w.britishPhonetic), uw.createdAt) " +
            "FROM UnitWord uw " +
            "JOIN Unit u ON uw.unitId = u.id " +
            "JOIN Word w ON uw.wordId = w.id " +
            "JOIN Textbook t ON u.textbookId = t.id " +
            "WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(w.wordText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
     Page<UnitWordDTO> searchUnitWords(@Param("keyword") String keyword, Pageable pageable);
    
    // 根据单元ID搜索单元单词关联（通过单词文本）
    @Query("SELECT uw FROM UnitWord uw " +
           "JOIN Word w ON uw.wordId = w.id " +
           "WHERE uw.unitId = :unitId AND " +
           "LOWER(w.wordText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<UnitWord> searchUnitWordsByUnitId(@Param("unitId") Long unitId, @Param("keyword") String keyword, Pageable pageable);
    
    // 根据单元ID搜索单元单词关联（通过单词文本，返回详细信息）
    @Query("SELECT new com.jiji.dto.UnitWordDTO(uw.id, uw.unitId, u.name, u.textbookId, t.name, t.grade, t.publisher, uw.wordId, w.wordText, COALESCE(w.americanPhonetic, w.britishPhonetic), uw.createdAt) " +
           "FROM UnitWord uw " +
           "JOIN Unit u ON uw.unitId = u.id " +
           "JOIN Word w ON uw.wordId = w.id " +
           "JOIN Textbook t ON u.textbookId = t.id " +
           "WHERE uw.unitId = :unitId AND " +
           "LOWER(w.wordText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<UnitWordDTO> searchUnitWordsByUnitIdWithDetails(@Param("unitId") Long unitId, @Param("keyword") String keyword, Pageable pageable);
}
