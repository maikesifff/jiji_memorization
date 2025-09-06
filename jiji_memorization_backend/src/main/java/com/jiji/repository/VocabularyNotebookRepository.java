package com.jiji.repository;

import com.jiji.entity.VocabularyNotebook;
import com.jiji.entity.VocabularyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyNotebookRepository extends JpaRepository<VocabularyNotebook, Long> {
    
    // 根据用户ID查找生词本
    List<VocabularyNotebook> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 根据用户ID查找生词本（分页）
    Page<VocabularyNotebook> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 根据用户ID和状态查找生词本
    List<VocabularyNotebook> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, VocabularyStatus status);
    
    // 根据用户ID和状态查找生词本（分页）
    Page<VocabularyNotebook> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, VocabularyStatus status, Pageable pageable);
    
    // 检查用户是否已经添加了某个单词到生词本
    boolean existsByUserIdAndWordId(Long userId, Long wordId);
    
    // 根据用户ID和单词ID查找生词本记录
    Optional<VocabularyNotebook> findByUserIdAndWordId(Long userId, Long wordId);
    
    // 统计用户生词本中的单词数量
    long countByUserId(Long userId);
    
    // 统计用户生词本中不同状态的单词数量
    long countByUserIdAndStatus(Long userId, VocabularyStatus status);
    
    // 查找需要复习的单词（根据最后复习时间）
    @Query("SELECT vn FROM VocabularyNotebook vn WHERE vn.userId = :userId AND " +
           "(vn.lastReviewedAt IS NULL OR vn.lastReviewedAt < :reviewTime) " +
           "ORDER BY vn.lastReviewedAt ASC NULLS FIRST")
    List<VocabularyNotebook> findWordsForReview(@Param("userId") Long userId, @Param("reviewTime") java.time.LocalDateTime reviewTime);
    
    // 根据难度等级查找单词
    List<VocabularyNotebook> findByUserIdAndDifficultyLevelOrderByCreatedAtDesc(Long userId, Integer difficultyLevel);
    
    // 删除用户的所有生词本记录
    void deleteByUserId(Long userId);
}
