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
    
    @Query("SELECT COUNT(er) FROM ErrorRecord er WHERE er.unitWordId IN " +
           "(SELECT uw.id FROM UnitWord uw WHERE uw.unitId = :unitId) AND er.userId = :userId")
    Long countByUnitIdAndUserId(@Param("unitId") Long unitId, @Param("userId") Long userId);
    
    List<ErrorRecord> findByUserId(Long userId);
    
    List<ErrorRecord> findByUnitWordId(Long unitWordId);

    // 根据用户ID和单元ID获取错误记录
    @Query("SELECT er FROM ErrorRecord er " +
           "JOIN UnitWord uw ON er.unitWordId = uw.id " +
           "WHERE er.userId = :userId AND uw.unitId = :unitId")
    List<ErrorRecord> findByUserIdAndUnitId(Long userId, Long unitId);
    
    // 根据用户ID和单元单词ID查找错误记录
    Optional<ErrorRecord> findByUserIdAndUnitWordId(Long userId, Long unitWordId);
}
