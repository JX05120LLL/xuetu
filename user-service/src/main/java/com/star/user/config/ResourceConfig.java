package com.star.user.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
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

    @Value("${file.upload.path:/www/wwwroot/media/UserAvatar}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置头像访问路径映射
        // 访问路径：/media/UserAvatar/**
        // 实际路径：file:/www/wwwroot/media/UserAvatar/
        registry.addResourceHandler("/media/UserAvatar/**")
                .addResourceLocations("file:" + uploadPath + "/");
        
        log.info("静态资源映射配置成功: /media/UserAvatar/** -> file:{}/", uploadPath);
    }
}
