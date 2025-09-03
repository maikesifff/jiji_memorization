package com.jiji.repository;

import com.jiji.entity.Phrase;
import com.jiji.dto.PhraseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhraseRepository extends JpaRepository<Phrase, Long> {
    
    // 根据单词ID查找短语
    List<Phrase> findByWordId(Long wordId);
    
    // 根据短语内容查找
    List<Phrase> findByPhraseText(String phraseText);
    
    // 根据翻译查找短语
    List<Phrase> findByTranslation(String translation);
    
    // 模糊搜索短语内容或翻译
    @Query("SELECT p FROM Phrase p WHERE p.phraseText LIKE %:keyword% OR p.translation LIKE %:keyword%")
    List<Phrase> findByKeyword(@Param("keyword") String keyword);
    
    // 根据单词ID搜索短语
    @Query("SELECT p FROM Phrase p WHERE p.wordId = :wordId AND (p.phraseText LIKE %:keyword% OR p.translation LIKE %:keyword%)")
    List<Phrase> findByWordIdAndKeyword(@Param("wordId") Long wordId, @Param("keyword") String keyword);
    
    // 分页获取所有短语，包含单词信息
    @Query("SELECT new com.jiji.dto.PhraseDTO(p.id, p.wordId, w.wordText, p.phraseText, p.translation, p.createdAt, p.updatedAt) " +
           "FROM Phrase p JOIN Word w ON p.wordId = w.id")
    Page<PhraseDTO> findAllWithWordInfo(Pageable pageable);
    
    // 分页搜索短语，包含单词信息
    @Query("SELECT new com.jiji.dto.PhraseDTO(p.id, p.wordId, w.wordText, p.phraseText, p.translation, p.createdAt, p.updatedAt) " +
           "FROM Phrase p JOIN Word w ON p.wordId = w.id " +
           "WHERE LOWER(p.phraseText) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.translation) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(w.wordText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<PhraseDTO> searchPhrasesWithWordInfo(@Param("keyword") String keyword, Pageable pageable);
}
