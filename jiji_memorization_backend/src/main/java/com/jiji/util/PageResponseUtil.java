package com.jiji.util;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class PageResponseUtil {

    /**
     * 创建标准的分页响应
     */
    public static <T> ResponseEntity<Map<String, Object>> createPageResponse(Page<T> page, String successMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("currentPage", page.getNumber());
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("size", page.getSize());
        response.put("hasNext", page.hasNext());
        response.put("hasPrevious", page.hasPrevious());
        response.put("message", successMessage);
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 创建错误响应
     */
    public static ResponseEntity<Map<String, Object>> createErrorResponse(String errorMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", errorMessage);
        errorResponse.put("status", "error");
        
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    /**
     * 创建成功响应（非分页）
     */
    public static ResponseEntity<Map<String, Object>> createSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("message", message);
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 创建计数响应
     */
    public static ResponseEntity<Map<String, Object>> createCountResponse(long count) {
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
}
