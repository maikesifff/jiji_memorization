package com.jiji.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置头像文件访问路径
        Path uploadPath = Paths.get("uploads/avatars");
        String uploadDir = uploadPath.toFile().getAbsolutePath();
        
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:" + uploadDir + "/");
        
        System.out.println("头像文件访问路径配置: " + uploadDir);
    }
}
