package com.jiji.controller;

import com.jiji.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "用户名和密码不能为空"
            ));
        }
        
        Map<String, Object> result = authService.login(username, password);
        
        if ("success".equals(result.get("status"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String nickname = request.get("nickname");
        String password = request.get("password");
        String email = request.get("email");
        
        if (username == null || nickname == null || password == null || email == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "用户名、昵称、密码和邮箱不能为空"
            ));
        }
        
        Map<String, Object> result = authService.register(username, nickname, password, email);
        
        if ("success".equals(result.get("status"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "无效的认证头"
            ));
        }
        
        String token = authHeader.substring(7);
        boolean isValid = authService.validateToken(token);
        
        if (isValid) {
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Token有效",
                "valid", true
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Token无效或已过期",
                "valid", false
            ));
        }
    }
}
