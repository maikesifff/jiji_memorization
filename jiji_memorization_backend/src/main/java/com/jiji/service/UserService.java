package com.jiji.service;

import com.jiji.entity.User;
import com.jiji.entity.UserStatus;
import com.jiji.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // 创建新用户
    public User createUser(String username, String password, String email) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在: " + username);
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在: " + email);
        }
        
        User user = new User(username, password, email);
        return userRepository.save(user);
    }
    
    // 根据ID查找用户
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    // 根据用户名查找用户
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // 根据邮箱查找用户
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // 根据用户名或邮箱查找活跃用户（用于登录）
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmailAndStatus(usernameOrEmail, UserStatus.ACTIVE);
    }
    
    // 获取所有活跃用户
    @Transactional(readOnly = true)
    public List<User> findAllActiveUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE);
    }
    
    // 更新用户信息
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    // 更新用户最后登录时间
    public void updateLastLoginTime(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }
    
    // 更新用户状态
    public void updateUserStatus(Long userId, UserStatus status) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(status);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }
    
    // 删除用户（软删除）
    public void deleteUser(Long userId) {
        updateUserStatus(userId, UserStatus.DELETED);
    }
    
    // 检查用户名是否存在
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    // 检查邮箱是否存在
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // 获取活跃用户数量
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countByStatus(UserStatus.ACTIVE);
    }
}
