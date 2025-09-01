package com.jiji.repository;

import com.jiji.entity.UnitWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitWordRepository extends JpaRepository<UnitWord, Long> {
    
    @Query("SELECT COUNT(uw) FROM UnitWord uw WHERE uw.unitId = :unitId")
    Long countByUnitId(@Param("unitId") Long unitId);
}
