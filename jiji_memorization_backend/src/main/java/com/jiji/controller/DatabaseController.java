package com.jiji.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            result.put("status", "success");
            result.put("message", "Database connection successful");
            result.put("database", connection.getMetaData().getDatabaseProductName());
            result.put("version", connection.getMetaData().getDatabaseProductVersion());
            result.put("url", connection.getMetaData().getURL());
            
            // Test a simple query
            String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            result.put("currentDatabase", dbName);
            
        } catch (SQLException e) {
            result.put("status", "error");
            result.put("message", "Database connection failed: " + e.getMessage());
            result.put("error", e.toString());
        }
        
        return result;
    }
}
