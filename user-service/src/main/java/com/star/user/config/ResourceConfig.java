package com.star.user.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源配置
 * 配置文件上传目录的访问映射
 * 
 * @author star
 */
@Slf4j
@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 配置头像访问路径映射
        // 开发环境访问路径：http://localhost:8088/uploads/avatars/2025/01/15/xxx.jpg
        // 生产环境访问路径：http://8.141.106.92/media/UserAvatar/2025/01/15/xxx.jpg
        
        // 本地开发环境映射
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:" + uploadPath + "/");
        
        // 生产环境映射（兼容性）
        registry.addResourceHandler("/media/UserAvatar/**")
                .addResourceLocations("file:" + uploadPath + "/");
        
        log.info("静态资源映射配置成功:");
        log.info("  - /uploads/avatars/** -> file:{}/", uploadPath);
        log.info("  - /media/UserAvatar/** -> file:{}/", uploadPath);
    }
}
