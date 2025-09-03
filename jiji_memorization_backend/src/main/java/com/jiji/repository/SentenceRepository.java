package com.jiji.repository;

import com.jiji.entity.Sentence;
import com.jiji.dto.SentenceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {
    
    // 根据单词ID查找句子
    List<Sentence> findByWordId(Long wordId);
    
    // 根据句子内容查找
    List<Sentence> findBySentenceText(String sentenceText);
    
    // 根据翻译查找句子
    List<Sentence> findByTranslation(String translation);
    
    // 模糊搜索句子内容或翻译
    @Query("SELECT s FROM Sentence s WHERE s.sentenceText LIKE %:keyword% OR s.translation LIKE %:keyword%")
    List<Sentence> findByKeyword(@Param("keyword") String keyword);
    
    // 根据单词ID搜索句子
    @Query("SELECT s FROM Sentence s WHERE s.wordId = :wordId AND (s.sentenceText LIKE %:keyword% OR s.translation LIKE %:keyword%)")
    List<Sentence> findByWordIdAndKeyword(@Param("wordId") Long wordId, @Param("keyword") String keyword);
    
    // 分页获取所有句子，包含单词信息
    @Query("SELECT new com.jiji.dto.SentenceDTO(s.id, s.wordId, w.wordText, s.sentenceText, s.translation, s.createdAt, s.updatedAt) " +
           "FROM Sentence s JOIN Word w ON s.wordId = w.id")
    Page<SentenceDTO> findAllWithWordInfo(Pageable pageable);
    
    // 分页搜索句子，包含单词信息
    @Query("SELECT new com.jiji.dto.SentenceDTO(s.id, s.wordId, w.wordText, s.sentenceText, s.translation, s.createdAt, s.updatedAt) " +
           "FROM Sentence s JOIN Word w ON s.wordId = w.id " +
           "WHERE LOWER(s.sentenceText) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.translation) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(w.wordText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SentenceDTO> searchSentencesWithWordInfo(@Param("keyword") String keyword, Pageable pageable);
}
