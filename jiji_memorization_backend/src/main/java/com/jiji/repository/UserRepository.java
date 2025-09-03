package com.jiji.repository;

import com.jiji.entity.User;
import com.jiji.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 根据用户名查找用户
    Optional<User> findByUsername(String username);
    
    // 根据邮箱查找用户
    Optional<User> findByEmail(String email);
    
    // 根据用户名和状态查找用户
    Optional<User> findByUsernameAndStatus(String username, UserStatus status);
    
    // 根据邮箱和状态查找用户
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
    
    // 查找所有活跃用户
    List<User> findByStatus(UserStatus status);
    
    // 分页查找所有活跃用户
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    
    // 检查用户名是否存在
    boolean existsByUsername(String username);
    
    // 检查邮箱是否存在
    boolean existsByEmail(String email);
    
    // 根据用户名或邮箱查找用户（用于登录）
    @Query("SELECT u FROM User u WHERE (u.username = :usernameOrEmail OR u.email = :usernameOrEmail) AND u.status = :status")
    Optional<User> findByUsernameOrEmailAndStatus(@Param("usernameOrEmail") String usernameOrEmail, @Param("status") UserStatus status);
    
    // 查找用户数量
    long countByStatus(UserStatus status);
    
    // 搜索用户（用户名、邮箱、昵称）
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
