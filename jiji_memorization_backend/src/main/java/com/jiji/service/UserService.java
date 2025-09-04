package com.jiji.service;

import com.jiji.entity.User;
import com.jiji.entity.UserStatus;
import com.jiji.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
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
    
    // 分页获取所有活跃用户
    @Transactional(readOnly = true)
    public Page<User> getAllActiveUsersWithPagination(Pageable pageable) {
        return userRepository.findByStatus(UserStatus.ACTIVE, pageable);
    }
    
    // 分页搜索用户
    @Transactional(readOnly = true)
    public Page<User> searchUsersWithPagination(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable);
    }
    
    // 更新用户信息
    public User updateUser(User user) {
        // 不需要手动设置updatedAt，@PreUpdate会自动处理
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
            // 不需要手动设置updatedAt，@PreUpdate会自动处理
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
    
    // 上传用户头像
    public String uploadAvatar(User user, String fileName, String base64Data, String fileType) throws IOException {
        // 创建uploads/avatars目录
        String uploadDir = "uploads/avatars";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 删除用户之前的头像文件
        if (user.getAvatar() != null && user.getAvatar().startsWith("avatar_")) {
            try {
                Path oldAvatarPath = uploadPath.resolve(user.getAvatar());
                if (Files.exists(oldAvatarPath)) {
                    Files.delete(oldAvatarPath);
                    System.out.println("删除旧头像文件: " + user.getAvatar());
                }
            } catch (IOException e) {
                System.err.println("删除旧头像文件失败: " + e.getMessage());
                // 不抛出异常，继续处理新文件
            }
        }
        
        // 保存新头像文件
        Path filePath = uploadPath.resolve(fileName);
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);
        
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(fileBytes);
        }
        
        // 更新用户头像信息
        user.setAvatar(fileName);
        userRepository.save(user);
        
        System.out.println("头像文件保存成功: " + fileName);
        return fileName;
    }
}
