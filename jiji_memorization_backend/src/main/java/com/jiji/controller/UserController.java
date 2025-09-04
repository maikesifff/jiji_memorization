package com.jiji.controller;

import com.jiji.entity.User;
import com.jiji.service.UserService;
import com.jiji.util.PageResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    
    // 分页获取所有活跃用户
    @GetMapping
    public ResponseEntity<?> getAllActiveUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<User> userPage = userService.getAllActiveUsersWithPagination(pageable);
            
            return PageResponseUtil.createPageResponse(userPage, "获取用户列表成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("获取用户列表失败: " + e.getMessage());
        }
    }
    
    // 分页搜索用户
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<User> userPage = userService.searchUsersWithPagination(keyword, pageable);
            
            return PageResponseUtil.createPageResponse(userPage, "搜索用户成功");
        } catch (Exception e) {
            return PageResponseUtil.createErrorResponse("搜索用户失败: " + e.getMessage());
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
    
    
    // 上传用户头像
    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String fileName = request.get("fileName").toString();
            String fileData = request.get("fileData").toString();
            String fileType = request.get("fileType").toString();
            Long fileSize = Long.valueOf(request.get("fileSize").toString());
            
            // 验证文件类型（只允许图片）
            if (!fileType.startsWith("image/")) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "只允许上传图片文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证文件大小（2MB限制）
            if (fileSize > 2 * 1024 * 1024) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "文件大小不能超过2MB");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证文件名格式（必须以avatar_开头）
            if (!fileName.startsWith("avatar_")) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "文件名格式不正确");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 查找用户
            Optional<User> userOpt = userService.findById(userId);
            if (!userOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
            // 保存头像文件并更新用户信息
            String avatarPath = userService.uploadAvatar(userOpt.get(), fileName, fileData, fileType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "头像上传成功");
            response.put("avatarPath", avatarPath);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "头像上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 更新用户头像（保留原有方法作为备用）
    @PutMapping("/{id}/avatar")
    public ResponseEntity<?> updateUserAvatar(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String avatarPath = request.get("avatarPath");
            if (avatarPath == null || avatarPath.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "头像路径不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            Optional<User> userOpt = userService.findById(id);
            if (!userOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            user.setAvatar(avatarPath);
            User updatedUser = userService.updateUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "头像更新成功");
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", updatedUser.getId());
            userInfo.put("username", updatedUser.getUsername());
            userInfo.put("email", updatedUser.getEmail());
            userInfo.put("nickname", updatedUser.getNickname());
            userInfo.put("avatar", updatedUser.getAvatar());
            userInfo.put("status", updatedUser.getStatus());
            userInfo.put("createdAt", updatedUser.getCreatedAt());
            userInfo.put("updatedAt", updatedUser.getUpdatedAt());
            userInfo.put("lastLoginAt", updatedUser.getLastLoginAt());
            
            response.put("user", userInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "头像更新失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
