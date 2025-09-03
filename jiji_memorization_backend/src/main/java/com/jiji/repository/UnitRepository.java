package com.jiji.repository;

import com.jiji.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    
    // 根据教材ID查找单元，按创建时间排序
    List<Unit> findByTextbookIdOrderByCreatedAtAsc(Long textbookId);
    
    // 搜索单元（名称、描述）
    @Query("SELECT u FROM Unit u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Unit> searchUnits(@Param("keyword") String keyword, Pageable pageable);
    
    // 检查同一教材内是否有重复的单元名
    boolean existsByTextbookIdAndName(Long textbookId, String name);
}
