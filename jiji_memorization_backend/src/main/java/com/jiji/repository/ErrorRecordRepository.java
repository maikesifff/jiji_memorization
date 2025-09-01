package com.jiji.repository;

import com.jiji.entity.ErrorRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorRecordRepository extends JpaRepository<ErrorRecord, Long> {
    
    @Query("SELECT COUNT(er) FROM ErrorRecord er WHERE er.unitWordId IN " +
           "(SELECT uw.id FROM UnitWord uw WHERE uw.unitId = :unitId) AND er.userId = :userId")
    Long countByUnitIdAndUserId(@Param("unitId") Long unitId, @Param("userId") Long userId);
    
    List<ErrorRecord> findByUserId(Long userId);
    
    List<ErrorRecord> findByUnitWordId(Long unitWordId);
}
