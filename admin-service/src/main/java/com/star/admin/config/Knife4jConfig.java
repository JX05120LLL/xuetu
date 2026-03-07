package com.star.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置
 * 
 * @author star
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("后台管理")
                .pathsToMatch("/role/**", "/permission/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("学途教育平台 - 后台管理API")
                        .version("1.0.0")
                        .description("角色权限管理、系统配置等后台管理功能")
                        .contact(new Contact()
                                .name("star")
                                .email("star@xuetu.com")));
    }
}
