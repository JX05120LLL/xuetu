package com.star.course.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 课程服务 Knife4j API文档配置
 * 
 * @author star
 */
@Configuration
public class Knife4jConfig {

    /**
     * OpenAPI 3.0 配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(components())
                .addSecurityItem(securityRequirement());
    }

    /**
     * API信息配置
     */
    private Info apiInfo() {
        return new Info()
                .title("学途在线教育平台 - 课程服务API")
                .description("提供课程管理、分类管理、章节管理、标签管理等功能的API接口文档")
                .version("1.0.0")
                .contact(new Contact()
                        .name("star")
                        .email("star@xuetu.com")
                        .url("https://xuetu.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }

    /**
     * 安全组件配置
     */
    private Components components() {
        return new Components()
                .addSecuritySchemes("Bearer Token", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("请输入JWT Token，格式：Bearer {token}"));
    }

    /**
     * 安全要求配置
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("Bearer Token");
    }
}