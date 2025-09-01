package com.jiji.service;

import com.jiji.entity.User;
import com.jiji.entity.UserStatus;
import com.jiji.repository.UserRepository;
import com.jiji.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public Map<String, Object> login(String usernameOrEmail, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查找用户（支持用户名或邮箱登录）
            Optional<User> userOpt = userRepository.findByUsernameOrEmailAndStatus(usernameOrEmail, UserStatus.ACTIVE);
            
            if (userOpt.isEmpty()) {
                result.put("status", "error");
                result.put("message", "用户名或密码错误");
                return result;
            }
            
            User user = userOpt.get();
            
            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                result.put("status", "error");
                result.put("message", "用户名或密码错误");
                return result;
            }
            
            // 生成JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            
            // 更新最后登录时间
            userRepository.save(user);
            
            // 构建用户信息（不包含密码）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("status", user.getStatus());
            userInfo.put("createdAt", user.getCreatedAt());
            
            result.put("status", "success");
            result.put("message", "登录成功");
            result.put("token", token);
            result.put("user", userInfo);
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "登录失败：" + e.getMessage());
        }
        
        return result;
    }
    
    public Map<String, Object> register(String username, String password, String email) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(username)) {
                result.put("status", "error");
                result.put("message", "用户名已存在");
                return result;
            }
            
            // 检查邮箱是否已存在
            if (userRepository.existsByEmail(email)) {
                result.put("status", "error");
                result.put("message", "邮箱已存在");
                return result;
            }
            
            // 加密密码
            String encodedPassword = passwordEncoder.encode(password);
            
            // 创建新用户
            User newUser = new User(username, encodedPassword, email);
            newUser.setStatus(UserStatus.ACTIVE);
            
            User savedUser = userRepository.save(newUser);
            
            // 生成JWT token
            String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getId());
            
            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", savedUser.getId());
            userInfo.put("username", savedUser.getUsername());
            userInfo.put("email", savedUser.getEmail());
            userInfo.put("nickname", savedUser.getNickname());
            userInfo.put("avatar", savedUser.getAvatar());
            userInfo.put("status", savedUser.getStatus());
            userInfo.put("createdAt", savedUser.getCreatedAt());
            
            result.put("status", "success");
            result.put("message", "注册成功");
            result.put("token", token);
            result.put("user", userInfo);
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "注册失败：" + e.getMessage());
        }
        
        return result;
    }
    
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }
    
    public Long getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }
}
