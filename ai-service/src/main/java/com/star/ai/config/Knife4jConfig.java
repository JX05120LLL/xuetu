package com.star.ai.config;

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
    public GroupedOpenApi aiApi() {
        return GroupedOpenApi.builder()
                .group("AI服务")
                .pathsToMatch("/chat/**", "/analysis/**", "/recommend/**",
                        "/knowledge/**", "/agent/**", "/openclaw/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("学途教育平台 - AI智能服务API")
                        .version("1.0.0")
                        .description("基于阿里通义千问的AI学习助手服务")
                        .contact(new Contact()
                                .name("star")
                                .email("star@xuetu.com")));
    }
}
