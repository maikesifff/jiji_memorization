package com.jiji.repository;

import com.jiji.entity.ErrorRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ErrorRecordRepository extends JpaRepository<ErrorRecord, Long> {
    
    // 根据用户ID和单元单词ID查找记录
    Optional<ErrorRecord> findByUserIdAndUnitWordId(Long userId, Long unitWordId);
    
    // 根据用户ID查找所有记录
    List<ErrorRecord> findByUserId(Long userId);
    
    // 根据单元单词ID查找所有记录
    List<ErrorRecord> findByUnitWordId(Long unitWordId);
    
    // 根据用户ID和单元ID查找记录（通过JOIN查询）
    @Query("SELECT er FROM ErrorRecord er " +
           "JOIN UnitWord uw ON er.unitWordId = uw.id " +
           "WHERE er.userId = :userId AND uw.unitId = :unitId")
    List<ErrorRecord> findByUserIdAndUnitId(@Param("userId") Long userId, @Param("unitId") Long unitId);
    
    // 统计用户答对的单词数量（错误次数为0）
    @Query("SELECT COUNT(er) FROM ErrorRecord er WHERE er.userId = :userId AND er.errorCount = 0")
    Long countCorrectWordsByUserId(@Param("userId") Long userId);
    
    // 统计用户答错的单词数量（错误次数大于0）
    @Query("SELECT COUNT(er) FROM ErrorRecord er WHERE er.userId = :userId AND er.errorCount > 0")
    Long countIncorrectWordsByUserId(@Param("userId") Long userId);
    
    // 根据用户ID和单元ID统计答对的单词数量
    @Query("SELECT COUNT(er) FROM ErrorRecord er " +
           "JOIN UnitWord uw ON er.unitWordId = uw.id " +
           "WHERE er.userId = :userId AND uw.unitId = :unitId AND er.errorCount = 0")
    Long countCorrectWordsByUserIdAndUnitId(@Param("userId") Long userId, @Param("unitId") Long unitId);
    
    // 根据用户ID和单元ID统计答错的单词数量
    @Query("SELECT COUNT(er) FROM ErrorRecord er " +
           "JOIN UnitWord uw ON er.unitWordId = uw.id " +
           "WHERE er.userId = :userId AND uw.unitId = :unitId AND er.errorCount > 0")
    Long countIncorrectWordsByUserIdAndUnitId(@Param("userId") Long userId, @Param("unitId") Long unitId);
    
    // 检查用户是否已经答对过某个单词
    @Query("SELECT CASE WHEN COUNT(er) > 0 THEN true ELSE false END FROM ErrorRecord er " +
           "WHERE er.userId = :userId AND er.unitWordId = :unitWordId AND er.errorCount = 0")
    boolean isWordCorrectByUser(@Param("userId") Long userId, @Param("unitWordId") Long unitWordId);
    
    // 检查用户是否已经答错过某个单词
    @Query("SELECT CASE WHEN COUNT(er) > 0 THEN true ELSE false END FROM ErrorRecord er " +
           "WHERE er.userId = :userId AND er.unitWordId = :unitWordId AND er.errorCount > 0")
    boolean isWordIncorrectByUser(@Param("userId") Long userId, @Param("unitWordId") Long unitWordId);
}