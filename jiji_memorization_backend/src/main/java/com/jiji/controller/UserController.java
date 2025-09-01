package com.jiji.controller;

import com.jiji.entity.User;
import com.jiji.entity.UserStatus;
import com.jiji.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // 创建用户
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");
            
            if (username == null || password == null || email == null) {
                return ResponseEntity.badRequest().body("用户名、密码和邮箱不能为空");
            }
            
            User user = userService.createUser(username, password, email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "用户创建成功");
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "nickname", user.getNickname(),
                "status", user.getStatus(),
                "createdAt", user.getCreatedAt()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 获取所有活跃用户
    @GetMapping
    public ResponseEntity<?> getAllActiveUsers() {
        try {
            List<User> users = userService.findAllActiveUsers();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "获取用户列表成功");
            response.put("users", users.stream().map(user -> Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "nickname", user.getNickname(),
                "status", user.getStatus(),
                "createdAt", user.getCreatedAt()
            )).toList());
            response.put("count", users.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "获取用户列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "获取用户信息成功");
                response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "nickname", user.getNickname(),
                    "avatar", user.getAvatar(),
                    "status", user.getStatus(),
                    "createdAt", user.getCreatedAt(),
                    "updatedAt", user.getUpdatedAt(),
                    "lastLoginAt", user.getLastLoginAt()
                ));
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "获取用户信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 根据用户名查找用户
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "获取用户信息成功");
                response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "nickname", user.getNickname(),
                    "status", user.getStatus(),
                    "createdAt", user.getCreatedAt()
                ));
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "获取用户信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 检查用户名是否存在
    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsernameExists(@PathVariable String username) {
        try {
            boolean exists = userService.existsByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("exists", exists);
            response.put("message", exists ? "用户名已存在" : "用户名可用");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "检查用户名失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 检查邮箱是否存在
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmailExists(@PathVariable String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("exists", exists);
            response.put("message", exists ? "邮箱已存在" : "邮箱可用");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "检查邮箱失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 获取用户统计信息
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats() {
        try {
            long activeUserCount = userService.countActiveUsers();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "获取用户统计信息成功");
            response.put("stats", Map.of(
                "activeUserCount", activeUserCount
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "获取用户统计信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
