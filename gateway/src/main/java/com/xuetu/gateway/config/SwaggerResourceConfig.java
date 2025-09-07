package com.xuetu.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Swagger资源配置
 * 处理Knife4j静态资源和路由
 * 
 * @author star
 */
@Slf4j
@Configuration
public class SwaggerResourceConfig {

    /**
     * 配置Knife4j静态资源路由
     */
    @Bean
    public RouterFunction<ServerResponse> swaggerRouterFunction() {
        log.info("配置Knife4j静态资源路由");
        
        return RouterFunctions.resources("/doc.html", new ClassPathResource("META-INF/resources/doc.html"))
                .and(RouterFunctions.resources("/webjars/**", new ClassPathResource("META-INF/resources/webjars/")))
                .and(RouterFunctions.resources("/favicon.ico", new ClassPathResource("META-INF/resources/favicon.ico")))
                .and(RouterFunctions.resources("/swagger-resources", new ClassPathResource("META-INF/resources/")))
                .and(RouterFunctions.resources("/v3/api-docs", new ClassPathResource("META-INF/resources/")));
    }
}